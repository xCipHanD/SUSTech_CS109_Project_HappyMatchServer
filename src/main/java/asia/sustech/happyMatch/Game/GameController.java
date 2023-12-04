package asia.sustech.happyMatch.Game;

import asia.sustech.happyMatch.Config.Config;
import asia.sustech.happyMatch.Const.Msg;
import asia.sustech.happyMatch.Const.SQL;
import asia.sustech.happyMatch.Const.StatusCode;
import asia.sustech.happyMatch.DataBase.DAO;
import asia.sustech.happyMatch.HTTPResult;
import asia.sustech.happyMatch.Utils.FormatValidator;
import com.google.gson.JsonObject;
import io.javalin.http.Context;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class GameController {
    // 查看排行榜，必要参数：token
    //解析参数->数据合法检验->数据库连接->数据库查询->返回
    public static void rankList(Context ctx) {
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
                //获取排行榜
                sql = SQL.RANKLIST;
                res = dao.query(sql);
                JsonObject data = new JsonObject();
                int i = 1;
                while (res.next()) {
                    JsonObject user = new JsonObject();
                    user.addProperty("username", res.getString("username"));
                    user.addProperty("avatarURL", res.getString("avatarURL"));
                    user.addProperty("level", res.getInt("level"));
                    user.addProperty("coins", res.getInt("coins"));
                    user.addProperty("experience", res.getInt("experience"));
                    data.add(String.valueOf(i), user);
                    i++;
                }
                Logger.getLogger("GameController").info("用户" + res.getString("username") + "获取排行榜成功");
                new HTTPResult(ctx, StatusCode.OK, Msg.OK, data, null).Return();
            } else {
                //用户不存在
                new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
