package asia.sustech.happyMatch.Game;

import asia.sustech.happyMatch.Config.Config;
import asia.sustech.happyMatch.Const.Msg;
import asia.sustech.happyMatch.Const.SQL;
import asia.sustech.happyMatch.Const.StatusCode;
import asia.sustech.happyMatch.DataBase.DAO;
import asia.sustech.happyMatch.HTTPResult;
import asia.sustech.happyMatch.Utils.FormatValidator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
                JSONObject data = JSON.parseObject("{}");   //构造返回的json
                int i = 1;
                while (res.next()) {
                    JSONObject user = JSON.parseObject("{}");
                    user.put("username", res.getString("username"));
                    user.put("avatarURL", res.getString("avatarURL"));
                    user.put("level", res.getInt("level"));
                    user.put("coins", res.getInt("coins"));
                    user.put("experience", res.getInt("experience"));
                    data.put(String.valueOf(i), user);
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
