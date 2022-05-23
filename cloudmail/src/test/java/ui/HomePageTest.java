package ui;

import encryption.User;
import io.qameta.allure.Owner;
import models.dbModels.CloudFileInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.HomePage;
import sql.SqlQuery;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;
import static encryption.MailUserRole.admin;
import static encryption.MailUserRole.mailDBReader;
import static encryption.UserCryptographer.getUser;
import static io.qameta.allure.Allure.step;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Authorization.login;

public class HomePageTest extends TestRunner {
    private static final User user = getUser(admin);
    private static HomePage homePage;
    private static CloudFileInfo fileFromDB;

    @BeforeEach
    void authorization() {
        homePage = login(user).userWidgetCheck(user);
        fileFromDB = getAllCloudFilesFromDB().get(0).setName(currentTimeMillis() + "");
    }

    @DisplayName("Проверка видимости файлов домашней страницы")
    @Owner("Калашников Владислав Александрович")
    @Test
    void homePageFilesCheck() {
        getAllCloudFilesFromDB().forEach(fileInfo ->
                homePage.cellContentVisible(fileInfo.getName(), fileInfo.getContentextension()));
    }

    @DisplayName("Проверка выгрузки файла")
    @Owner("Калашников Владислав Александрович")
    @Test
    void uploadFileCheck() {
        homePage.uploadFile(fileFromDB.toTempFile())
                .cellContentVisible(fileFromDB.getName(), fileFromDB.getContentextension())
                .removeFile(fileFromDB);
    }

    @DisplayName("Проверка загрузки файла и его соответствия раннее выгруженному")
    @Owner("Калашников Владислав Александрович")
    @Test
    void downloadFileCheck() {
        File fileFromCloud = homePage
                .uploadFile(fileFromDB.toTempFile())
                .downloadFile(fileFromDB);
        homePage.removeFile(fileFromDB);
        step("Проверить имя загруженного файла", () ->
                assertEquals(fileFromDB.getNameWithExt(), fileFromCloud.getName(),
                        "Имя файла не совпадает с выгруженным"));
        step("Проверить содержимое загруженного файла", () ->
                assertArrayEquals(fileFromDB.getContentbytes(), fileToByteArray(fileFromCloud),
                        "Содержимое файла не совпадает с выгруженным"));
    }

    /**
     * Считывает файл в массив байтов
     */
    private byte[] fileToByteArray(File file) {
        try {
            return readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось прочитать содержимое файла в массив байтов");
        }
        return null;
    }

    /**
     * Получает представление файл из базы данных
     */
    private static List<CloudFileInfo> getAllCloudFilesFromDB() {
        return new SqlQuery(getUser(mailDBReader))
                .setQuery("select fn.name, ft.contentextension, fc.contentbytes from  file_upload " +
                        "join file_name fn on fn.filename_id = file_upload.filename_id " +
                        "join file_content fc on fc.content_id = file_upload.content_id " +
                        "join file_type ft on ft.type_id = fc.type_id;")
                .getRowsAs(CloudFileInfo.class);
    }

    @AfterEach
    void closeDriver() {
        if (hasWebDriverStarted())
            closeWebDriver();
    }
}