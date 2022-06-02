package usersTests;

import api.PerfApi;
import database.PerfDbExecutor;
import io.qameta.allure.Owner;
import models.PersonRq;
import org.jooq.generated.tables.records.CarRecord;
import org.jooq.generated.tables.records.PersonRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static encryption.PerfUserRole.perfDBReader;
import static encryption.UserCryptographer.getUser;
import static io.qameta.allure.Allure.step;
import static java.math.BigDecimal.valueOf;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class PerfApiTest {
    private static final PerfDbExecutor dbExecutor = new PerfDbExecutor(getUser(perfDBReader));
    private static final PerfApi perfApi = new PerfApi();

    @DisplayName("Проверка добаления пользователя")
    @Owner("Калашников Владислав Александрович")
    @Test
    void addUserCheck() {
        PersonRecord person = new PersonRecord(null, 25, "Tony", valueOf(247000), "Stark", true, null);
        int countRecordsBefore = dbExecutor.getCountPersonRecords(person);
        perfApi.addUser(new PersonRq(person))
                .shouldBeStatusCode(201);
        step("Проверить, что в базе появился новый пользователь", () ->
                assertEquals(countRecordsBefore + 1, dbExecutor.getCountPersonRecords(person),
                        "В базе не найден новый пользователь"));
    }

    @DisplayName("Проверка покупки автомобиля человеком без дома")
    @Owner("Калашников Владислав Александрович")
    @Test
    void buyCarWithoutHouseCheck() {
        PersonRecord person = getRichestPerson();
        CarRecord car = getCheapestCar();
        assumeTrue(person.getMoney().compareTo(car.getPrice()) >= 0,
                "В базе данных нет пользователя без дома, у которого хватает денег, хотя бы на самую дешевую машину");

        String errorMessage = "ERROR: Person with id = " + person.getId() + " does not have free parking place for car";
        perfApi.buyCar(person.getId(), car.getId())
                .shouldBeStatusCode(412)
                .shouldContainText(errorMessage);

        assertEquals(person.getMoney(), dbExecutor.getPersonRecordById(person.getId()).getMoney(),
                "Количество денег должно оставаться прежним: " + person.getMoney());
        assertEquals(car.getPersonId(), dbExecutor.getCarRecordById(car.getId()).getPersonId(),
                "У автомобиля не должен меняться владелец: " + car.getPersonId());
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
     * Возвращает самый дешевый автомобиль
     */
    private CarRecord getCheapestCar() {
        return dbExecutor.getCarRecords().stream()
                .filter(c -> c.getPrice() != null)
                .min(comparing(CarRecord::getPrice))
                .orElseThrow(() -> new IllegalStateException("Не удалось найти самую дешевую машину"));
    }
}
