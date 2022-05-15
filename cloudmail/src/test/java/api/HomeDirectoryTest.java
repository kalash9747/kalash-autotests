package api;

import io.qameta.allure.Owner;
import models.FeedRs;
import models.FileInCloud;
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
            add("Берег.jpg");
            add("Горное озеро.jpg");
            add("Долина реки.jpg");
            add("На отдыхе.jpg");
            add("Полет.mp4");
            add("Чистая вода.jpg");
            add("спиннер.png");
        }};

        var actualFileNames = cloudMailApi.feed()
                .parseBodyTo(FeedRs.class)
                .getBody().getObjects()
                .stream().map(FileInCloud::getName)
                .collect(toSet());

        step("Проверить что в теле ответа присутствуют имена файлов:" + expectedFileNames, () ->
                assertEquals(expectedFileNames, actualFileNames,"Список файлов не соответствует ожидаемому")
        );
    }
}
