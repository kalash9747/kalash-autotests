package api;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import static io.qameta.allure.Allure.addAttachment;
import static java.net.http.HttpClient.Redirect.NORMAL;
import static java.net.http.HttpClient.Version.HTTP_2;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;

/**
 * Класс для отправки базовых запросов
 */
public class BaseHttpMethods {
    private CookieManager cookieManager;

    /**
     * Отправить запрос
     */
    public HttpResponseFacade send(HttpRequest request) {
        addAttachment("Uri:", request.uri().toString());
        try {
            return new HttpResponseFacade(getHttpClient().send(request, BodyHandlers.ofString()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Не удалось отправить запрос");
        }
        return null;
    }

    /**
     * Получить куки
     */
    public List<HttpCookie> getCookies() {
        return cookieManager.getCookieStore().getCookies();
    }

    /**
     * Возвращает новый клиент
     */
    public HttpClient getHttpClient() {
        if (cookieManager == null) cookieManager = new CookieManager();
        return HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .version(HTTP_2)
                .followRedirects(NORMAL)
                .build();
    }

    /**
     * Отправить GET запрос без заголовков
     */
    public HttpResponseFacade get(URI uri) {
        return get(uri, (String[]) null);
    }

    /**
     * Отправить GET запрос
     */
    public HttpResponseFacade get(URI uri, String... headers) {
        HttpRequest.Builder builder = newBuilder(uri);
        return send(headers == null ? builder.build() : builder.headers(headers).build());
    }

    /**
     * Отправить POST запрос
     */
    public HttpResponseFacade post(URI uri, BodyPublisher body, String... headers) {
        HttpRequest.Builder builder = newBuilder(uri).POST(body);
        return send(headers == null ? builder.build() : builder.headers(headers).build());
    }

    /**
     * Отправить POST запрос без заголовков
     */
    public HttpResponseFacade post(URI uri, BodyPublisher body) {
        return post(uri, body, (String[]) null);
    }

    /**
     * Отправить POST запрос без тела
     */
    public HttpResponseFacade post(URI uri, String... headers) {
        return post(uri, noBody(), headers);
    }

    /**
     * Отправить POST запрос без тела и заголовков
     */
    public HttpResponseFacade post(URI uri) {
        return post(uri, (String[]) null);
    }
}
