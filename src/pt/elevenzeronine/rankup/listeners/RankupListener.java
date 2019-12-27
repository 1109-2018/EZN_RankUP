package pt.elevenzeronine.rankup.listeners;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pt.elevenzeronine.rankup.RankupPlugin;
import pt.elevenzeronine.rankup.utils.Utils;
import pt.elevenzeronine.rankup.database.DataManager;

public class RankupListener implements Listener {

    private Utils utils = new Utils();


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!RankupPlugin.getPlugin().playerRankHashMap.containsKey(e.getPlayer())) {
            if (!DataManager.hasPlayer(e.getPlayer())) {
                DataManager.addPlayer(e.getPlayer());
            }
            RankupPlugin.getPlugin().playerRankHashMap.put(e.getPlayer(), RankupPlugin.getPlugin().utils.getRank(DataManager.getPlayer(e.getPlayer())));
        }
    }

    @EventHandler
    public void onPlayerAquit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DataManager.setPlayer(player,
                RankupPlugin.getPlugin().playerRankHashMap.get(player).getRank());
    }

    @EventHandler
    public void onChatMessage(ChatMessageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!event.getTags().contains("ezn_rank")) {
            return;
        }
        event.setTagValue("ezn_rank", utils.getRank(event.getSender()).getPrefix());
    }

}
