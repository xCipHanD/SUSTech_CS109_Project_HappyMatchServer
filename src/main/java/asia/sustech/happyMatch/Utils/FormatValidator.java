package asia.sustech.happyMatch.Utils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
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

    public static boolean isDiyMapIdInvalid(String mapId) {
        //地图id是否为六位数字/大小写字母
        return mapId == null || !mapId.matches("^[0-9a-zA-Z]{6}$");
    }

    public static boolean isMapIdInvalid(String mapId) {
        //地图id是否位1-50的数字
        int id = Integer.parseInt(mapId);
        return id < 1 || id > 50;
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

    public static String getDiyMapId() {
        StringBuilder mapId = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int random = (int) (Math.random() * 62);
            if (random < 10) {
                mapId.append(random);
            } else if (random < 36) {
                mapId.append((char) (random + 55));
            } else {
                mapId.append((char) (random + 61));
            }
        }
        return mapId.toString();
    }

    public static Boolean isMapInvalid(String map) {
        try {
            Scanner sc = new Scanner(map);
            String s1 = sc.nextLine();
            int level = Integer.parseInt(s1.split(" ")[0]);
            int blockNums = Integer.parseInt(s1.split(" ")[1]);
            int currentSteps = Integer.parseInt(s1.split(" ")[2]);
            int totalSteps = Integer.parseInt(s1.split(" ")[3]);
            int currentPoints = Integer.parseInt(s1.split(" ")[4]);
            int TotalPoints = Integer.parseInt(s1.split(" ")[5]);
            int propUsedCounts = Integer.parseInt(s1.split(" ")[6]);

            String[][] s3 = new String[8][8];
            for (int i = 0; i < 8; i++) {
                String s = sc.nextLine();
                System.arraycopy(s.split(" "), 0, s3[i], 0, 8);
            }
            sc.close();
            //检查地图格式
            if (level < 0 || level > 50 || blockNums < 0 || blockNums > 6 || currentSteps < 0 || currentSteps > 100 || totalSteps < 0 || totalSteps > 100 || currentPoints < 0 || currentPoints > 10000 || TotalPoints < 0 || TotalPoints > 10000 || propUsedCounts < 0 || propUsedCounts > 3) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}