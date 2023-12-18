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
import java.io.IOException;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Logger;

public class MapController {
    public static void getMap(Context ctx) {
        ctx.contentType("application/json; charset=utf-8");
        String token, mapId;
        try {
            mapId = ctx.queryParam("mapId");//地图id
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
                JSONObject data = JSON.parseObject("{}");   //构造返回的json
                data.put("map", genMap(Integer.parseInt(mapId)));
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

    //保存自定义的地图
    public static void saveDiyMap(Context ctx) {
        ctx.contentType("application/json; charset=utf-8");
        String token, map;
        try {
            String data = ctx.body();
            //解析json
            JSONObject jsonObject = JSON.parseObject(data);
            token = jsonObject.getString("token");
            map = jsonObject.getString("map");
        } catch (Exception e) {
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        System.out.println(token + " " + map);
        //检验数据合法性
        if (FormatValidator.isTokenInvalid(token) || FormatValidator.isMapInvalid(map)) {
            if (FormatValidator.isMapInvalid(map)) {
                System.out.println("map is invalid");
            }
            if (FormatValidator.isTokenInvalid(token)) {
                System.out.println("token is invalid");
            }
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
                //生成地图id
                String mapId = FormatValidator.getDiyMapId();
                File mapFile = new File(System.getProperty("user.dir") + "/diyMaps/" + mapId);
                //写出地图文件
                if (!mapFile.exists()) {
                    mapFile.createNewFile();
                    if (map != null) {
                        Files.write(mapFile.toPath(), map.getBytes());
                    }
                }
                //构造地址
                String url = "/res/diyMaps/" + mapId;
                JSONObject data = JSON.parseObject("{}");   //构造返回的json
                data.put("url", url);
                Logger.getLogger("GameController").info("用户" + res.getString("username") + "保存地图成功");
                new HTTPResult(ctx, StatusCode.OK, Msg.OK, data, null).Return();
            } else {
                //用户不存在
                new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String genMap(int level) {
        return genMap(level, 4, 0, 0, 0, 0, 0);
    }

    public static String genMap(int level, int blocks, int currentSteps, int totalSteps, int currentPoints,
                                int totalPoints, int propUsedCounts) {
        int[][] map = new int[8][8];
        int[] forbitBlocks = {0, 1, 3, 5, 7, 7};
        int[] totalStepsArr = {20, 20, 25, 30, 35, 35};
        int[] totalPointsArr = {1000, 1500, 2000, 2500, 3000, 3000};
        propUsedCounts = 0;
        int count = forbitBlocks[level / 10];
        totalSteps = totalStepsArr[level / 10];
        totalPoints = totalPointsArr[level / 10];
        // 生成地图
        for (int i = 0; i < count; i++) {
            Random rand = new Random();
            int x = rand.nextInt(8);
            int y = rand.nextInt(8);
            if (map[x][y] == 0) {
                map[x][y] = -1;
            } else {
                i--;
            }
        }
        // 构造返回
        StringBuilder ret = new StringBuilder(String.format("%d %d %d %d %d %d %d\n", level, blocks, currentSteps,
                totalSteps, currentPoints, totalPoints, propUsedCounts));
        for (int i = 0; i < 8; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < 8; j++) {
                line.append(String.format("%d ", map[i][j]));
            }
            ret.append(line).append("\n");
        }
        return ret.toString();
    }

    public static void getProcess(Context context) {
        context.contentType("application/json; charset=utf-8");
        String token;
        try {
            token = context.queryParam("token");
        } catch (Exception e) {
            new HTTPResult(context, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //检验数据合法性
        if (FormatValidator.isTokenInvalid(token)) {
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
                //获取用户进度
                String map = res.getString("savedMap");
                int level = res.getInt("level");
                if (map != null) {
                    level = Integer.parseInt(map.split(" ")[0]);
                }
                JSONObject data = JSON.parseObject("{}");   //构造返回的json
                data.put("level", level);
                data.put("map", map == null ? "" : map);
                Logger.getLogger("GameController").info("用户" + res.getString("username") + "获取进度成功");
                new HTTPResult(context, StatusCode.OK, Msg.OK, data, null).Return();
            } else {
                //用户不存在
                new HTTPResult(context, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveProcess(Context context) {
        context.contentType("application/json; charset=utf-8");
        String token, map;
        int level;
        try {
            String data = context.body();
            //解析json
            JSONObject jsonObject = JSON.parseObject(data);
            token = jsonObject.getString("token");
            map = jsonObject.getString("map");
        } catch (Exception e) {
            new HTTPResult(context, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //检验数据合法性
        if (FormatValidator.isTokenInvalid(token) || FormatValidator.isMapInvalid(map)) {
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
                //更新用户进度
                sql = String.format(SQL.SAVE_MAP, map, token);
                Boolean a = dao.update(sql);
                System.out.println(a);
                JSONObject data = JSON.parseObject("{}");   //构造返回的json
                Logger.getLogger("GameController").info("用户" + res.getString("username") + "保存进度成功");
                new HTTPResult(context, StatusCode.OK, Msg.OK, data, null).Return();
            } else {
                //用户不存在
                new HTTPResult(context, StatusCode.UNAUTHORIZED, Msg.UNAUTHORIZED, null, null).Return();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
