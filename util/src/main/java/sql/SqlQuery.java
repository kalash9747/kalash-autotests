package sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import encryption.User;

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
    private final String connectionUrl;
    //Логин пользователя для подключения к БД
    private final String userName;
    //Пароль
    private final String userPassword;
    //Запрос
    private String query;
    //Параметры для подстановки в запрос
    private List<Object> parameters;

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
     * Получить результат запроса в виде списка экземпляров указанного класса
     */
    public <T> List<T> getRowsAs(Class<T> clazz) {
        return getRows().stream()
                .map(row -> new ObjectMapper().convertValue(row, clazz))
                .collect(toList());
    }
}
