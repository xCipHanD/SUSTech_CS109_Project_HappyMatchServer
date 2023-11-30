package asia.sustech.happyMatch.User;

import io.javalin.http.Context;

import java.util.Objects;

public class PWDController {
    // 一个用户修改密码的网页，如果没有pwd参数，则返回网页，如果有pwd参数，则修改密码
    public static void changePWD(Context ctx) {
        try {
            String pwd = ctx.queryParam("pwd");
            if (pwd == null) {
                String s = String.valueOf(Objects.requireNonNull(UserController.class.getResource("/html/changePWD" +
                        ".html")));
                ctx.result("aaa" + s);
            }
        } catch (Exception e) {
            ctx.result("error");
        }
    }
}
