package asia.sustech.happyMatch.Utils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Logger;

public class FormatValidator {
    private static final String emailRegex = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    private static final String passwordRegex = "^[a-zA-Z0-9`~!@#$%^&*()_+\\-={}|\\[\\]\\\\:\";'<>?,./]{6,16}$";
    private static final String userNameRegex = "^[a-zA-Z0-9_-]{4,10}$";
    private static final String tokenRegex = "^[a-zA-Z0-9]{32}$";

    private static final String salt = "heLlomYj@VApROJEcTbyXC1Ph4nd";

    public static boolean isEmailInvalid(String email) {
        if (email == null || email.equals("null") || email.length() > 50) return true;
        return !email.matches(emailRegex);
    }

    public static boolean isPasswordInvalid(String password) {
        if (password == null || password.length() > 16 || password.length() < 6) {
            return true;
        }
        return !password.matches(passwordRegex);
    }

    public static boolean isUserNameInvalid(String userName) {
        if (userName == null || userName.length() > 10 || userName.length() < 4) return true;
        return !userName.matches(userNameRegex);
    }

    public static boolean isTokenInvalid(String token) {
        if (token == null || token.length() != 32) return true;
        return !token.matches(tokenRegex);
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
            Logger.getLogger("FormatValidator").warning("MD5加密失败" + e.getMessage());
        }
        return null;
    }

    public static boolean isAvatarInvalid(String avatar) {
        if (avatar == null) return true;
        //是否为base64编码 且是否小于2M
        return isBase64Encoded(avatar) || isSizeValid(avatar);
    }

    private static boolean isBase64Encoded(String avatar) {
        try {
            // 移除base64编码前缀部分
            String imageData = avatar.split(",")[1];
            // 解码base64数据
            byte[] imageBytes = Base64.getDecoder().decode(imageData);
            // 将字节数组转换为图像
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            ImageIO.read(bis);
            bis.close();
            return false;
        } catch (IllegalArgumentException | IOException e) {
            return true;
        }
    }

    private static boolean isSizeValid(String avatar) {
        // 获取头像字符串的字节数
        int avatarSize = avatar.getBytes().length;

        // 将最大大小转换为字节数
        int maxSizeInBytes = 2 * 1024 * 1024;

        return avatarSize > maxSizeInBytes;
    }

    public static boolean isCodeInvalid(String code) {
        //验证码是否为4位数字
        return code == null || code.length() != 4 || !code.matches("^[0-9]{4}$");
    }

    public static boolean isMapIdInvalid(String mapId) {
        //地图id是否为数字
        return mapId == null || !mapId.matches("^[0-9]{1,2}$");
    }


    public static boolean isResPathInValid(String res) {
        //res不包含"/ \"
        return res.matches("^\\$") || res.matches("^/$");
    }

    public static String getRandomCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append((int) (Math.random() * 10));
        }
        return code.toString();
    }
}