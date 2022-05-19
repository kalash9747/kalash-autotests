package api;

import encryption.User;
import io.qameta.allure.Step;

import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublisher;
import java.util.regex.Matcher;

import static api.StringPublisherAdapter.ofMapEntries;
import static java.util.regex.Pattern.compile;

/**
 * Класс для работы с Api CloudMail
 */
public class CloudMailApi {
    private final BaseHttpMethods base = new BaseHttpMethods();
    private final String baseAuthUri = "https://auth.mail.ru/cgi-bin";
    private String baseUri;
    private String csrfToken;

    @Step("Авторизоваться под пользователем {user}")
    public HttpResponseFacade login(User user) {
        baseUri = user.getUrl();
        URI authUri = URI.create(baseAuthUri + "/auth");
        BodyPublisher authBody = ofMapEntries(
                "username", user.getLogin(),
                "Login", user.getLogin(),
                "password", user.getPassword(),
                "Password", user.getPassword(),
                "saveauth", "1",
                "act_token", getActToken(),
                "page", baseUri
        );
        HttpResponseFacade response = base.post(authUri, authBody)
                .shouldBeStatusCode(200);
        csrfToken = getCsrfTokenFromBody(response.getBody());
        return response;
    }

    @Step("Выйти из системы")
    public HttpResponseFacade logout() {
        return base.get(URI.create(baseAuthUri + "/logout"));
    }

    /**
     * Запрос информации о каталоге, указанном в параметрах
     */
    @Step("Отправить запрос api/v4/private/list?{path}")
    public HttpResponseFacade privateList(String path) {
        return base.get(URI.create(baseUri + "/api/v4/private/list?path=" + path),
                "X-CSRF-Token", csrfToken);
    }

    /**
     * Получает токен csrf, для подстановки в хидеры запросов /api/
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
        return base
                .getCookies()
                .stream()
                .filter(x -> x.getName().startsWith("act"))
                .findFirst()
                .map(HttpCookie::getValue)
                .orElse("");
    }
}
