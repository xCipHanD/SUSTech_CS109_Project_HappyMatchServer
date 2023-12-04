package asia.sustech.happyMatch.User;

import asia.sustech.happyMatch.Config.Config;
import asia.sustech.happyMatch.Const.Msg;
import asia.sustech.happyMatch.Const.SQL;
import asia.sustech.happyMatch.Const.StatusCode;
import asia.sustech.happyMatch.DataBase.DAO;
import asia.sustech.happyMatch.HTTPResult;
import asia.sustech.happyMatch.Utils.FormatValidator;
import io.javalin.http.Context;

public class PWDController {
    //修改密码的方法。用户传入新密码、token、验证码，返回是否修改成功/验证码是否正确
    public static void changePWD(Context ctx) {
        //解析参数
        String token = ctx.queryParam("token");
        String newPWD = ctx.queryParam("newPWD");
        String code = ctx.queryParam("code");
        //检查参数是否有效
        if (FormatValidator.isPasswordInvalid(newPWD) || FormatValidator.isTokenInvalid(token) || FormatValidator.isCodeInvalid(code)) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //连接数据库查询token是否有效，code是否正确且是否在有效期内
        DAO dao = new DAO(Config.url, Config.dbUser, Config.dbPwd, Config.dbDriver, ctx);
        //执行&解析 sql
        String sql = String.format(SQL.USER_INFO, token);
    }
}
