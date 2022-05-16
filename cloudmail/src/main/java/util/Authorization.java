package util;

import com.codeborne.selenide.Selenide;
import encryption.User;
import pages.HomePage;
import pages.PreLoginPage;

import static com.codeborne.selenide.Selenide.open;

/**
 * Класс Ui-авторизации
 */
public class Authorization {
    public static HomePage login(User user){
        open(user.getUrl());
        return new PreLoginPage()
                .aboutTitleVisible()
                .loginButtonClick()
                .userNameInputFill(user.getLogin())
                .nextButtonClick()
                .passwordInputFill(user.getPassword())
                .submitButtonClick();
    }
}
