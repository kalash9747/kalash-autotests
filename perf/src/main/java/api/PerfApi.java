package api;

import io.qameta.allure.Step;
import models.PersonRq;

import static encryption.PerfUserRole.perfApiUser;
import static encryption.UserCryptographer.getUser;
import static java.net.URI.create;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static json.JsonHelper.objectToJson;

/**
 * Класс для работы с Api Перфоманс Лаб
 */
public class PerfApi {
    private final BaseHttpMethods base = new BaseHttpMethods();
    private final String baseUri = getUser(perfApiUser).getUrl();

    @Step("Отправить запрос на добавление пользователя /addUser")
    public HttpResponseFacade addUser(PersonRq personRq) {
        return base.post(create(baseUri + "/addUser"),
                ofString(objectToJson(personRq)),
                "Content-Type", "application/json");
    }

    @Step("Отправить запрос на покупку автомобиля /user/{personId}/buyCar/{carId}")
    public HttpResponseFacade buyCar(long personId, long carId) {
        return base.post(create(baseUri + "/user/" + personId + "/buyCar/" + carId));
    }
}
