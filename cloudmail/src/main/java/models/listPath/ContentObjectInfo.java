package models.listPath;

public class ContentObjectInfo {
    public CountContent count;
    public String name;
    public String path;
    public int size;
    public int rev;
    public String kind;
    public String type;
    public int mtime;

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

    public String hash;
}
