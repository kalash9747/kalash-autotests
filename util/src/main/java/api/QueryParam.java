package api;


/**
 * Параметр для Api запросов
 */
public class QueryParam {
    private final String key;
    private final String value;

    public QueryParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
