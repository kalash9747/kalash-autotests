package api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    /**
     * Проверяет соответствие статус-кода ожидаемому
     */
    public HttpResponseFacade shouldBeStatusCode(int expectedStatusCode) {
        assertEquals(expectedStatusCode, response.statusCode(),
                "Статус-код ответа не соответствует ожидаемому");
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
