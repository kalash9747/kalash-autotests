package api;

import java.net.http.HttpRequest.BodyPublisher;

import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;


/**
 * Адаптер к StringPublisher
 */
public class StringPublisherAdapter {
    public static BodyPublisher ofQueryParams(QueryParam... queryParams) {
        return ofString(stream(queryParams)
                .map(QueryParam::toString)
                .collect(joining("&")));
    }
}
