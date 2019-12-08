package pt.elevenzeronine.rankup.listeners;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pt.elevenzeronine.rankup.RankupPlugin;

public class HologramEvents implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (RankupPlugin.getPlugin().hologramRankup.infoHologram.containsKey(player.getName())) {
            Hologram hologram = RankupPlugin.getPlugin().hologramRankup.infoHologram.get(player.getName());

            if (hologram.getLocation().distance(player.getLocation()) >= 5) {
                RankupPlugin.getPlugin().utils.deleteHolograms(player.getName());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (RankupPlugin.getPlugin().hologramRankup.infoHologram.containsKey(player.getName())) {
            RankupPlugin.getPlugin().utils.deleteHolograms(player.getName());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (RankupPlugin.getPlugin().hologramRankup.infoHologram.containsKey(player.getName())) {
            RankupPlugin.getPlugin().utils.deleteHolograms(player.getName());
        }
    }

}
