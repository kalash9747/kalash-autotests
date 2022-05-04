package util;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static util.MailUserRole.admin;

/**
 * Класс шифровальщик
 */
public class Cryptographer {

    private static byte[] key;

    /**
     * Метод для зашифровки строки
     *
     * @param str    - строка которую нужно зашифровать
     * @param secret - секретный ключ
     * @return - зашифрованная строка
     */
    public static String encrypt(String str, String secret) {
        return crypt(str, secret, true);
    }

    /**
     * Метод для расшифровки строки
     *
     * @param str    - строка которую нужно расшифровать
     * @param secret - секретный ключ
     * @return - расшифрованная строка
     */
    public static String decrypt(String str, String secret) {
        return crypt(str, secret, false);
    }

    /**
     * Метод для шифрования строки
     *
     * @param str     - строка которую нужно шифровать
     * @param secret  - секретный ключ
     * @param isCrypt - зашифровать(true), расшифровать(false)
     * @return - (рас/за)шифрованная строка
     */
    public static String crypt(String str, String secret, boolean isCrypt) {
        setKey(secret);
        byte[] strBytes = isCrypt ? str.getBytes() : Base64.getDecoder().decode(str);
        for (int i = 0, size = strBytes.length; i < size; i++)
            for (byte keyByte : key)
                strBytes[i] = (byte) (strBytes[i] ^ keyByte);
        return isCrypt ? Base64.getEncoder().encodeToString(strBytes) : new String(strBytes);
    }

    private static void setKey(String secret) {
        key = secret.getBytes(UTF_8);
    }
}
