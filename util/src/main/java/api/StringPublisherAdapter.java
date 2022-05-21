package api;

import java.net.http.HttpRequest.BodyPublisher;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.util.Map.entry;


/**
 * Адаптер к StringPublisher
 */
public class StringPublisherAdapter {
    public static BodyPublisher ofMapEntries(String... queryParams) {
        if (queryParams != null && queryParams.length != 0 && (queryParams.length & 1) == 0) {
            Set<Map.Entry<String, String>> entrySet = new HashSet<>();
            for (int i = 0; i < queryParams.length; i += 2) {
                entrySet.add(entry(queryParams[i], queryParams[i + 1]));
            }
            return ofString(entrySet.stream()
                    .map((entry) -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&")));
        } else {
            throw new IllegalArgumentException("Количество параметров должно быть четным!");
        }
    }
}