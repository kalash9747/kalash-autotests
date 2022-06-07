package json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Вспомогательный класс для парсинга Json в объекты и обратно
 */
public class JsonHelper {
    /**
     * Парсит объект в Json
     */
    public static String objectToJson(Object bodyModelObject) {
        try {
            return new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(bodyModelObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Не удалось преобразовать объект в Json");
        }
        return "";
    }

    /**
     * Парсит Json в Объект
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        try {
            return new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(json, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Не удалось распарсить JSON в " + clazz.getName());
        }
        return null;
    }
}
