package asia.sustech.happyMatch.Shop;

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

public class GoodsController {
    //获取商品列表接口，必要参数：token
    //解析参数->数据合法检验->数据库连接->数据库查询->返回
    public static void getGoodsList(Context ctx) {
        ctx.contentType("application/json; charset=utf-8");
        String token;
        try {
            token = ctx.queryParam("token");
        } catch (Exception e) {
            //参数错误
            return;
        }
        //检验数据合法性
        if (FormatValidator.isTokenInvalid(token)) {
            //参数错误
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
                //获取商品
                String sql_goods = SQL.GET_GOODS;
                ResultSet res_goods = dao.query(sql_goods);
                JSONObject data = JSON.parseObject("{}");   //构造返回的json
                while (res_goods.next()) {
                    JSONObject goods = JSON.parseObject("{}");
                    goods.put("id", res_goods.getInt("id"));
                    goods.put("name", res_goods.getString("name"));
                    goods.put("price", res_goods.getInt("price"));
                    goods.put("description", res_goods.getString("description"));
                    goods.put("imageURL", res_goods.getString("imageURL"));
                    data.put(String.valueOf(res_goods.getInt("id")), goods);
                }
                Logger.getLogger("GameController").info("用户" + res.getString("userName") + "获取商品列表成功");
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
