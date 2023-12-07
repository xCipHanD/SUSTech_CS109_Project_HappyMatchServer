package asia.sustech.happyMatch.Const;

//定义常用sql语句常量
public class SQL {
    //更新token
    //UPDATE user SET loginTime = CURRENT_TIMESTAMP, token = '%s' WHERE (username = '%s' OR email = '%s') AND pwd =
    // '%s';
    public static final String LOGIN = "UPDATE user SET loginTime = CURRENT_TIMESTAMP, token = '%s' WHERE (username =" +
            " '%s' OR email = '%s') AND pwd = '%s';";
    public static final String REGISTER = "INSERT INTO user (`userName`, `email`, `pwd`, `token`,`registerTime`," +
            "`loginTime`) VALUES ('%s', '%s', '%s', '%s',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);";
    public static final String CHECK_USER = "SELECT * FROM user WHERE `userName` = '%s' OR `email` = '%s'";
    public static final String CHECK_USER_BY_EMAIL = "SELECT * FROM user WHERE `email` = '%s'";

    public static final String USER_INFO = "SELECT * FROM user WHERE `token` = '%s'";

    public static final String SIGN_IN = "UPDATE user SET signIn = CURRENT_TIMESTAMP WHERE token = '%s'";

    public static final String ADD_COINS = "UPDATE user SET coins = coins + %s WHERE token = '%s'";

    public static final String RANKLIST = "SELECT * FROM user ORDER BY level DESC, coins DESC, experience DESC " +
            "LIMIT 10;";
    public static final String CHANGE_AVATAR = "UPDATE user SET avatarURL = '%s' WHERE token = '%s';";
    //    public static final String GEN_CODE =
    public static final String CHANGE_PWD = "UPDATE user SET pwd = '%s' WHERE token = '%s';";

    public static final String GET_GOODS = "SELECT * FROM items";
    public static final String UPDATE_CODE = "UPDATE user SET verifyCode = '%s', verifyCodeTime = CURRENT_TIMESTAMP +" +
            " " +
            "INTERVAL 10 MINUTE WHERE email = '%s';";
}
