package asia.sustech.happyMatch.DataBase;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;

import asia.sustech.happyMatch.Const.Msg;
import asia.sustech.happyMatch.Const.StatusCode;
import asia.sustech.happyMatch.HTTPResult;
import io.javalin.http.Context;

import java.sql.*;
import java.util.logging.Logger;

public class DAO {
    private Connection conn;
    private String url;
    private String dbUser;
    private String dbPwd;
    private String dbDriver;

    private Context ctx;

    public DAO(String url, String dbUser, String dbPwd, String driver, Context ctx) {
        //连接数据库
        try {
            this.conn = DriverManager.getConnection(url, dbUser, dbPwd);
            this.url = url;
            this.dbUser = dbUser;
            this.dbPwd = dbPwd;
            this.dbDriver = driver;
        } catch (SQLException e) {
            //数据库连接失败
            Logger.getLogger("DAO").warning("数据库连接失败" + e.getMessage());
            //返回错误信息
            new HTTPResult(ctx, StatusCode.SERVER_ERROR, Msg.SERVER_ERR, null, null).Return();
        }
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPwd() {
        return dbPwd;
    }

    public void setDbPwd(String dbPwd) {
        this.dbPwd = dbPwd;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public ResultSet query(String sql) {
        try {
            return conn.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean update(String sql) {
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
//            System.out.println(statement.getUpdateCount());
            return statement.getUpdateCount() > 0;
        } catch (SQLException e) {
            Logger.getLogger("DAO").warning("数据库更新失败" + e.getMessage());
            return false;
        }
    }
}
