package asia.sustech.happyMatch;


import asia.sustech.happyMatch.Config.Config;
import asia.sustech.happyMatch.Game.GameController;
import asia.sustech.happyMatch.Map.MapController;
import asia.sustech.happyMatch.User.PWDController;
import asia.sustech.happyMatch.User.UserController;
import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.apibuilder.ApiBuilder.*;

@Slf4j
public class Main {
    public static void main(String[] args) {
        //数据库连接
//        DAO db = new DAO(Config.getUrl(), Config.getDbUser(), Config.getDbPwd(), Config.getDbDriver());

        Config.init();
        Javalin app = Javalin.create().start(80);
        app.get("/", ctx -> ctx.result("Hello HappyMatch!"));
        app.routes(() -> {
            path("/user/login", () -> get(UserController::login));
            path("/user/register", () -> get(UserController::register));
            path("/user/info", () -> get(UserController::userInfo));
            path("/user/signin", () -> get(UserController::signIn));
            path("/user/changeAvatar", () -> post(UserController::changeAvatar));
            path("/user/changePWD", () -> get(PWDController::changePWD));
            path("/ranklist", () -> get(GameController::rankList));
            path("/map/get", () -> get(MapController::getMap));
        });


    }
}