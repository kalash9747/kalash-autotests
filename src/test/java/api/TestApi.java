package api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestApi {
    @Test
    void testApi() throws  InterruptedException, IOException {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://77.50.236.203:4880/users")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
        Assertions.assertEquals(response.body(),
                "[{\"id\":1,\"firstName\":\"Vasiliy\",\"secondName\":\"Rubenstein\",\"age\":42,\"sex\":\"MALE\",\"money\":1000000.00}]", "Ошибка");


        HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create("http://77.50.236.203:4880/user/1")).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        System.out.println(response2.statusCode());
        System.out.println(response2.body());
        Assertions.assertEquals(response2.body(),
                "{\"id\":1,\"firstName\":\"Vasiliy\",\"secondName\":\"Rubenstein\",\"age\":42,\"sex\":\"MALE\",\"money\":1000000.00}", "Ошибка");


        HttpRequest request3 = HttpRequest.newBuilder().uri(URI.create("http://77.50.236.203:4880/cars")).build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        System.out.println(response3.statusCode());
        System.out.println(response3.body());


    }

    @Test
    public void testSyncGet() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.baidu.com"))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }
}
