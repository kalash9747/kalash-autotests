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
import java.util.Random;

import static encryption.PerfUserRole.PerfDBReader;
import static encryption.UserCryptographer.getUser;
import static io.qameta.allure.Allure.step;
import static java.lang.Integer.MAX_VALUE;
import static java.math.BigDecimal.valueOf;
import static java.util.Comparator.comparing;
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

        PersonInfo personRq = new PersonInfo(new PersonRecord(null, faker.number().numberBetween(18, 100),
                faker.name().firstName(), new BigDecimal(random.nextInt(MAX_VALUE) + "." + random.nextInt(100)),
                faker.name().lastName(), faker.bool().bool(), null));

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
        PersonRecord person = getRichestPerson();
        CarRecord car = getCheapestCarNotOwned(person);
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

    @Step("Проверить, что данные новой записи в БД соответствуют отправленным в запросе")
    private void checkNewPerson(PersonInfo person, PersonRecord personRecord) {
        assertAll(
                () -> assertEquals(person.getFirstName(), personRecord.getFirstName(),
                        "Имя не соответствует отправленному"),
                () -> assertEquals(person.getSecondName(), personRecord.getSecondName(),
                        "Фамилия не соответствует отправленной"),
                () -> assertEquals(person.getAge(), personRecord.getAge(),
                        "Возраст не соответствует отправленному"),
                () -> assertEquals(0, valueOf(person.getMoney()).compareTo(personRecord.getMoney()),
                        "Количество денег не соответствует отправленному"),
                () -> assertEquals(person.getSexAsBool(), personRecord.getSex(),
                        "Пол не соответствует отправленному"));
    }

    /**
     * Возвращает самого богатого пользователя
     */
    private PersonRecord getRichestPerson() {
        return dbExecutor.getPersonRecords().stream()
                .filter(p -> p.getHouseId() == null && p.getMoney() != null)
                .max(comparing(PersonRecord::getMoney))
                .orElseThrow(() -> new IllegalStateException("Не удалось найти самого богатого человека"));
    }

    /**
     * Возвращает самый дешевый автомобиль который не принадлежит указанному пользователю
     */
    private CarRecord getCheapestCarNotOwned(PersonRecord person) {
        return dbExecutor.getCarRecords().stream()
                .filter(c -> c.getPrice() != null)
                .filter(c -> !person.getId().equals(c.getPersonId()))
                .min(comparing(CarRecord::getPrice))
                .orElseThrow(() -> new IllegalStateException("Не удалось найти самую дешевую машину"));
    }
}
