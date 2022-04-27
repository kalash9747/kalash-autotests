package util;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Cryptographer {

    private static byte[] key;

    public static String encode(String str, String secret) {
        setKey(secret);
        byte[] strBytes = str.getBytes();
        for (int i = 0, size = strBytes.length; i < size; i++) {
            for (byte keyByte : key) {
                strBytes[i] = (byte) (strBytes[i] ^ keyByte);
            }
        }
        return Base64.getEncoder().encodeToString(strBytes);
    }
    public static String decode(String str, String secret) {

        setKey(secret);
        byte[] strBytes = Base64.getDecoder().decode(str);
        for (int i = 0, size = strBytes.length; i < size; i++) {
            for (byte keyByte : key) {
                strBytes[i] = (byte) (strBytes[i] ^ keyByte);
            }
        }
        return new String(strBytes);
    }

    private static void setKey(String secret) {
        key = secret.getBytes(UTF_8);
    }

    public static void main(String[] args) {
        String key = "kuyjeleso";
        UserCryptographer.decryptUsers();
        User user = UserCryptographer.getUser(MailUserRole.admin);
        String login = user.getLogin();
        String loginc = encode(login,key);
        String logind = decode(loginc,key);
        String password = user.getPassword();
        String passwordc = encode(password,key);
        String passwordd = decode(passwordc,key);
        System.out.println();
        System.out.println("login : " +login);
        System.out.println("login : " + loginc);
        System.out.println("login : " + logind);
        System.out.println();
        System.out.println("password : " +password);
        System.out.println("password : " + passwordc);
        System.out.println("password : " + passwordd);
        System.out.println();
        User user1 = UserCryptographer.getUser(MailUserRole.admin1);
        String login1 = user1.getLogin();
        String loginc1 =encode(login1,key);
        String logind1 = decode(loginc1,key);
        String password1 = user1.getPassword();
        String passwordc1 = encode(password1,key);
        String passwordd1 = decode(passwordc1,key);
        System.out.println();
        System.out.println("login : " +login1);
        System.out.println("login : " + loginc1);
        System.out.println("login : " + logind1);
        System.out.println();
        System.out.println("password : " +password1);
        System.out.println("password : " + passwordc1);
        System.out.println("password : " + passwordd1);
        System.out.println();


//        String s = "you are right";
//        String enc = encodeDecode(s,"hsalak");
//        String dec = encodeDecode(enc,"hsalak");
//        System.out.println(enc);
//        System.out.println(dec);
    }
}
