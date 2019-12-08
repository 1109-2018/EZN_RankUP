package pt.elevenzeronine.rankup.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ConnectionManager {

    private Connection connection;
    private Properties properties;

    private String tableName;
    private String pluginName;

    private String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    private final String MAX_POOL = "250";

    public ConnectionManager(JavaPlugin plugin, String parameters, String address, String username, String password,
                           String databaseName,String tableName) {

        this.pluginName = plugin.getName();
        this.tableName = tableName;

        try {
            String USERNAME = username;
            String PASSWORD = password;
            String DATABASE_URL = "jdbc:mysql://"+address+":3306/"+databaseName;

            Class.forName(DATABASE_DRIVER);
            connection = DriverManager.getConnection(DATABASE_URL, getProperties(USERNAME,PASSWORD));

            Statement stmt = connection.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + parameters + ");";

            stmt.execute(sql);
            stmt.close();
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §eMySql conectado!");
        } catch (Exception e) {
            Bukkit.getConsoleSender()
                    .sendMessage("§7[" + pluginName.toUpperCase() + "] §cOcorreu um erro ao tentar conectar ao mysql");
        }
    }


    private Properties getProperties(String USERNAME, String PASSWORD) {
        if (properties == null) {
            properties = new Properties();
            properties.setProperty("user", USERNAME);
            properties.setProperty("password", PASSWORD);
            properties.setProperty("MaxPooledStatements", MAX_POOL);
            properties.setProperty("connectTimeout", "0");
            properties.setProperty("socketTimeout", "0");
        }
        return properties;
    }

    public String getTableName() {
        return tableName;
    }

    public Connection getConnection() {
        return connection;
    }


    public void close() throws SQLException {
        connection.close();
    }

}

