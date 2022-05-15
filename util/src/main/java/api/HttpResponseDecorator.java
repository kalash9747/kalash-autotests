package api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс обертка для упрощения работы с HttpResponse
 */
public class HttpResponseDecorator {
    private final HttpResponse<String> response;

    public HttpResponseDecorator(HttpResponse<String> response) {
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
    public HttpResponseDecorator shouldBeStatusCode(int expectedStatusCode) {
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
            return new ObjectMapper().readValue(getBody(), clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Не удалось распарсить JSON в " + clazz.getName());
        }
        return null;
    }
}
