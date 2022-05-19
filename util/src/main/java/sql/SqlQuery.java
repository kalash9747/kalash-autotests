package sql;

import annotations.Column;
import com.fasterxml.jackson.databind.ObjectMapper;
import encryption.User;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.DriverManager.getConnection;
import static java.util.stream.Collectors.toList;

/**
 * Класс для отправки запросов к БД
 */
public class SqlQuery {
    //Адрес для подключения к БД
    private String connectionUrl;
    //Логин пользователя для подключения к БД
    private String userName;
    //Пароль
    private String userPassword;
    //Запрос
    private String query;
    //Параметры для подстановки в запрос
    private List<Object> parameters;

//    static {
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (ClassNotFoundException e) {
//            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
//            e.printStackTrace();
//        }
//    }

    public SqlQuery(User dbUser) {
        userName = dbUser.getLogin();
        userPassword = dbUser.getPassword();
        connectionUrl = dbUser.getUrl();
    }

    public SqlQuery addParameter(Object object) {
        if (parameters == null)
            parameters = new ArrayList<>();
        parameters.add(object);
        return this;
    }

    public SqlQuery setQuery(String query) {
        this.query = query;
        return this;
    }

    /**
     * Выполнить запрос в базу
     */
    public ResultSet executeQuery() {
        try (Connection connection = getConnection(connectionUrl, userName, userPassword)) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            if (parameters != null)
                for (int i = 0; i < parameters.size(); i++)
                    preparedStatement.setObject(i + 1, parameters.get(i));
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Не удалось подключиться к базе");
            return null;
        }
    }

    /**
     * Получить результат запроса в виде списка Map(k-название колонки,v-значение)
     */
    public List<Map<String, Object>> getRows() {
        try (ResultSet resultSet = executeQuery()) {
            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnsCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnsCount; i++)
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                rows.add(row);
            }
            return rows;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Не удалось выполнить запрос");
        }
        return null;
    }

    /**
     * Получить результат запроса в виде списка моделей
     */
    public <T> List<T> getRows(Class<T> clazz) {
        try (ResultSet resultSet = executeQuery()) {
            List<T> list = new ArrayList<>();
            Field[] fields = clazz.getDeclaredFields();

            while (resultSet.next()) {
                T dto = clazz.getConstructor().newInstance();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Column column = field.getAnnotation(Column.class);
                    String value = resultSet.getString((column == null) ? field.getName() : column.name());
                    if (value != null)
                        field.set(dto, field.getType().getConstructor(String.class).newInstance(value));
                }
                list.add(dto);
            }
            return list;
        } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("Не удалось выполнить запрос");
        }
        return null;
    }

    /**
     * Получить результат запроса в виде списка моделей
     */
    public <T> List<T> getRowsAs(Class<T> clazz) {
        return getRows().stream()
                .map(row -> new ObjectMapper().convertValue(row, clazz))
                .collect(toList());
    }
//
//    public static void main(String[] args) throws SQLException {
////        new SqlQuery(getUser(mailDBReader))
////                .setQuery("select * from file_type")
////                .getRowsAsListMaps().forEach(row -> row.forEach((k, v) -> System.out.println(k + "    " + v)));
//
//    String cookie = " p=1xsAAM6+IAAA; mrcu=527D616D6F3110412065E380545F; searchuid=6256850271628776424; tmr_lvid=80ac5bc3b19f72a90a1ec98c833a0e25; tmr_lvidTS=1641808314851; _ym_uid=1644599902939175911; _ym_d=1644599902; cto_bundle=qbzE619aYmU3ViUyQjRJMDJPQ1ZXZzlNcExuM3BadXB4WUVnSFRrTXRVQ2MlMkJZYyUyQkRBWUkxajVmNTlnTm9YZjZBTlNLUWM1dnVrMG5HTVFxYjIlMkYlMkZKRU8zJTJGeHlMQ01NYXVHWnRaTjFOQnpxSjg3NkUlMkJodGNOYjI5MEJNWU9aWW5WVXBGaTBPVCUyRnlYdmhtSW90eklIVnpWSDFyTzBBJTNEJTNE; OTVET-10061=92; OTVET-10063=75; OTVET-9607-shown=1; s_cp=skipNotificationsTo=1647529747701; OTVET-10530=45; _ga=GA1.3.1631838415.1641808315; ph_v_my-mail-ru=1; act=5c7c6d44b16d4315a677e98cd375d2e2; t=obLD1AAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAACAABQC0AcA; _gid=GA1.2.2091540955.1651762216; _ym_isad=1; _gcl_au=1.1.1926243112.1651762340; _gid=GA1.3.2091540955.1651762216; o=testtestov.20022000@mail.ru:768:gAE=.s|kalashtests@mail.ru:756:ACE=.s|kalash9747@vk.com:756:AAI=.s|kva.97@mail.ru:732:IAAC.s; s=octavius=1|fver=0|ww=1920|wh=929; onboarding-shown=1; sdcs=dnah63lIDSxYARWb; _ga=GA1.1.1631838415.1641808315; tmr_detect=0%7C1651825490497; Mpop=1651825507:404c497d455075021905000017031f051c054f6c5150445e05190401041d455c4343455c4a4c5e461f0505070703040709105d57515e1c4a4c:testtestov.20022000@mail.ru:; i=AQAMx3RiCgATAAhnInwAAZ0AAc4AAQkBARwBAR4BAR8BAUUBARsCAfACAcIEATgFATkFAeMFAR0GAdAHAVsIAe4IATwJAdMJAfEJAYQKAUMLAZALAXwMAX4MAYAMAT4NAbkOAXseAvUgAfYgAfEiAfYiAZICCAQBAQABkwIIWB1uAAEBAgECAgEHAgEKAgEPAgESAgEYAgFgBQFoBQF0BQF1BQF2BQGgBQGhBQGmBQGpBQF6BgHFCwHICwHJCwHLCwHMCwHOCwF0DQF3DQHxEQHiEgGiYwHcBAgEAQEAAeEECQEB4gQKBAEF0Ac6BQhMGagHAbYHAdEHAdMHAd8HAYIJAYUJAaoJAbUJAbcJAbgJAUgLAWQLAWULAYYLAZMLAcoLAcwLAeILAeMLATIMAUkMAaYMAd0MASAOAdYGCAQBAQABvQcIBAGCFQEpCQgKA8wLAc4LAecSAQ==; b=rUoQAKAEL4YAPzKINGt5ZiVjU0otccAuFbdsgg25x6MIS1HvCFfOowgDwo0L13WpC9HKjQv3UkoNAQAACBkPywpxf2YJsWyCDbbwvRzBVMGPcphdf0AFM8Jxz3E6/gEVTg/cLwMA; c=1dt0YgEAoHsTAAAkBhAACQAAQGbmLgPYjM9jAJlp0AwgM2OQAXzGizKADDZoBtB8QATvdZxXQRAUrRzhQIRHHR3hwHhRBhAA; mtrc=%7B%22mytrackerid%22%3A52864%2C%22tmr_lvid%22%3A%22undefined%22%7D; tmr_reqNum=569; _ga_6B7RC3QRCC=GS1.1.1651820846.4.1.1651825627.0; VID=2ACF7328Z-Y900000V0-D4o9:::78f3423-0-78f24dc-62f80a8:CAASEK75jk9QEWGAQhHKcPoa9esacKo0VOyVG3yvViUwuqG8XmE-lc5LCbf4FKqTSBfp9_l9_LEY3EDXHOg56cV-KRNkKDbJxlhdI9DqT9mEnuaIkYLy-iUbiXmFg-liyGMVsdcMmT25g_pBCRWra4KFVXistHupcV7eJ15O9A0Gg7_u8TA\n";
//    String[] cookies = cookie.split(";");
//        for (String c:cookies) {
//            System.out.print(c.split("=")[0]);
//            System.out.print("               =");
//            System.out.println(c.split("=")[1]);
//        }
//    }
}
