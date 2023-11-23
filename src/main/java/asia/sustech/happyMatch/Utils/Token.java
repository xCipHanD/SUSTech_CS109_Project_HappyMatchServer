package asia.sustech.happyMatch.Utils;

public class Token {
    public static String getToken(String userName) {
        return FormatValidator.getHashedPassword(userName + System.currentTimeMillis());
    }
}
