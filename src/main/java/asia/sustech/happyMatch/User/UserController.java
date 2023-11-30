package asia.sustech.happyMatch.User;

import asia.sustech.happyMatch.Config.Config;
import asia.sustech.happyMatch.Const.Msg;
import asia.sustech.happyMatch.Const.SQL;
import asia.sustech.happyMatch.Const.StatusCode;
import asia.sustech.happyMatch.DataBase.DAO;
import asia.sustech.happyMatch.HTTPResult;
import asia.sustech.happyMatch.Utils.FormatValidator;
import asia.sustech.happyMatch.Utils.ImageUtils;
import asia.sustech.happyMatch.Utils.Token;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.http.Context;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
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
        String sql = String.format(SQL.LOGIN, token, username, username, FormatValidator.getHashedPassword(password));
        //发起数据库查询
        //200成功，403用户名密码错误，400参数非法，500服务器错误
        if (dao.update(sql)) {
            Logger.getLogger("UserController").info("用户" + username + "登录成功");
            new HTTPResult(ctx, StatusCode.OK, Msg.LOGIN_SUCCESS, null, token).Return();
        } else {
            Logger.getLogger("UserController").info("用户" + username + "登录失败");
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
                Logger.getLogger("UserController").info("用户" + username + "注册失败，用户已存在");
                new HTTPResult(ctx, StatusCode.USER_EXIST, Msg.USER_EXIST, null, null).Return();
            } else {
                //用户不存在->注册
                sql = String.format(SQL.REGISTER, username, email, FormatValidator.getHashedPassword(password), token);

                if (dao.update(sql)) {
                    //注册成功(返回token
                    Logger.getLogger("UserController").info("用户" + username + "注册成功");
                    new HTTPResult(ctx, StatusCode.OK, Msg.REGISTER_SUCCESS, null, token).Return();
                } else {
                    Logger.getLogger("UserController").info("用户" + username + "注册失败，数据库错误");
                    new HTTPResult(ctx, StatusCode.SERVER_ERROR, Msg.SERVER_ERR, null, null).Return();
                }
            }
        } catch (SQLException e) {
            Logger.getLogger("UserController").warning("数据库查询失败" + e.getMessage());
            new HTTPResult(ctx, StatusCode.SERVER_ERROR, Msg.SERVER_ERR, null, null).Return();
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
                Logger.getLogger("UserController").info("用户" + res.getString("username") + "查询信息成功");
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

    // 更新用户信息，必要参数：token
    //解析参数->数据合法检验->数据库连接->数据库查询->返回
    public static void updateProcess(Context ctx) {

    }

    // 更新用户头像(post)，必要参数：token, avatarBase64文本
    //解析参数->数据合法检验->数据库连接->数据库查询->返回
    public static void changeAvatar(Context context) {
        context.contentType("application/json; charset=utf-8");
        String token, avatar;
        try {
            String data = context.body();
            //解析json
            Type type = new com.google.gson.reflect.TypeToken<Map<String, Object>>() {
            }.getType();
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(data, type);
            token = (String) map.get("token");
            avatar = (String) map.get("avatar");
        } catch (Exception e) {
            new HTTPResult(context, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //检验数据合法性
        if (FormatValidator.isTokenInvalid(token) || FormatValidator.isAvatarInvalid(avatar)) {
            new HTTPResult(context, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //数据库连接
        DAO dao = new DAO(Config.url, Config.dbUser, Config.dbPwd, Config.dbDriver, context);
        //执行&解析 sql
        String sql = String.format(SQL.USER_INFO, token);
        try {
            ResultSet res = dao.query(sql);
            if (res.next()) {
                //用户存在
                //解析base64编码的图片到图片对象,jpg格式
                if (FormatValidator.isAvatarInvalid(avatar)) {
                    new HTTPResult(context, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
                    return;
                } else {
                    //保存图片到本地
                    String fileName = res.getInt("uid") + ".jpg";
                    //保存到当前目录下的avatar文件夹下
                    String filePath = System.getProperty("user.dir") + "/avatar/" + fileName;
                    ImageUtils.saveBase64ImageAsJpg(avatar, filePath);
                    //更新数据库
                    sql = String.format(SQL.CHANGE_AVATAR, "/avatar/" + fileName, token);
                    if (dao.update(sql)) {
                        //更新成功
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("avatarURL", "/avatar/" + fileName);
                        Logger.getLogger("UserController").info("用户" + res.getString("username") + "更新头像成功");
                        new HTTPResult(context, StatusCode.OK, Msg.OK, jsonObject, null).Return();
                    } else {
                        //更新失败
                        new HTTPResult(context, StatusCode.SERVER_ERROR, Msg.SERVER_ERR, null, null).Return();
                    }
                    //返回url
                }
            } else {
                //用户不存在
                new HTTPResult(context, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}