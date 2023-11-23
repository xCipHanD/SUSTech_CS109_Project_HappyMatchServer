package asia.sustech.happyMatch.User;

import asia.sustech.happyMatch.Config.Config;
import asia.sustech.happyMatch.Const.Msg;
import asia.sustech.happyMatch.Const.SQL;
import asia.sustech.happyMatch.Const.StatusCode;
import asia.sustech.happyMatch.DataBase.DAO;
import asia.sustech.happyMatch.HTTPResult;
import asia.sustech.happyMatch.Utils.FormatValidator;
import asia.sustech.happyMatch.Utils.Token;
import com.google.gson.JsonObject;
import io.javalin.http.Context;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class UserController {
    //解析参数->数据合法检验->token->数据库连接->数据库查询->返回
    public static void login(Context ctx) {
        ctx.contentType("application/json; charset=utf-8");
        String username, password;
        //解析是否成功
        try {
            username = ctx.queryParam("user");
            password = ctx.queryParam("pwd");
        } catch (Exception e) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //检验数据合法性
        if (FormatValidator.isPasswordInvalid(password) || FormatValidator.isUserNameInvalid(username)) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //生成token
        String token = Token.getToken(username);
        //数据库连接
        DAO dao = new DAO(Config.url, Config.dbUser, Config.dbPwd, Config.dbDriver, ctx);
        //获取sql语句
        //"UPDATE user SET loginTime = CURRENT_TIMESTAMP, token = '%s' WHERE (username = '%s' OR email = '%s') AND
        // pwd = '%s'"
        String sql = String.format(SQL.LOGIN, token, username, username, FormatValidator.getHashedPassword(password));
//        System.out.println(sql);
        //发起数据库查询
        //200成功，403用户名密码错误，400参数非法，500服务器错误
        if (dao.update(sql)) {
            new HTTPResult(ctx, StatusCode.OK, Msg.LOGIN_SUCCESS, null, token).Return();
        } else {
            new HTTPResult(ctx, StatusCode.ERROR_UP, Msg.ERROR_UP, null, null).Return();
        }
    }

    //解析参数->数据合法检验->token->数据库连接->数据库查询->返回
    public static void register(Context ctx) {
        ctx.contentType("application/json; charset=utf-8");
        String username, password, email;
        try {
            username = ctx.queryParam("user");
            password = ctx.queryParam("pwd");
            email = ctx.queryParam("email");
        } catch (Exception e) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //检验数据合法性
        if (FormatValidator.isEmailInvalid(email) || FormatValidator.isPasswordInvalid(password) || FormatValidator.isUserNameInvalid(username)) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //生成token
        String token = Token.getToken(username);
        //数据库连接
//        new Config();
        DAO dao = new DAO(Config.url, Config.dbUser, Config.dbPwd, Config.dbDriver, ctx);
        //执行&解析 sql
        //查看用户是否存在
        String sql = String.format(SQL.CHECK_USER, username, email);
        try {
            ResultSet res = dao.query(sql);
            if (res.next()) {
                //用户存在
                new HTTPResult(ctx, StatusCode.USER_EXIST, Msg.USER_EXIST, null, null).Return();
            } else {
                //用户不存在->注册
                sql = String.format(SQL.REGISTER, username, email, FormatValidator.getHashedPassword(password), token);

                if (dao.update(sql)) {
                    //注册成功(返回token
                    new HTTPResult(ctx, StatusCode.OK, Msg.REGISTER_SUCCESS, null, token).Return();

                } else {
                    new HTTPResult(ctx, StatusCode.SERVER_ERROR, Msg.SERVER_ERR, null, null).Return();
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("UserController").warning("数据库查询失败" + e.getMessage());
            new HTTPResult(ctx, StatusCode.SERVER_ERROR, Msg.SERVER_ERR, null, null).Return();
            return;
//            throw new RuntimeException(e);
        }
    }

    // 查询用户信息，必要参数：token
    //解析参数->数据合法检验->数据库连接->数据库查询->返回
    public static void userInfo(Context ctx) {
        ctx.contentType("application/json; charset=utf-8");
        String token;
        try {
            token = ctx.queryParam("token");
        } catch (Exception e) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //检验数据合法性
        if (FormatValidator.isTokenInvalid(token)) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //数据库连接
        DAO dao = new DAO(Config.url, Config.dbUser, Config.dbPwd, Config.dbDriver, ctx);
        //执行&解析 sql
        String sql = String.format(SQL.USER_INFO, token);
        try {
            ResultSet res = dao.query(sql);
            if (res.next()) {
                //用户存在
                JsonObject data = new JsonObject();
                data.addProperty("uid", res.getInt("uid"));
                data.addProperty("username", res.getString("username"));
                data.addProperty("email", res.getString("email"));
                data.addProperty("avatarURL", res.getString("avatarURL"));
                data.addProperty("experience", res.getInt("experience"));
                data.addProperty("level", res.getInt("level"));
                data.addProperty("coins", res.getInt("coins"));
                data.addProperty("signIn", String.valueOf(res.getDate("signIn")));
                data.addProperty("role", res.getInt("role"));
                new HTTPResult(ctx, StatusCode.OK, Msg.OK, data, null).Return();
            } else {
                //用户不存在
                new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 签到，必要参数：token
    //解析参数->数据合法检验->数据库连接->数据库查询->返回
    public static void signIn(Context ctx) {
        ctx.contentType("application/json; charset=utf-8");
        String token;
        try {
            token = ctx.queryParam("token");
        } catch (Exception e) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //检验数据合法性
        if (FormatValidator.isTokenInvalid(token)) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //数据库连接
        DAO dao = new DAO(Config.url, Config.dbUser, Config.dbPwd, Config.dbDriver, ctx);
        //执行&解析 sql
        String sql = String.format(SQL.USER_INFO, token);
        try {
            ResultSet res = dao.query(sql);
            if (res.next()) {
                //用户存在
                //检查checkIn(timeStamp)时间是否为今天
                if (res.getDate("signIn") != null && res.getDate("signIn").toString().equals(java.time.LocalDate.now().toString())) {
                    //今天已经签到
                    new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.ALREADY_CHECKIN, null, null).Return();
                } else {
                    //今天未签到
                    //更新签到时间
                    sql = String.format(SQL.SIGN_IN, token);
                    if (dao.update(sql)) {
                        //更新成功
                        //更新金币
                        sql = String.format(SQL.ADD_COINS, 150, token);
                        if (dao.update(sql)) {
                            //更新成功
                            new HTTPResult(ctx, StatusCode.OK, Msg.OK, null, null).Return();
                        } else {
                            //更新失败
                            new HTTPResult(ctx, StatusCode.SERVER_ERROR, Msg.SERVER_ERR, null, null).Return();
                        }
                    } else {
                        //更新失败
                        new HTTPResult(ctx, StatusCode.SERVER_ERROR, Msg.SERVER_ERR, null, null).Return();
                    }
                }
            } else {
                //用户不存在
                new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateProcess(Context ctx) {

    }
}