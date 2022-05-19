package models.dbModels;

import annotations.Column;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CloudFile {
    private String name;
    @Column(name = "content_extension")
    private String extension;
    @Column(name = "content_bytes")
    private String content;

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public String getContent() {
        return content;
    }
    public String getNameWithExt() {
        return name + extension;
    }

    @Override
    public String toString() {
        return "CloudFile{" +
                "name='" + name + '\'' +
                ", extension='" + extension + '\''+
                '}';
    }
}
