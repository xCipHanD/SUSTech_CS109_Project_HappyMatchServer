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
import java.util.Objects;
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

    //购买商品接口，必要参数：token, goodsId
    //解析参数->数据合法检验->数据库连接->数据库查询->返回
    public static void buyGoods(Context ctx) {
        ctx.contentType("application/json; charset=utf-8");
        String token;
        int itemId;
        try {
            token = ctx.queryParam("token");
            itemId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("itemId")));
        } catch (Exception e) {
            //参数错误
            new HTTPResult(ctx, StatusCode.BAD_REQUEST, Msg.BAD_REQUEST, null, null).Return();
            return;
        }
        //检验数据合法性
        if (FormatValidator.isTokenInvalid(token) || itemId < 0) {
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
                String sql_good = String.format(SQL.GET_ITEM_BY_ID, itemId);
                ResultSet res_good = dao.query(sql_good);
                if (!res_good.next()) {
                    //商品不存在
                    new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.ITEM_NOT_FOUND, null, null).Return();
                    return;
                }
                //金币不足
                if (res.getInt("coins") < res_good.getInt("price")) {
                    new HTTPResult(ctx, StatusCode.UNAUTHORIZED, Msg.MONEY_NOT_ENOUGH, null, null).Return();
                    return;
                }
                //购买商品
                String sql_goods = String.format(SQL.BUY_GOODS, res.getInt("uid"), itemId);
                dao.update(sql_goods);
                //金币减少
                String sql_coins = String.format(SQL.ADD_COINS, -res_good.getInt("price"), token);
                dao.update(sql_coins);
                Logger.getLogger("GameController").info("用户" + res.getString("userName") + "购买商品" + itemId + "成功");
                //返回property数据
                String sql_property = String.format(SQL.GET_PROPERTY, res.getInt("uid"));
                ResultSet res_property = dao.query(sql_property);
                JSONObject data = JSON.parseObject("{}");   //构造返回的json
                while (res_property.next()) {
                    JSONObject property = JSON.parseObject("{}");
                    property.put("itemId", res_property.getInt("itemId"));
                    property.put("count", res_property.getInt("count"));
                    data.put(String.valueOf(res_property.getInt("itemId")), property);
                }
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
