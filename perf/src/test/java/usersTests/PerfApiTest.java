package usersTests;

import api.PerfApi;
import com.github.javafaker.Faker;
import database.PerfDbExecutor;
import io.qameta.allure.Owner;
import io.qameta.allure.Step;
import models.PersonInfo;
import org.jooq.generated.tables.records.CarRecord;
import org.jooq.generated.tables.records.PersonRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import static encryption.PerfUserRole.PerfDBReader;
import static encryption.UserCryptographer.getUser;
import static io.qameta.allure.Allure.step;
import static java.lang.Integer.MAX_VALUE;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class PerfApiTest {
    private final PerfDbExecutor dbExecutor = new PerfDbExecutor(getUser(PerfDBReader));
    private final PerfApi perfApi = new PerfApi();

    @DisplayName("Проверка добавления пользователя")
    @Owner("Калашников Владислав Александрович")
    @Test
    void addUserCheck() {
        Random random = new Random();
        Faker faker = new Faker();
        long maxPersonIdBefore = dbExecutor.getMaxPersonId();

        PersonInfo personRq = new PersonInfo()
                .setFirstName(faker.name().firstName())
                .setSecondName(faker.name().lastName())
                .setSexFromBool(faker.bool().bool())
                .setAge(faker.number().numberBetween(18, 100))
                .setMoney(new BigDecimal(random.nextInt(MAX_VALUE) + "." + random.nextInt(100)));

        PersonInfo personRs = perfApi.addUser(personRq)
                .shouldBeStatusCode(201)
                .parseBodyTo(PersonInfo.class);
        step("Проверить что Id в ответе больше, чем максимальный Id в БД до отправки запроса", () ->
                assertTrue(maxPersonIdBefore < personRs.getId(),
                        "Id в ответе должен быть больше, чем " + maxPersonIdBefore));
        checkNewPerson(personRq, dbExecutor.getPersonRecordById(personRs.getId()));
    }

    @DisplayName("Проверка покупки автомобиля человеком без дома")
    @Owner("Калашников Владислав Александрович")
    @Test
    void buyCarWithoutHouseCheck() {
        PersonRecord person = getRichestPerson(dbExecutor.getPersonRecords().stream()
                .filter(p -> p.getHouseId() == null)
                .collect(toList()));
        CarRecord car = getCheapestCar(dbExecutor.getCarsNotOwned(person.getId()));
        assumeTrue(person.getMoney().compareTo(car.getPrice()) >= 0,
                "В базе данных нет пользователя без дома, у которого хватает денег " +
                        "хотя бы на самую дешевую, не принадлежащую ему машину");

        String errorMessage = "ERROR: Person with id = " + person.getId() + " does not have free parking place for car";
        perfApi.buyCar(person.getId(), car.getId())
                .shouldBeStatusCode(412)
                .shouldContainText(errorMessage);

        assertEquals(person.getMoney(), dbExecutor.getPersonRecordById(person.getId()).getMoney(),
                "Количество денег должно оставаться прежним: " + person.getMoney());
        assertEquals(car.getPersonId(), dbExecutor.getCarRecordById(car.getId()).getPersonId(),
                "У автомобиля не должен меняться владелец: " + car.getPersonId());
    }

    @DisplayName("Проверка покупки не электрического автомобиля, в доме со свободным парковочным местом")
    @Owner("Калашников Владислав Александрович")
    @Test
    void buyNotElectricCarCheck() {
        PersonRecord person = getRichestPerson(dbExecutor.getUsersInHousesWithFreePlaces());
        CarRecord car = getCheapestCarNotEngineTypeId(dbExecutor.getCarsNotOwned(person.getId()), 5);
        assumeTrue(person.getMoney().compareTo(car.getPrice()) >= 0,
                "В базе данных нет пользователя c заданным условием, у которого хватает денег" +
                        "хотя бы на самую дешевую не электрическую машину");
        perfApi.buyCar(person.getId(), car.getId())
                .shouldBeStatusCode(200)
                .shouldContainText("\"id\":" + person.getId());
        step("Проверить что у автомобиля изменился владелец", () ->
                assertEquals(person.getId(), dbExecutor.getCarRecordById(car.getId()).getPersonId(),
                        "У автомобиля должен измениться владелец"));
        step("Проверить что количество денег человека уменьшилось на цену автомобиля", () -> {
            BigDecimal expectedMoney = person.getMoney().subtract(car.getPrice());
            BigDecimal actualMoney = dbExecutor.getPersonRecordById(person.getId()).getMoney();

            assertEquals(0, expectedMoney.compareTo(actualMoney),
                    "Количество денег должно было уменьшиться на цену автомобиля\n" +
                            "Expected: " + expectedMoney + "\nActual: " + actualMoney);
        });
    }

    @DisplayName("Проверка покупки не электрического автомобиля, " +
            "человеком, живущим в доме без свободных мест, у которого количество машин меньше, " +
            "чем суммарное количество парковочных мест в этом доме " +
            "(Суммарное количество машин всех людей живущих в доме, " +
            "не должно превышать общего количества парковочных мест в этом доме)")
    @Owner("Калашников Владислав Александрович")
    @Test
    void buyNotElectricCarWithoutFreePlacesInHouse() {
        List<PersonRecord> personRecords = dbExecutor.getUsersWhereCountCarsLessSumPlaces();
        assumeTrue(!personRecords.isEmpty(),
                "Не удалось найти пользователей, живущих в доме без свободных парковочных мест");

        PersonRecord person = getRichestPerson(personRecords);
        CarRecord car = getCheapestCarNotEngineTypeId(dbExecutor
                .getCarsNotOwned(personsIdInOneHouse(person.getHouseId())), 5);
        assumeTrue(person.getMoney().compareTo(car.getPrice()) >= 0,
                "В базе данных нет пользователя c заданным условием, у которого хватает денег" +
                        "хотя бы на самую дешевую не электрическую машину");

        int countCarsInHouseBefore = dbExecutor.getCountCarsInHouse(person.getHouseId());
        step("Проверить код ответа и количество машин в доме ", () -> assertAll(
                () -> perfApi.buyCar(person.getId(), car.getId())
                        .shouldBeStatusCode(412),
                () -> assertEquals(countCarsInHouseBefore, dbExecutor.getCountCarsInHouse(person.getHouseId()),
                        "Количество машин в доме: " + countCarsInHouseBefore + ", не должно было измениться")));
    }

    @Step("Проверить, что данные новой записи в БД соответствуют отправленным в запросе")
    private void checkNewPerson(PersonInfo person, PersonRecord personRecord) {
        assertAll(
                () -> assertEquals(person.getFirstName(), personRecord.getFirstName(),
                        "Имя не соответствует отправленному"),
                () -> assertEquals(person.getSecondName(), personRecord.getSecondName(),
                        "Фамилия не соответствует отправленной"),
                () -> assertEquals(person.getAge(), personRecord.getAge(),
                        "Возраст не соответствует отправленному"),
                () -> assertEquals(0, person.getMoney().compareTo(personRecord.getMoney()),
                        "Количество денег не соответствует отправленному"),
                () -> assertEquals(person.getSexAsBool(), personRecord.getSex(),
                        "Пол не соответствует отправленному"));
    }

    @Step("Выбрать самого богатого человека из списка")
    private PersonRecord getRichestPerson(List<PersonRecord> persons) {
        return persons.stream()
                .filter(p -> p.getMoney() != null)
                .max(comparing(PersonRecord::getMoney))
                .orElseThrow(() -> new IllegalStateException("Не удалось найти самого богатого человека"));
    }

    @Step("Выбрать самый дешевый автомобиль из списка")
    private CarRecord getCheapestCar(List<CarRecord> cars) {
        return cars.stream()
                .filter(p -> p.getPrice() != null)
                .min(comparing(CarRecord::getPrice))
                .orElseThrow(() -> new IllegalStateException("Не удалось найти самую дешевую машину"));
    }

    @Step("Выбрать из списка самый дешевый автомобиль c типом двигателя отличным от указанного: {engineTypeId}")
    private CarRecord getCheapestCarNotEngineTypeId(List<CarRecord> cars, long engineTypeId) {
        return cars.stream()
                .filter(p -> p.getPrice() != null)
                .filter(c -> !c.getEngineTypeId().equals(engineTypeId))
                .min(comparing(CarRecord::getPrice))
                .orElseThrow(() -> new IllegalStateException("Не удалось найти самую дешевую машину"));
    }

    /**
     * Возвращает массив id всех человек живущих в указанном доме
     */
    private Long[] personsIdInOneHouse(long houseId) {
        return dbExecutor
                .getAllPersonsInHouse(houseId)
                .stream()
                .map(PersonRecord::getId)
                .toArray(Long[]::new);
    }
}
