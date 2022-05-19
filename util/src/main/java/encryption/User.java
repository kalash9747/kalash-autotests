package encryption;

import static java.lang.String.format;

/**
 * Данные пользователя
 */
public class User {
    //Роль
    private String role;
    //Логин
    private String login;
    //Пароль
    private String password;
    //Базовый url
    private String url;

    public String getUrl() {
        return url;
    }

    public String getRole() {
        return role;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return format("User{ role='%s', login='%s'}", role, login);
    }
}
