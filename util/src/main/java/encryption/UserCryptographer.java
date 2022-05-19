package encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static encryption.Cryptographer.decrypt;
import static java.lang.System.getenv;
import static java.util.Objects.requireNonNull;

/**
 * Класс для рашифровки пользователей
 */
public class UserCryptographer {
    private static final Map<UserRole, User> users = new HashMap<>();

    /**
     * Возвращает пользователя с указанной ролью
     */
    public static User getUser(UserRole userRole) {
        if (!users.containsKey(userRole)) decryptUserFromFile(userRole);
        return users.get(userRole);
    }

    /**
     * Расшифровывает пользователя с указанной ролью
     */
    public static void decryptUserFromFile(UserRole userRole) {
        Properties properties = new Properties();
        try {
            properties.load(getCredFileAsStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось прочитать файл свойств пользователей");
        }

        User user = new User();
        user.setRole(userRole.toString());
        user.setUrl(properties.getProperty(userRole + ".url"));
        user.setLogin(properties.getProperty(userRole + ".login"));
        user.setPassword(properties.getProperty(userRole + ".password"));

        if (user.getPassword() == null || user.getLogin() == null)
            throw new IllegalStateException("Проверьте файл свойств пользователей, не удалось получить свойства " + userRole);

        String key = getenv("key");
        if (key != null)
            user.setPassword(decrypt(user.getPassword(), key));
        users.put(userRole, user);
    }

    private static InputStream getCredFileAsStream() {
        return requireNonNull(UserCryptographer.class.getClassLoader()
                .getResourceAsStream("credentials/secrets.properties"));
    }
}
