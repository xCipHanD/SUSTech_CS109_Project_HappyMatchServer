package asia.sustech.happyMatch.Config;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Config {
    public static String dbUser;
    public static String dbPwd;
    public static String dbDriver;
    public static String url;
    public static String email_User;
    public static String email_Pwd;
    public static String email_Host;
    public static String email_ForgetPWD;
    public static int email_Port;
    public static int server_port;

    public static void init() {
        Logger logger = Logger.getLogger("Config");
        //读入数据库配置文件
        File f = new File("config.json");
        if (!f.exists()) {
            logger.warning("config.json不存在");
            System.exit(1);
        } else {
            //解析json
            try {
                String json = Files.readString(Paths.get(f.getPath()), StandardCharsets.UTF_8);
                JSONObject jsonObject = JSONObject.parseObject(json);
                //存入配置
                url = jsonObject.getString("url");
                dbUser = jsonObject.getString("dbUser");
                dbPwd = jsonObject.getString("dbPwd");
                dbDriver = jsonObject.getString("dbDriver");
                email_User = jsonObject.getString("email_User");
                email_Pwd = jsonObject.getString("email_Pwd");
                email_Host = jsonObject.getString("email_Host");
                email_ForgetPWD = jsonObject.getString("email_ForgetPWD");
                email_Port = jsonObject.getInteger("email_Port");
                server_port = jsonObject.getInteger("server_port");
            } catch (IOException e) {
                logger.warning("读取config.json失败");
                System.exit(1);
            }
        }
    }
}
