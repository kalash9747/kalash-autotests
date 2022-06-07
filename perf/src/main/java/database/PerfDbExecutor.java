package database;

import encryption.User;
import org.jooq.generated.tables.records.CarRecord;
import org.jooq.generated.tables.records.PersonRecord;
import sql.JooqQuery;

import java.util.List;

import static org.jooq.generated.Tables.CAR;
import static org.jooq.generated.Tables.PERSON;
import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.max;

/**
 * Исполнитель запросов к БД 'pflb_trainingcenter'
 */
public class PerfDbExecutor {
    private final JooqQuery jooqQuery;

    public PerfDbExecutor(User dbUser) {
        this.jooqQuery = new JooqQuery(dbUser);
    }


    /**
     * Получить всех пользователей из БД
     */
    public long getMaxPersonId() {
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(max(PERSON.ID)).from(PERSON).fetchAnyInto(Long.class));
    }

    /**
     * Получить всех пользователей из БД
     */
    public List<PersonRecord> getPersonRecords() {
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(asterisk()).from(PERSON)
                .fetchInto(PersonRecord.class));
    }

    /**
     * Получить все автомобили из БД
     */
    public List<CarRecord> getCarRecords() {
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(asterisk()).from(CAR)
                .fetchInto(CarRecord.class));
    }

    /**
     * Получить пользователя из БД по id
     */
    public PersonRecord getPersonRecordById(long id) {
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(asterisk()).from(PERSON)
                .where(PERSON.ID.eq(id))
                .fetchAnyInto(PersonRecord.class));
    }

    /**
     * Получить автомобиль из БД по id
     */
    public CarRecord getCarRecordById(long id) {
        return jooqQuery.executeQuery(dslContext -> dslContext
                .select(asterisk()).from(CAR)
                .where(CAR.ID.eq(id))
                .fetchAnyInto(CarRecord.class));
    }
}
