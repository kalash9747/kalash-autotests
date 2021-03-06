package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import encryption.User;
import io.qameta.allure.Step;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byTagName;
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

    //Кнопка 'Загрузить'
    public SelenideElement uploadButton() {
        return $(byAttribute("data-name", "upload"));
    }

    //Поле загрузки файла в диалоговом окне загрузки
    public SelenideElement uploadDialogInput() {
        return $(byClassContaining("UploadDialog__input"));
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

    /**
     * Находит ячейку по имени и расширению файла
     */
    public SelenideElement cell(String name, String extension) {
        return cells()
                .filterBy(text(name))
                .filterBy(text(extension))
                .first();
    }

    //Имя файла в ячейке
    public SelenideElement cellName(SelenideElement cell) {
        return cell.$(byClassContaining("Name__name"));
    }

    //Расширение файла в ячейке
    public SelenideElement cellExtension(SelenideElement cell) {
        return cell.$(byClassContaining("Name__extension"));
    }

    /**
     * Получает полное имя(с расширением) файла в ячейке
     */
    public String getNameWithExtFromCell(SelenideElement cell) {
        return cellName(cell).text() + cellExtension(cell).text();

    }

    /**
     * Получает список полных имён(с расширением) всех файлов
     */
    public List<String> getFullNamesFromAllCells() {
        List<String> names = new ArrayList<>();
        cells().asFixedIterable().forEach(cell -> names.add(getNameWithExtFromCell(cell)));
        return names;
    }

    @Step("Проверить видимость имени и иконки файла {name}.{extension}")
    public HomePage cellContentVisible(String name, String extension) {
        SelenideElement cell = cell(name, extension).shouldBe(visible);
        cellName(cell).shouldBe(visible, text(name));
        cellExtension(cell).shouldBe(visible, text("." + extension));
        SelenideElement cellImage = cell.$(byTagName("img"));
        SelenideElement cellIcon = cellImage.exists() ? cellImage : cell.$(byClassContaining("FileIcon__icon"));
        cellIcon.shouldBe(visible);
        return this;
    }

    @Step("Скачать файл {nameWithExt}")
    public File downloadFile(String nameWithExt) {
        String[] nameParts = nameWithExt.split("\\.");
        cellScrollAndClick(cell(nameParts[0], nameParts[1]));
        try {
            return downloadButton().download(10000, withName(nameParts[0] + "." + nameParts[1]));
        } catch (FileNotFoundException e) {
            throw new AssertionFailedError("Не удалось скачать файл", e);
        }
    }

    @Step("Загрузить файл в облако")
    public HomePage uploadFile(File file) {
        uploadButton().shouldBe(visible).click();
        uploadDialogInput().uploadFile(file);
        return this;
    }

    @Step("Удалить файл из облака")
    public HomePage removeFile(SelenideElement cell) {
        cellScrollAndClick(cell);
        removeButton().shouldBe(visible).click();
        removeConfirmButton().shouldBe(visible).click();
        return this;
    }

    @Step("Проверить присутствие иконки и необходимого логина пользователя в виджете")
    public HomePage userWidgetCheck(User user) {
        userIcon().shouldBe(visible);
        userName().shouldBe(visible, text(user.getLogin()));
        return this;
    }

    /**
     * Скролл до элемента и клик по нему
     */
    public void cellScrollAndClick(SelenideElement element) {
        element.scrollTo().shouldBe(visible).click();
    }
}
