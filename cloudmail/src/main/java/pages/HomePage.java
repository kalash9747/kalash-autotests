package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.files.FileFilter;
import encryption.User;
import io.qameta.allure.Step;
import org.openqa.selenium.support.pagefactory.ByChained;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byTagName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.files.FileFilters.withName;
import static com.codeborne.selenide.files.FileFilters.withNameMatching;
import static pages.NotExtensionFilter.withoutExtension;

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
        return userWidget().$("span.ph-project__user-name");
    }

    //Иконка пользователя
    public SelenideElement userIcon() {
        return userWidget().$("span.ph-project__user-icon").$(byTagName("img"));
    }

    //Кнопка 'Скачать'
    public SelenideElement downloadButton() {
        return $(byAttribute("data-name", "download"));
    }

    //Коллекция ячеек с представлением файлов
    public ElementsCollection cells() {
        return $$(new ByChained(byAttribute("class", "VirtualList__gridRow--m0RSQ"),
                (byAttribute("class", "DataListItemThumb__root--3TJe9"))));
    }

    /**
     * Находит ячейку по имени файла
     */
    public SelenideElement cell(String name) {
        return cells().filterBy(Condition.matchText(".*"+name+".*")).first();
    }

    @Step("Скачать файл {name}")
    public File downloadFile(String name) {
        cell(name).click();
        File file = null;
        downloadButton().click();
        try {
            file = downloadButton().download(10000,withNameMatching(".*"+name+".*"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Не удалось скачать файл");
        }
        return file;
    }
//withoutExtension("tmp", "crdownload")
    @Step("Проверить присутствие иконки и необходимого логина пользователя в виджете")
    public HomePage userWidgetCheck(User user) {
        userIcon().shouldBe(visible);
        userName().shouldBe(visible, text(user.getLogin()));
        return this;
    }
}
