package pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.*;

/**
 * Прелогин страница
 */
public class PreLoginPage {
    //Область с информацией об облаке
    public SelenideElement sectionAbout() {
        return $("section.about");
    }

    //Заголовок области информации об облаке
    public SelenideElement aboutTitle() {
        return sectionAbout().$("h2.about__title");
    }

    //Кнопка Войдите в Облако
    public SelenideElement loginButton() {
        return sectionAbout().$(byAttribute("data-action", "login"));
    }

    @Step("Проверить видимость заголовка 'Что такое Облако'")
    public PreLoginPage aboutTitleVisible() {
        aboutTitle().shouldBe(visible);
        return this;
    }

    @Step("Нажать кнопку Войти в Облако")
    public AuthorizationFrame loginButtonClick() {
        loginButton().shouldBe(visible).click();
        switchTo().frame(1);
        return new AuthorizationFrame();
    }

}
