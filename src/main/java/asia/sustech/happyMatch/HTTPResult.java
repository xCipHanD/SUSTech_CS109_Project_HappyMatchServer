package asia.sustech.happyMatch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.javalin.http.Context;

//http返回的结果
public class HTTPResult {
    private final Context ctx;
    private final int code;
    private final String msg;
    private final JSONObject data;
    private final String token;

    public HTTPResult(Context ctx, int code, String msg, JSONObject data, String token) {
        this.ctx = ctx;
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.token = token;
    }

    public void Return() {
        ctx.contentType("application/json; charset=utf-8");
        //构造返回的json
        JSONObject jsonObject = JSON.parseObject("{}");
        // 添加不同的键值对
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if (data != null) jsonObject.put("data", data);
        if (token != null) jsonObject.put("token", token);
        // 将JsonObject转换为JSON字符串
        ctx.result(jsonObject.toJSONString());
    }
}
