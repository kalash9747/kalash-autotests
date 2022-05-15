package models;

import java.util.List;

/**
 * Модель body из ответа на запрос /api/v2/feed
 */
public class FeedRsBody {
    private int count;
    private List<FileInCloud> objects;

    public int getCount() {
        return count;
    }

    public List<FileInCloud> getObjects() {
        return objects;
    }
}
