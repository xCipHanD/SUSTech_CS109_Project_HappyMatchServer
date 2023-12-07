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
import java.util.Objects;
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
                ResultSet res_rank = dao.query(sql);
                JSONObject data = JSON.parseObject("{}");   //构造返回的json
                int i = 1;
                while (res_rank.next()) {
                    JSONObject user = JSON.parseObject("{}");
                    user.put("username", res_rank.getString("username"));
                    user.put("avatarURL", res_rank.getString("avatarURL"));
                    user.put("level", res_rank.getInt("level"));
                    user.put("coins", res_rank.getInt("coins"));
                    user.put("experience", res_rank.getInt("experience"));
                    data.put(String.valueOf(i), user);
                    i++;
                }
                Logger.getLogger("GameController").info("用户" + res.getString("userName") + "获取排行榜成功");
                new HTTPResult(ctx, StatusCode.OK, Msg.OK, data, null).Return();
            } else {
                //用户不存在
                new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 更新游戏进度，必要参数：token，进度
    //解析参数->数据合法检验->数据库连接->数据库查询->返回
    public static void updateProcess(Context ctx) {
        ctx.contentType("application/json; charset=utf-8");
        String token;
        int level;
        try {
            token = ctx.queryParam("token");
            level = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("level")));
        } catch (Exception e) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //检验数据合法性
        if (FormatValidator.isTokenInvalid(token) || level < 0 || level > 100) {
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
                //用户存在，比较用户进度
                int process = res.getInt("level");
                int experience = res.getInt("experience");
                if (process >= level) {
                    //用户进度已经大于等于当前进度
                    JSONObject data = JSON.parseObject("{}");   //构造返回的json
                    data.put("level", process);
                    new HTTPResult(ctx, StatusCode.OK, Msg.OK, data, null).Return();
                    return;
                }
                //更新进度,experience 增加 process-level * 100
                experience = experience + (level - process) * 100;
                sql = String.format(SQL.UPDATE_PROCESS, level, experience, token);
                dao.update(sql);
                Logger.getLogger("GameController").info("用户" + res.getString("username") + "更新进度成功");
                new HTTPResult(ctx, StatusCode.OK, Msg.OK, null, null).Return();
            } else {
                //用户不存在
                new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
            }
        } catch (SQLException e) {
            new HTTPResult(ctx, StatusCode.SERVER_ERROR, Msg.SERVER_ERR, null, null).Return();
        }
    }
}
