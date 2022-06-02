package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jooq.generated.tables.records.PersonRecord;

import java.math.BigDecimal;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static java.math.BigDecimal.valueOf;

/**
 * Сущность пользователя для отправки запросов к PerfApi
 */
public class PersonRq {
    @JsonInclude(NON_DEFAULT)
    public long id;
    public String firstName;
    public String secondName;
    public int age;
    public String sex;
    public BigDecimal money;

    public PersonRq() {
    }

    public PersonRq(PersonRecord personRecord) {
        this.firstName = personRecord.getFirstName();
        this.secondName = personRecord.getSecondName();
        this.age = personRecord.getAge();
        this.setSex(personRecord.getSex());
        this.money = personRecord.getMoney();
        if (personRecord.getId() != null)
            this.id = personRecord.getId();
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public PersonRq setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getSecondName() {
        return secondName;
    }

    public PersonRq setSecondName(String secondName) {
        this.secondName = secondName;
        return this;
    }

    public int getAge() {
        return age;
    }

    public PersonRq setAge(int age) {
        this.age = age;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public PersonRq setSex(boolean sex) {
        this.sex = sex ? "MALE" : "FEMALE";
        return this;
    }

    public double getMoney() {
        return money.doubleValue();
    }

    public PersonRq setMoney(double money) {
        this.money = valueOf(money);
        return this;
    }

    @Override
    public String toString() {
        return "PersonRq{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                ", money=" + money +
                '}';
    }
}
