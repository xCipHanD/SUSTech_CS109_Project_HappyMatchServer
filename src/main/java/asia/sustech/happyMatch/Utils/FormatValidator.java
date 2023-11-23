package asia.sustech.happyMatch.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FormatValidator {
    private static final String emailRegex = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    private static final String passwordRegex = "^[a-zA-Z0-9`~!@#$%^&*()_+\\-={}|\\[\\]\\\\:\";'<>?,./]{6,16}$";
    private static final String userNameRegex = "^[a-zA-Z0-9_-]{6,16}$";

    private static final String salt = "heLlomYj@VApROJEcTbyXC1Ph4nd";

    public static boolean isEmailInvalid(String email) {
        if (email == null || email.equals("null") || email.length() > 50) return true;
        return !email.matches(emailRegex);
    }

    public static boolean isPasswordInvalid(String password) {
        if (password == null || password.length() > 16 || password.length() < 6)
            return true;
        return !password.matches(passwordRegex);
    }

    public static boolean isUserNameInvalid(String userName) {
        if (userName == null || userName.length() > 16 || userName.length() < 6) return true;
        return !userName.matches(userNameRegex);
    }

    public static String getHashedPassword(String password) {

        try {
            // 创建MD5加密算法的实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 将输入字符串转换为字节数组
            byte[] inputBytes = (password + salt).getBytes();
            // 执行MD5加密
            byte[] hashBytes = md.digest(inputBytes);
            // 将加密后的字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            //            System.out.println("MD5加密结果：" + md5Hash);
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}