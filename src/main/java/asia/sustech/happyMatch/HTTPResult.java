package asia.sustech.happyMatch;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.http.Context;

//http返回的结果
public class HTTPResult {
    private final Context ctx;
    private final int code;
    private final String msg;
    private final String data;
    private final String token;

    public HTTPResult(Context ctx, int code, String msg, String data, String token) {
        this.ctx = ctx;
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.token = token;
    }

    public void Return() {
        ctx.contentType("application/json; charset=utf-8");
        //构造返回的json
        JsonObject jsonObject = new JsonObject();
        // 添加不同的键值对
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("msg", msg);
        if (data != null) jsonObject.addProperty("data", data);
        if (token != null) jsonObject.addProperty("token", token);
        // 将JsonObject转换为JSON字符串
        Gson gson = new Gson();
        ctx.result(gson.toJson(jsonObject));
    }
}
