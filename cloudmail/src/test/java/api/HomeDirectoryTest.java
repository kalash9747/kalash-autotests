package api;

import encryption.User;
import io.qameta.allure.Owner;
import models.listPath.ContentObjectInfo;
import models.listPath.PrivateListPathRs;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static encryption.MailUserRole.admin;
import static encryption.UserCryptographer.getUser;
import static io.qameta.allure.Allure.step;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HomeDirectoryTest {
    private static final CloudMailApi cloudMailApi = new CloudMailApi();
    private static final User user = getUser(admin);
    private static HttpResponseFacade authResponse;

    @BeforeAll
    static void login() {
        authResponse = cloudMailApi.login(user);
    }

    @DisplayName("Проверка сожержания логина пользователя в теле ответа авторизации")
    @Owner("Калашников Владислав Александрович")
    @Test
    void containedLoginTest() {
        authResponse.shouldContainedText(user.getLogin());
    }

    @DisplayName("Проверка списка файлов в корневом каталоге")
    @Owner("Калашников Владислав Александрович")
    @Test
    void homeTest() {
        Set<String> expectedFileNames = new HashSet<>() {{
            add("River Valley.jpg");
            add("Полет.mp4");
            add("Чистая вода.jpg");
        }};

        Set<String> actualFileNames = cloudMailApi.privateList("/")
                .parseBodyTo(PrivateListPathRs.class)
                .getList().stream()
                .filter((obj) -> obj.getType().equals("file"))
                .map(ContentObjectInfo::getName)
                .collect(toSet());

        step("Проверить что имена файлов в теле ответа соответствуют списку:" + expectedFileNames, () ->
                assertEquals(expectedFileNames, actualFileNames, "Список файлов не соответствует ожидаемому")
        );
    }

    @AfterAll
    static void logout() {
        cloudMailApi.logout();
    }
}
