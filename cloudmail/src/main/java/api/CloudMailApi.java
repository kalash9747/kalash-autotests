package api;

import encryption.User;
import io.qameta.allure.Step;

import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Optional;

import static io.qameta.allure.Allure.step;

public class CloudMailApi {
    private final BaseHttpMethods base = new BaseHttpMethods();
    private String baseUri;

    public HttpResponse<String> login(User user) {
        return step("Авторизоваться под пользователем " + user.getLogin(), () -> {
            baseUri = user.getUrl();
            URI authUri = URI.create("https://auth.mail.ru/cgi-bin/auth");
            String authBody = String.format("username=%s&Login=%s&password=%s&Password=%s&saveauth=1&act_token=%s&page=%s",
                    user.getLogin(), user.getLogin(), user.getPassword(), user.getPassword(), getActToken(), user.getUrl());
            return base.post(authUri, authBody);
        });
    }

    private String getActToken() {
        base.get(URI.create("https://mail.ru/"));
        Optional<HttpCookie> act = base
                .getCookies()
                .stream()
                .filter(x -> x.getName().startsWith("act"))
                .findFirst();
        return act.isPresent() ? act.get().getValue() : "";
    }

    @Step("Отправить запрос /home")
    public HttpResponse<String> home() {
        return base.get(URI.create(baseUri + "/home/"));
    }
}
