package asia.sustech.happyMatch.Config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
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


    public Config() {
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
                Gson gson = new Gson();
                Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>() {
                }.getType());
                //存入配置
                url = (String) map.get("url");
                dbUser = (String) map.get("dbUser");
                dbPwd = (String) map.get("dbPwd");
                dbDriver = (String) map.get("dbDriver");
                email_User = (String) map.get("email_User");
                email_Pwd = (String) map.get("email_Pwd");
                email_Host = (String) map.get("email_Host");
                email_ForgetPWD = (String) map.get("email_ForgetPWD");
                email_Port = Integer.parseInt((String) map.get("email_Port"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
