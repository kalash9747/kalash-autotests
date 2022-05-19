package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

/**
 * Всплывающее окно авторизации
 */
public class AuthorizationFrame {

    //Панель авторизации
    public SelenideElement loginPanel() {
        return $("div.login-panel");
    }

    //Поле ввода имени пользователя (почты)
    public SelenideElement userNameInput() {
        return loginPanel().$(byAttribute("name", "username"));
    }

    //Поле ввода пароля
    public SelenideElement passwordInput() {
        return loginPanel().$(byAttribute("name", "password"));
    }

    //Кнопка дальше (Ввести пароль)
    public SelenideElement nextButton() {
        return loginPanel().$(byAttribute("data-test-id", "next-button"));
    }

    //Кнопка Войти
    public SelenideElement submitButton() {
        return loginPanel().$(byAttribute("data-test-id", "submit-button"));
    }

    @Step("Ввести логин")
    public AuthorizationFrame userNameInputFill(String login) {
        userNameInput().click();
        userNameInput().sendKeys(login);
        return this;
    }

    @Step("Ввести пароль")
    public AuthorizationFrame passwordInputFill(String password) {
        passwordInput().click();
        passwordInput().sendKeys(password);
        return this;
    }

    @Step("Нажать кнопку 'Ввести пароль'")
    public AuthorizationFrame nextButtonClick() {
        nextButton().shouldBe(Condition.visible).click();
        return this;
    }

    @Step("Нажать кнопку 'Войти'")
    public HomePage submitButtonClick() {
        submitButton().shouldBe(Condition.visible).click();
        switchTo().window(0);
        return new HomePage();
    }
}
