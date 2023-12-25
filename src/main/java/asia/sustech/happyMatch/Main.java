package asia.sustech.happyMatch;


import asia.sustech.happyMatch.Config.Config;
import asia.sustech.happyMatch.Game.GameController;
import asia.sustech.happyMatch.Map.MapController;
import asia.sustech.happyMatch.Shop.GoodsController;
import asia.sustech.happyMatch.User.PWDController;
import asia.sustech.happyMatch.User.UserController;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.apibuilder.ApiBuilder.*;

@Slf4j
public class Main {
    public static void main(String[] args) {
        Config.init();

        Javalin app = Javalin.create(config -> {
            //静态资源
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/res/avatar";
                staticFiles.directory = System.getProperty("user.dir") + "/avatar";
                staticFiles.location = Location.EXTERNAL;
            });
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/res/goods";
                staticFiles.directory = System.getProperty("user.dir") + "/goods";
                staticFiles.location = Location.EXTERNAL;
            });
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/res/maps";
                staticFiles.directory = System.getProperty("user.dir") + "/maps";
                staticFiles.location = Location.EXTERNAL;
            });
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/res/diyMaps";
                staticFiles.directory = System.getProperty("user.dir") + "/diyMaps";
                staticFiles.location = Location.EXTERNAL;
            });
        }).start(Config.server_port);
        app.get("/", ctx -> ctx.result("Hello HappyMatch!"));


        app.routes(() -> {
            path("/user/login", () -> get(UserController::login));
            path("/user/register", () -> get(UserController::register));
            path("/user/info", () -> get(UserController::userInfo));
            path("/user/signin", () -> get(UserController::signIn));
            path("/user/changeAvatar", () -> post(UserController::changeAvatar));
            path("/user/getCode", () -> get(PWDController::getCode));
            path("/user/changePWD", () -> get(PWDController::changePWD));
            path("/user/updateProcess", () -> get(GameController::updateProcess));
            path("/user/getProperty", () -> get(GoodsController::getItems));
            path("/ranklist", () -> get(GameController::rankList));
            path("/map/get", () -> get(MapController::getMap));
            path("/map/getProcess", () -> get(MapController::getProcess));
            path("/map/saveProcess", () -> post(MapController::saveProcess));
            path("/map/saveDiy", () -> post(MapController::saveDiyMap));
            path("/shop/getGoodsList", () -> get(GoodsController::getGoodsList));
            path("/shop/buy", () -> get(GoodsController::buyGoods));
        });
    }
}