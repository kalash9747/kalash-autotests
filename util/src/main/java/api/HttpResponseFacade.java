package api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;

import java.net.http.HttpResponse;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
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
        try {
            return new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(getBody(), clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Не удалось распарсить JSON в " + clazz.getName());
        }
        return null;
    }
}
