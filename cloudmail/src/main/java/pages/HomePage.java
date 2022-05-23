package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import encryption.User;
import io.qameta.allure.Step;
import models.dbModels.CloudFileInfo;
import org.openqa.selenium.support.pagefactory.ByChained;

import java.io.File;
import java.io.FileNotFoundException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.files.FileFilters.withName;
import static ui.ByAttribute.byClassContaining;

/**
 * Домашняя страница
 */
public class HomePage {

    //Виджет информации о пользователе
    public SelenideElement userWidget() {
        return $(byAttribute("data-testid", "whiteline-account"));
    }

    //Логин пользователя
    public SelenideElement userName() {
        return userWidget().$(byClassContaining("span", "user-name"));
    }

    //Иконка пользователя
    public SelenideElement userIcon() {
        return userWidget().$(byClassContaining("span", "user-icon")).$(byTagName("img"));
    }

    //Кнопка 'Скачать'
    public SelenideElement downloadButton() {
        return $(byAttribute("data-name", "download"));
    }

    //Кнопка 'Удалить'
    public SelenideElement removeButton() {
        return $(byAttribute("data-name", "remove"));
    }

    //Окно подтверждения удаления файла
    public SelenideElement removeDialog() {
        return $(byAttribute("data-qa-modal", "remove-confirmation-dialog"));
    }

    //Кнопка подтверждения удаления файла
    public SelenideElement removeConfirmButton() {
        return removeDialog().$(byAttribute("data-name", "confirm"));
    }

    //Коллекция ячеек с представлением файлов
    public ElementsCollection cells() {
        return $$(new ByChained(byClassContaining("gridRow"),
                (byClassContaining("DataListItemThumb__root"))));
    }

    //Область загрузки файла
    public SelenideElement uploadProvocation() {
        return $(byClassContaining("UploadProvocation__root"));
    }

    //Поле загрузки файла
    public SelenideElement uploadInput() {
        return uploadProvocation().$(byAttribute("type", "file"));
    }

    /**
     * Находит ячейку по имени и расширению файла
     */
    public SelenideElement cell(String name, String extension) {
        return cells()
                .filterBy(matchText(name))
                .filterBy(matchText(extension))
                .first();
    }

    @Step("Проверить видимость имени и иконки файла")
    public HomePage cellContentVisible(String name, String extension) {
        SelenideElement cell = cell(name, extension).shouldBe(visible);
        cell.$(byText(name)).shouldBe(visible);
        cell.$(byText(extension.replace(".", ""))).shouldBe(visible);
        SelenideElement cellImage = cell.$(byTagName("img"));
        SelenideElement cellIcon = cellImage.exists() ? cellImage : cell.$(byClassContaining("FileIcon__icon"));
        cellIcon.shouldBe(visible);
        return this;
    }

    @Step("Скачать файл {cloudFile.name}")
    public File downloadFile(CloudFileInfo cloudFile) {
        cell(cloudFile.getName(), cloudFile.getContentextension()).click();
        File file = null;
        downloadButton().click();
        try {
            file = downloadButton().download(10000, withName(cloudFile.getNameWithExt()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Не удалось скачать файл");
        }
        return file;
    }

    @Step("Загрузить файл в облако")
    public HomePage uploadFile(File file) {
        uploadInput().uploadFile(file);
        return this;
    }

    @Step("Удалить файл из облака")
    public HomePage removeFile(CloudFileInfo cloudFile) {
        SelenideElement cell = cell(cloudFile.getName(), cloudFile.getContentextension());
        cell.shouldBe(visible).click();
        removeButton().shouldBe(visible).click();
        removeConfirmButton().shouldBe(visible).click();
        cell.shouldNotBe(visible);
        return this;
    }

    @Step("Проверить присутствие иконки и необходимого логина пользователя в виджете")
    public HomePage userWidgetCheck(User user) {
        userIcon().shouldBe(visible);
        userName().shouldBe(visible, text(user.getLogin()));
        return this;
    }
}
