package api;

import io.qameta.allure.Owner;
import models.listPath.ContentObjectInfo;
import models.listPath.PrivateListPathRs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void login() {
        cloudMailApi.login(getUser(admin));
    }

    @DisplayName("Проверка списка файлов в корневом каталоге")
    @Owner("Калашников Владислав Александрович")
    @Test
    void homeTest() {
        Set<String> expectedFileNames = new HashSet<>() {{
            add("Долина реки.jpg");
            add("Полет.mp4");
            add("Чистая вода.jpg");
        }};

        Set<String> actualFileNames = cloudMailApi.privateList("/")
                .parseBodyTo(PrivateListPathRs.class)
                .getList().stream()
                .filter((obj) -> obj.type.equals("file"))
                .map(ContentObjectInfo::getName)
                .collect(toSet());

        step("Проверить что имена файлов в теле ответа соответствуют списку:" + expectedFileNames, () ->
                assertEquals(expectedFileNames, actualFileNames, "Список файлов не соответствует ожидаемому")
        );
    }

    @AfterEach
    void logout() {
        cloudMailApi.logout();
    }
}
