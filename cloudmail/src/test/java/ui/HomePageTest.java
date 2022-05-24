package ui;

import com.codeborne.selenide.ElementsCollection;
import encryption.User;
import io.qameta.allure.Owner;
import models.dbModels.CloudFileInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import pages.HomePage;
import sql.SqlQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;
import static encryption.MailUserRole.admin;
import static encryption.MailUserRole.mailDBReader;
import static encryption.UserCryptographer.getUser;
import static io.qameta.allure.Allure.step;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Authorization.login;

public class HomePageTest extends TestRunner {
    private final User user = getUser(admin);
    private List<CloudFileInfo> allFilesFromDB;
    private CloudFileInfo fileFromDB;
    private HomePage homePage;

    @BeforeEach
    void authorization() {
        homePage = login(user).userWidgetCheck(user);
        allFilesFromDB = getAllCloudFilesFromDB();
    }

    @DisplayName("Проверка видимости файлов домашней страницы")
    @Owner("Калашников Владислав Александрович")
    @Test
    void homePageFilesCheck() {
        allFilesFromDB.forEach(fileInfo ->
                homePage.cellContentVisible(fileInfo.getName(), fileInfo.getContentextension()));
    }

    @DisplayName("Проверка выгрузки файла")
    @Owner("Калашников Владислав Александрович")
    @Test
    void uploadFileCheck() {
        fileFromDB = allFilesFromDB.get(new Random().nextInt(allFilesFromDB.size()));
        fileFromDB.setName(format("%s--%s--", fileFromDB.getName(), currentTimeMillis()));
        homePage.uploadFile(fileFromDB.toTempFile())
                .cellContentVisible(fileFromDB.getName(), fileFromDB.getContentextension());
    }

    @DisplayName("Проверка загрузки файла и его соответствия раннее выгруженному")
    @Owner("Калашников Владислав Александрович")
    @Test
    void downloadFileCheck() {
        File fileFromCloud = homePage.downloadRandomFile();
        String fileFromCloudNameWithoutTimestamp = fileFromCloud.getName().replaceAll("--[0-9]*--", "");
        fileFromDB = allFilesFromDB.stream()
                .filter(fileInfo -> fileInfo.getNameWithExt().equals(fileFromCloudNameWithoutTimestamp))
                .findFirst()
                .orElse(new CloudFileInfo());

        step("Проверить имя загруженного файла", () ->
                assertEquals(fileFromDB.getNameWithExt(), fileFromCloudNameWithoutTimestamp,
                        "Имя файла не совпадает ни с одним из выгруженных"));
        step("Проверить содержимое загруженного файла", () ->
                assertArrayEquals(fileFromDB.getContentbytes(), fileToByteArray(fileFromCloud),
                        "Содержимое файла не совпадает с выгруженным"));
    }

    @DisplayName("Удалить все файлы из облака, которых нет в БД ")
    @Owner("Калашников Владислав Александрович")
    @Test
    void removeAllFilesNotContainedInDBFromCloud() {
        Set<String> filesFromDBNames = allFilesFromDB.stream()
                .map(CloudFileInfo::getNameWithExt)
                .collect(toSet());

        ElementsCollection cells = homePage.cells();
        for (int i = 0; i < cells.size() - 1; i++) {
            if (!filesFromDBNames.contains(homePage.getNameWithExtFromCell(cells.get(i)))) {
                homePage.removeFile(cells.get(i));
                i = -1;
                cells = homePage.cells();
            }
        }
    }

    /**
     * Считывает файл в массив байтов
     */
    private byte[] fileToByteArray(File file) {
        try {
            return new FileInputStream(file).readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionFailedError("Не удалось прочитать содержимое файла в массив байтов");
        }
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