package asia.sustech.happyMatch.User;

import asia.sustech.happyMatch.Config.Config;
import asia.sustech.happyMatch.Const.Msg;
import asia.sustech.happyMatch.Const.SQL;
import asia.sustech.happyMatch.Const.StatusCode;
import asia.sustech.happyMatch.DataBase.DAO;
import asia.sustech.happyMatch.Email.EmailSender;
import asia.sustech.happyMatch.HTTPResult;
import asia.sustech.happyMatch.Utils.FormatValidator;
import io.javalin.http.Context;

import javax.mail.MessagingException;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class PWDController extends Thread {
    private static String email;
    private static String code;


    //修改密码的方法。用户传入新密码、token、验证码，返回是否修改成功/验证码是否正确
    public static void changePWD(Context ctx) {
        String email, code, newPWD;
        //解析参数
        email = ctx.queryParam("email");
        newPWD = ctx.queryParam("newPWD");
        code = ctx.queryParam("code");
        //检查参数是否有效
        if (FormatValidator.isPasswordInvalid(newPWD) || FormatValidator.isCodeInvalid(code) || FormatValidator.isEmailInvalid(email)) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //连接数据库查询token是否有效，code是否正确且是否在有效期内
        DAO dao = new DAO(Config.url, Config.dbUser, Config.dbPwd, Config.dbDriver, ctx);
        //执行&解析 sql
        String sql = String.format(SQL.CHECK_USER_BY_EMAIL, email);
        try {
            ResultSet res = dao.query(sql);
            if (res.next()) {
                //用户存在
                //是否存在验证码
                if (res.getString("verifyCode").isEmpty() || res.getString("verifyCodeTime").isEmpty()) {
                    new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
                    return;
                }
                //检查验证码是否正确且是否在有效期内
                if (res.getTimestamp("verifyCodeTime").getTime() + 10 * 60 * 1000 < System.currentTimeMillis()) {
                    new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.CODE_OUT_OF_DATE, null, null).Return();
                    return;
                }
                //检查验证码是否正确
                if (!res.getString("verifyCode").equals(code)) {
                    new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.CODE_ERROR, null, null).Return();
                    return;
                }
                //验证码正确，修改密码
                sql = String.format(SQL.UPDATE_PWD, FormatValidator.getHashedPassword(newPWD), email);
                dao.update(sql);
                Logger.getLogger("UserController").info(
                        "用户" + res.getString("userName") + "修改密码成功");
                //返回用户token
                new HTTPResult(ctx, StatusCode.OK, Msg.OK, null, null).Return();
            } else {
                //用户不存在
                new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
            }
            return;
        } catch (Exception e) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
    }

    //获取验证码
    //解析参数->数据合法检验->数据库连接->数据库查询->返回
    public static void getCode(Context context) {
        //解析参数
        try {
            email = context.queryParam("email");
        } catch (Exception e) {
            new HTTPResult(context, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //检验数据合法性
        if (FormatValidator.isEmailInvalid(email)) {
            new HTTPResult(context, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //数据库连接
        DAO dao = new DAO(Config.url, Config.dbUser, Config.dbPwd, Config.dbDriver, context);
        //执行&解析 sql
        String sql = String.format(SQL.CHECK_USER_BY_EMAIL, email);
        try {
            ResultSet res = dao.query(sql);
            if (res.next()) {
                //如果邮件已经被发送过，且验证码还在有效期内，不再发送邮件
                try {
                    if (res.getTimestamp("verifyCodeTime").getTime() + 10 * 60 * 1000 > System.currentTimeMillis()) {
                        new HTTPResult(context, StatusCode.OK, Msg.ALREADY_SEND_CODE, null, null).Return();
                        return;
                    }
                } catch (Exception ignored) {}
                //用户存在 -> 更新验证码 -> 发送邮件
                code = FormatValidator.getRandomCode();
                sql = String.format(SQL.UPDATE_CODE, code, email);
                dao.update(sql);
                PWDController emailThread = new PWDController();
                emailThread.start();
                Logger.getLogger("UserController").info(
                        "用户" + res.getString("userName") + "获取验证码成功");
                new HTTPResult(context, StatusCode.OK, Msg.OK, null, null).Return();
                return;
            } else {
                //用户不存在
                new HTTPResult(context, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void run() {
        //新建一个线程来发送邮件
        EmailSender ems = new EmailSender(Config.email_Host, Config.email_Port, Config.email_User,
                Config.email_Pwd);
        try {
            ems.sendEmail(email, "[验证码]HappyMatch重置密码是" + code,
                    "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta " +
                            "http-equiv=\"X-UA-Compatible\"content=\"IE=edge\"><meta " +
                            "name=\"viewport\"content=\"width=device-width, initial-scale=1.0\"><title>Reset Your " +
                            "Password</title><style>body{font-family:'Arial',sans-serif;background-color:#f4f4f4;" +
                            "text-align:center;margin:50px}.container{background-color:#fff;border-radius:8px;" +
                            "padding:20px;box-shadow:0 0 10px rgba(0,0,0,0.1);max-width:400px;margin:0 " +
                            "auto}h1{color:#333}p{color:#666}.code{font-size:24px;font-weight:bold;color:#007bff;" +
                            "margin-top:10px}.note{color:#999;margin-top:20px}</style></head><body><div " +
                            "class=\"container\"><h1>Your verification code is</h1><div class=\"code\">" + code +
                            "</div" +
                            "><p " +
                            "class=\"note\">Validity period is 10 minutes</p><p class=\"note\">This verification code" +
                            " is used to confirm your identity and should not be shared with others" +
                            ".</p></div></body></html>");
        } catch (MessagingException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
