package database;

import encryption.User;
import io.qameta.allure.Step;
import org.jooq.*;
import org.jooq.generated.tables.Car;
import org.jooq.generated.tables.House;
import org.jooq.generated.tables.ParkingPlace;
import org.jooq.generated.tables.Person;
import org.jooq.generated.tables.records.CarRecord;
import org.jooq.generated.tables.records.PersonRecord;
import sql.JooqQuery;

import java.util.List;

import static org.jooq.generated.Tables.CAR;
import static org.jooq.generated.Tables.PERSON;
import static org.jooq.impl.DSL.*;

/**
 * Исполнитель запросов к БД 'pflb_trainingcenter'
 */
public class PerfDbExecutor {
    private final JooqQuery jooqQuery;
    private final Person person = Person.PERSON.as("person");
    private final House house = House.HOUSE.as("house");
    private final Car car = Car.CAR.as("car");
    private final ParkingPlace parkingPlace = ParkingPlace.PARKING_PLACE.as("parkingPlace");

    public PerfDbExecutor(User dbUser) {
        this.jooqQuery = new JooqQuery(dbUser);
    }

    @Step("Получить максимальный Id пользователя в БД")
    public long getMaxPersonId() {
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(max(PERSON.ID)).from(PERSON).fetchAnyInto(Long.class));
    }

    @Step("Получить всех пользователей из БД")
    public List<PersonRecord> getPersonRecords() {
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(asterisk()).from(PERSON)
                .fetchInto(PersonRecord.class));
    }

    @Step("Получить из БД все автомобили, не принадлежащие personId: {personId}")
    public List<CarRecord> getCarsNotOwned(Long... personId) {
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(asterisk()).from(CAR)
                .where(CAR.PERSON_ID.notIn(personId))
                .fetchInto(CarRecord.class));
    }

    @Step("Получить пользователя из БД по id: {id}")
    public PersonRecord getPersonRecordById(long id) {
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(asterisk()).from(PERSON)
                .where(PERSON.ID.eq(id))
                .fetchAnyInto(PersonRecord.class));
    }

    @Step("Получить автомобиль из БД по id: {id}")
    public CarRecord getCarRecordById(long id) {
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(asterisk()).from(CAR)
                .where(CAR.ID.eq(id))
                .fetchAnyInto(CarRecord.class));
    }

    @Step("Получить всех пользователей из БД живущих в указанном доме: {houseId}")
    public List<PersonRecord> getAllPersonsInHouse(long houseId) {
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(asterisk()).from(PERSON)
                .where(PERSON.HOUSE_ID.eq(houseId))
                .fetchInto(PersonRecord.class));
    }

    @Step("Посчитать суммарное количество машин в указанном доме: {houseId}")
    public int getCountCarsInHouse(long houseId) {
        Table<Record3<Long, Integer, Integer>> hpc = getHousePlacesCarsTable();
        Field<Long> houseIdField = cast(hpc.field("id"), Long.class);
        Field<Integer> countCars = cast(hpc.field("countCars"), Integer.class);
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(countCars).from(hpc)
                .where(houseIdField.eq(houseId)).fetchAnyInto(Integer.class));
    }


    @Step("Получить пользователей, которые живут в домах со свободными парковочными местами")
    public List<PersonRecord> getUsersInHousesWithFreePlaces() {
        Table<Record> housePlacesCars = getHousePlacesCarsTable(true);
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(person.asterisk())
                .from(person.join(housePlacesCars)
                        .on(person.HOUSE_ID.in(housePlacesCars.field("id"))))
                .orderBy(person.HOUSE_ID)
                .fetchInto(PersonRecord.class));
    }

    @Step("Получить пользователей, которые живут в домах без свободных парковочных мест " +
            "и у которых количество машин(у каждого по отдельности) меньше, " +
            "чем суммарное количество парковочных в доме")
    public List<PersonRecord> getUsersWhereCountCarsLessSumPlaces() {
        Table<Record2<Long, Integer>> countUserCars = getCountUserCarsTable();
        Table<Record> housePlacesCars = getHousePlacesCarsTable(false);
        Field<Integer> countCars = cast(countUserCars.field("countCars"), Integer.class);
        Field<Integer> sumPlaces = cast(housePlacesCars.field("sumPlaces"), Integer.class);
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(person.asterisk())
                .from(person
                        .join(housePlacesCars)
                        .on(person.HOUSE_ID.in(housePlacesCars.field("id")))
                        .leftJoin(countUserCars)
                        .on(person.HOUSE_ID.in(countUserCars.field("carPersonId")))
                        .and(countCars.lt(sumPlaces)))
                .orderBy(person.HOUSE_ID)
                .fetchInto(PersonRecord.class));
    }

    /**
     * Возвращает таблицу пользователей(carPersonId) и количества их машин(countCars)
     */
    private Table<Record2<Long, Integer>> getCountUserCarsTable() {
        return select(car.PERSON_ID.as("carPersonId"), count(car.ID).as("countCars"))
                .from(car)
                .groupBy(car.PERSON_ID)
                .asTable();
    }

    /**
     * Возвращает таблицу домов(id) у которых есть/нет свободных парковочных мест,
     * суммарного кол-ва парковочных мест(sumPlaces) и суммарного кол-ва машин в доме (countCars)
     */
    private Table<Record> getHousePlacesCarsTable(boolean areThereFreePlaces) {
        Table<Record3<Long, Integer, Integer>> hpc = getHousePlacesCarsTable();
        Field<Integer> countCars = cast(hpc.field("countCars"), Integer.class);
        Field<Integer> sumPlaces = cast(hpc.field("sumPlaces"), Integer.class);
        return select(hpc.asterisk()).from(hpc)
                .where(areThereFreePlaces ? countCars.lt(sumPlaces) : countCars.eq(sumPlaces))
                .asTable();
    }

    /**
     * Возвращает таблицу домов(id), суммарного кол-ва парковочных мест(sumPlaces)
     * и суммарного кол-ва машин в доме (countCars)
     */
    private Table<Record3<Long, Integer, Integer>> getHousePlacesCarsTable() {
        Table<Record2<Long, Integer>> ppCount = getHouseSumPlacesTable();
        Field<Integer> ppcountSum = cast(ppCount.field("sum_places"), Integer.class);
        return select(house.ID.as("id"), count(car.ID).as("countCars"), ppcountSum.as("sumPlaces"))
                .from(person
                        .join(house).on(house.ID.eq(person.HOUSE_ID))
                        .join(car).on(person.ID.eq(car.PERSON_ID))
                        .join(ppCount).on(house.ID.eq(cast(ppCount.field("house_id"), Long.class))))
                .groupBy(house.ID, ppcountSum)
                .asTable();
    }

    /**
     * Возвращает таблицу домов(house_id) и суммарного количества парковочных мест(sumPlaces)
     */
    private Table<Record2<Long, Integer>> getHouseSumPlacesTable() {
        return select(parkingPlace.HOUSE_ID.as("house_id"),
                sum(parkingPlace.PLACES_COUNT).cast(Integer.class).as("sum_places"))
                .from(parkingPlace)
                .groupBy(parkingPlace.HOUSE_ID).asTable("ppCount");
    }
}