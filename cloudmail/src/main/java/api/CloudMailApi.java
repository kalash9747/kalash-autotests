package api;

import encryption.User;
import io.qameta.allure.Step;

import java.net.HttpCookie;
import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;

import static io.qameta.allure.Allure.step;
import static java.util.regex.Pattern.compile;

/**
 * Класс для работы с Api CloudMail
 */
public class CloudMailApi {
    private final BaseHttpMethods base = new BaseHttpMethods();
    private String baseUri;
    private String csrfToken;

    /**
     * Авторизоваться в cloud.mail.ru
     */
    public HttpResponseFacade login(User user) {
        return step("Авторизоваться под пользователем " + user.getLogin(), () -> {
            baseUri = user.getUrl();
            URI authUri = URI.create("https://auth.mail.ru/cgi-bin/auth");
            String authBody = String.format("username=%s&Login=%s&password=%s&Password=%s&saveauth=1&act_token=%s&page=%s",
                    user.getLogin(), user.getLogin(), user.getPassword(), user.getPassword(), getActToken(), user.getUrl());

            HttpResponseFacade response = base.post(authUri, authBody)
                    .shouldBeStatusCode(200);
            csrfToken = getCsrfTokenFromBody(response.getBody());
            return response;
        });
    }

    @Step("Отправить запрос api/v2/feed")
    public HttpResponseFacade feed() {
        return base.get(URI.create(baseUri + "/api/v2/feed"),
                "X-CSRF-Token", csrfToken);
    }

    /**
     * Получает токен csrf, для подстановки в хидеры запросов /api/v2
     */
    private String getCsrfTokenFromBody(String body) {
        Matcher matcher = compile("csrf\": \".[^\"]*").matcher(body);
        matcher.find();
        return matcher.group().split("\": \"")[1];
    }

    /**
     * Получает токен act, для подстановки в тело запроса авторизации
     */
    private String getActToken() {
        base.get(URI.create("https://mail.ru"));
        Optional<HttpCookie> act = base
                .getCookies()
                .stream()
                .filter(x -> x.getName().startsWith("act"))
                .findFirst();
        return act.isPresent() ? act.get().getValue() : "";
    }
}
