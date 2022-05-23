package models.listPath;

import java.util.List;

/**
 * Представление ответа на запрос api/v4/private/list?path
 */
public class PrivateListPathRs {
    private CountContent count;
    private String name;
    private String path;
    private int size;
    private int rev;
    private String kind;
    private String type;
    private List<ContentObjectInfo> list;

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

    public List<ContentObjectInfo> getList() {
        return list;
    }
}
