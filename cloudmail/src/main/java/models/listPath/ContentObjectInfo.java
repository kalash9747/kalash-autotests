package models.listPath;

/**
 * Представление объекта(файла или папки) в ответе на запрос api/v4/private/list?path
 */
public class ContentObjectInfo {
    private CountContent count;
    private String name;
    private String path;
    private int size;
    private int rev;
    private String kind;
    private String type;
    private int mtime;
    private String hash;

    public CountContent getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getSize() {
        return size;
    }

    public int getRev() {
        return rev;
    }

    public String getKind() {
        return kind;
    }

    public String getType() {
        return type;
    }

    public int getMtime() {
        return mtime;
    }

    public String getHash() {
        return hash;
    }
}
