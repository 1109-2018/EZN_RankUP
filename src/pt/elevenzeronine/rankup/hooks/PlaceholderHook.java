package pt.elevenzeronine.rankup.hooks;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import org.bukkit.entity.Player;
import pt.elevenzeronine.rankup.RankupPlugin;


public class PlaceholderHook {

    public PlaceholderHook() {
        PlaceholderAPI.registerPlaceholder(RankupPlugin.getPlugin(), "ezn_rank", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent placeholderReplaceEvent) {
                Player player = placeholderReplaceEvent.getPlayer();
                if (player == null) {
                    return "§cLoading...";
                }
                if (!RankupPlugin.getPlugin().playerRankHashMap.containsKey(player.getPlayer())) {
                    return "§cLoading...";
                }
                return RankupPlugin.getPlugin().utils.getRank(player.getPlayer()).getPrefix().replace("&", "§");
            }
        });
        PlaceholderAPI.registerPlaceholder(RankupPlugin.getPlugin(), "ezn_next_rank", new PlaceholderReplacer()
        {
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                Player player = e.getPlayer();
                if (player == null) {
                    return "§cLoading...";
                }
                if (RankupPlugin.getPlugin().utils.getNextRank(player) == null) {
                    return RankupPlugin.getPlugin().getConfig().getString("Placeholders.last-rank");
                }
                if (!RankupPlugin.getPlugin().playerRankHashMap.containsKey(player.getPlayer())) {
                    return "§cLoading...";
                }
                return RankupPlugin.getPlugin().utils.getNextRank(player.getPlayer()).getPrefix().replace("&", "§");
            }
        });
    }

}
