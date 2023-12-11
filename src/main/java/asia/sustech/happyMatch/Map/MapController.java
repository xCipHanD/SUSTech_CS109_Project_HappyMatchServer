package asia.sustech.happyMatch.Map;

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

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class MapController {
    public static void getMap(Context ctx) {
        ctx.contentType("application/json; charset=utf-8");
        String token, mapId;
        try {
            mapId = ctx.queryParam("mapId");
            token = ctx.queryParam("token");
        } catch (Exception e) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //检验数据合法性
        if (FormatValidator.isTokenInvalid(token) || FormatValidator.isMapIdInvalid(mapId)) {
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
                //检测地图文件是否存在
                File mapFile = new File(System.getProperty("user.dir") + "/map" + mapId);
                if (!mapFile.exists()) {
                    //地图文件不存在
                    new HTTPResult(ctx, StatusCode.NOT_FOUND, Msg.NOT_FOUND, null, null).Return();
                    return;
                }
                //获取地图的本地文件路径
                String mapPath = System.getProperty("user.dir") + "/map" + mapId;
                JSONObject data = JSON.parseObject("{}");   //构造返回的json
                data.put("url", mapPath);
                Logger.getLogger("GameController").info("用户" + res.getString("username") + "获取地图成功");
                new HTTPResult(ctx, StatusCode.OK, Msg.OK, data, null).Return();
            } else {
                //用户不存在
                new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void diyMap(Context ctx) {
        ctx.contentType("application/json; charset=utf-8");
        String token, mapId;
        try {
            mapId = ctx.queryParam("mapId");
            token = ctx.queryParam("token");
        } catch (Exception e) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //检验数据合法性
        if (FormatValidator.isTokenInvalid(token) || FormatValidator.isMapIdInvalid(mapId)) {
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
                //检测地图文件是否存在
                File mapFile = new File(System.getProperty("user.dir") + "/map" + mapId);
                if (!mapFile.exists()) {
                    //地图文件不存在
                    new HTTPResult(ctx, StatusCode.NOT_FOUND, Msg.NOT_FOUND, null, null).Return();
                    return;
                }
                //获取地图的本地文件路径
                String mapPath = System.getProperty("user.dir") + "/map" + mapId;
                JSONObject data = JSON.parseObject("{}");   //构造返回的json
                data.put("url", mapPath);
                Logger.getLogger("GameController").info("用户" + res.getString("username") + "获取地图成功");
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
