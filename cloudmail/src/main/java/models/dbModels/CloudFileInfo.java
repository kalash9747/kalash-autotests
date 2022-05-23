package models.dbModels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.System.getProperty;

/**
 * Представление фойла в базе данных
 */
public class CloudFileInfo {
    private String name;
    private String contentextension;
    private byte[] contentbytes;

    public CloudFileInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getContentextension() {
        return contentextension;
    }

    public byte[] getContentbytes() {
        return contentbytes;
    }

    public String getNameWithExt() {
        return name + contentextension;
    }

    public File toTempFile() {
        File file = new File(getProperty("java.io.tmpdir"), this.getNameWithExt());
        file.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(file.getPath())) {
            fos.write(this.contentbytes);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось преобразование в File");
        }
        return file;
    }

    @Override
    public String toString() {
        return "CloudFile{" +
                "name='" + name + '\'' +
                ", extension='" + contentextension + '\'' +
                '}';
    }
}
