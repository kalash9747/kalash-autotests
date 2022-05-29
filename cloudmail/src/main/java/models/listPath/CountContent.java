package models.listPath;

/**
 * Количество папок и файлов в ответе за запрос api/v4/private/list?path
 */
public class CountContent {
    private int folders;
    private int files;

    public int getFolders() {
        return folders;
    }

    public int getFiles() {
        return files;
    }
}
