package util;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserCryptographer {
    private static Map<UserRole, User> users = new HashMap<>();

    public static User getUser(UserRole userRole) {
        return users.get(userRole);
    }

    public static void decryptUsers() {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(
                        new File(Objects.requireNonNull(
                                UserCryptographer.class
                                        .getClassLoader()
                                        .getResource("credentials/secrets").toURI()))))) {
            String line;
            User user = new User();
            if (users != null) {users.clear();}
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if(line.isBlank()){
                    user = new User();
                    continue;
                }
                String[] lineArr = line.split("=",2);
                String lineKey = lineArr[0].trim();
                String lineValue = lineArr[1].trim();
                if (lineKey.equals("login")) {
                    user.setLogin(lineValue);
                }
                if (lineKey.equals("password")) {
                    user.setPassword(lineValue);
                }
                if (lineKey.equals("role")) {
                    user.setRole(lineValue);
                    UserRole userRole = (UserRole) Class.forName(System.getenv("roleClass")).getMethod("valueOf", String.class).invoke(null, user.getRole());
                    users.put(userRole, user);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Не удалось прочитать файл");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
