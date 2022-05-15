package models;

/**
 * Модель ответа на запрос /api/v2/feed
 */
public class FeedRs {
    private String email;
    private FeedRsBody body;
    private long time;
    private int status;

    public String getEmail() {
        return email;
    }

    public FeedRsBody getBody() {
        return body;
    }

    public long getTime() {
        return time;
    }

    public int getStatus() {
        return status;
    }
}
