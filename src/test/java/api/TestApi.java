package api;

import annotations.Column;
import models.Cars;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class TestApi {
    @Test
    void testApi() throws InvocationTargetException, InstantiationException, IllegalAccessException, SQLException, NoSuchMethodException {

        ResultSet resultSet = null;
        try (Connection myConnection = getConnection(
                "jdbc:postgresql://77.50.236.203:4832/pflb_trainingcenter",
                "pflb-at-read",
                "PflbQaTraining2354")) {

            Statement statement = myConnection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Car");

        } catch (SQLException e) {
            e.printStackTrace();
        }


        List<Cars> cars = mapper(Cars.class, resultSet);

        System.out.println(cars.get(0).toString());

//
//            Statement statement = myConnection.createStatement();
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM Car");
//resultSet.next();
//            System.out.println(resultSet.getString(2) );
//resultSet.close();
//
//


//        HttpClient client = HttpClient.newBuilder().build();
//        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://77.50.236.203:4880/users")).build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

//        System.out.println(response.statusCode());
//        System.out.println(response.body());
//        Assertions.assertEquals(response.body(),
//                "[{\"id\":1,\"firstName\":\"Vasiliy\",\"secondName\":\"Rubenstein\",\"age\":42,\"sex\":\"MALE\",\"money\":1000000.00}]", "Ошибка");
//
//
//        HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create("http://77.50.236.203:4880/user/1")).build();
//        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
//
//        System.out.println(response2.statusCode());
//        System.out.println(response2.body());
//        Assertions.assertEquals(response2.body(),
//                "{\"id\":1,\"firstName\":\"Vasiliy\",\"secondName\":\"Rubenstein\",\"age\":42,\"sex\":\"MALE\",\"money\":1000000.00}", "Ошибка");
//
//
//        HttpRequest request3 = HttpRequest.newBuilder().uri(URI.create("http://77.50.236.203:4880/cars")).build();
//        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
//
//        System.out.println(response3.statusCode());
//        System.out.println(response3.body());


    }

//    @Test
//    public void testSyncGet() throws IOException, InterruptedException {
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://www.baidu.com"))
//                .build();
//
//        HttpResponse<String> response =
//                client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        System.out.println(response.body());
//    }

    private <T> List<T> mapper(Class<T> clazz, ResultSet resultSet) throws InvocationTargetException, InstantiationException, IllegalAccessException, SQLException, NoSuchMethodException {
        List<T> list = new ArrayList<>();


        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        while (resultSet.next()) {

            T dto = clazz.getConstructor().newInstance();

            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);

                String name;
                if (column != null) {
                    name = column.name();
                } else {
                    name = field.getName();
                }
                try {

                    String value = resultSet.getString(name);
                    if (value != null)
                        field.set(dto, field.getType().getConstructor(String.class).newInstance(value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            list.add(dto);
        }
        resultSet.close();
        return list;
    }
}
