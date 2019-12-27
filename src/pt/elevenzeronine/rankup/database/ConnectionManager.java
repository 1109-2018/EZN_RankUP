package pt.elevenzeronine.rankup.database;

import org.bukkit.Bukkit;
import pt.elevenzeronine.rankup.RankupPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectionManager {

    public static Connection connection = null;

    public static void openMySqlConnection() {
        String USER = RankupPlugin.getPlugin().mysql.getString("Username");
        String PASSWORD = RankupPlugin.getPlugin().mysql.getString("Password");
        String DATABASE = RankupPlugin.getPlugin().mysql.getString("Database");
        String HOST = RankupPlugin.getPlugin().mysql.getString("Host");
        int PORT = 3306;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE, USER, PASSWORD);
            createTable();
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RANKUP] §eMySQL carregado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RANKUP] §cErro ao carregar MySQL!");
        }
    }

    public static void openSQLiteConnection() {
        File file = new File(RankupPlugin.getPlugin().getDataFolder(), "rankup.db");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            createTable();
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RANKUP] §eSQLite carregado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RANKUP] §cErro no SQLite contacte-me no discord!");
        }
    }

    public static void createTable() {
        PreparedStatement stm = null;

        try {
            stm = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `ezn_rankup` (`player` VARCHAR(24) NULL, `uuid` VARCHAR(60) NULL, `rank` VARCHAR(250))");
            stm.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
