package models;

/**
 * Модель файла из ответа на запрос /api/v2/feed
 */
public class FileInCloud {
    private String hash;
    private int mtime;
    private String virus_scan;
    private String name;
    private String home;
    private int size;


    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getMtime() {
        return mtime;
    }

    public void setMtime(int mtime) {
        this.mtime = mtime;
    }

    public String getVirus_scan() {
        return virus_scan;
    }

    public void setVirus_scan(String virus_scan) {
        this.virus_scan = virus_scan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }



}
