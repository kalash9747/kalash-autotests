package api;

import encryption.MailUserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static encryption.UserCryptographer.getUser;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomeDirectoryTest {
    private static final CloudMailApi cloudMailApi = new CloudMailApi();

    @BeforeEach
    void login() {
        System.out.println(cloudMailApi.login(getUser(MailUserRole.admin1)).statusCode());
    }

    @Test
    void homeTest() {

        List<String> fileNames = new ArrayList<>() {{
            add("спиннер.png");
            add("Полет.mp4");
        }};

        HttpResponse<String> response = cloudMailApi.home();
        fileNames.forEach(fileName ->
                step("Проверить что в теле ответа присутствует имя файла:" + fileName, () ->
                        assertTrue(response.body().contains("\"name\": \"" + fileName + "\""))));
    }
}
