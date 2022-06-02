package api;

import io.qameta.allure.Step;
import json.JsonHelper;

import java.net.http.HttpResponse;

import static io.qameta.allure.Allure.addAttachment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Фасад для упрощения работы с HttpResponse
 */
public class HttpResponseFacade {
    private final HttpResponse<String> response;

    public HttpResponseFacade(HttpResponse<String> response) {
        this.response = response;
    }

    /**
     * Возвращает тело ответа
     */
    public String getBody() {
        return response.body();
    }

    @Step("Проверить, что статус-код соответствует ожидаемому: {expectedStatusCode}")
    public HttpResponseFacade shouldBeStatusCode(int expectedStatusCode) {
        assertEquals(expectedStatusCode, response.statusCode(),
                "Статус-код ответа не соответствует ожидаемому");
        return this;
    }

    @Step("Проверить, что в теле ответа присутствует текст: {expectedText}")
    public HttpResponseFacade shouldContainText(String expectedText) {
        addAttachment("Тело ответа:", getBody());
        assertTrue(response.body().contains(expectedText),
                "Указанный тект не найден в теле ответа: " + expectedText);
        return this;
    }

    /**
     * Распарсить тело ответа в класс указанный в параметрах
     *
     * @return - Объект указанного класса
     */
    public <T> T parseBodyTo(Class<T> clazz) {
        return JsonHelper.jsonToObject(getBody(), clazz);
    }
}
