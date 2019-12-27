package pt.elevenzeronine.rankup.database;


import org.bukkit.entity.Player;
import pt.elevenzeronine.rankup.RankupPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataManager {


    public static boolean hasPlayer(Player p) {
        try {
            PreparedStatement stm = ConnectionManager.connection.prepareStatement("SELECT * FROM `ezn_rankup` WHERE `uuid` = ?");
            stm.setString(1, p.getUniqueId().toString());
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getPlayer(Player p) {
        if (hasPlayer(p)) {
            try {
                PreparedStatement stm = ConnectionManager.connection.prepareStatement("SELECT * FROM `ezn_rankup` WHERE `uuid` = ?");
                stm.setString(1, p.getUniqueId().toString());
                ResultSet rs = stm.executeQuery();
                if (rs.next()) {
                    return rs.getString("rank");
                }
                return "";
            } catch (SQLException e) {
                return "";
            }
        }
        addPlayer(p);
        return getPlayer(p);
    }


    public static void setPlayer(Player p, String rank) {
        try {
            PreparedStatement st = ConnectionManager.connection.prepareStatement("UPDATE `ezn_rankup` SET `rank` = ? WHERE `uuid` = ?");
            st.setString(1, rank);
            st.setString(2, p.getUniqueId().toString());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPlayer(Player p) {
        try {
            PreparedStatement st = ConnectionManager.connection.prepareStatement("INSERT INTO `ezn_rankup`(`player`, `uuid`, `rank`) VALUES (?,?,?)");
            st.setString(1, p.getName());
            st.setString(2, p.getUniqueId().toString());
            st.setString(3, RankupPlugin.getPlugin().defaultRank.getRank());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
