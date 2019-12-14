package pt.elevenzeronine.rankup.listeners;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pt.elevenzeronine.rankup.RankupPlugin;
import pt.elevenzeronine.rankup.utils.Utils;
import pt.elevenzeronine.rankup.database.DataManager;

public class RankupListener implements Listener {

    private Utils utils = new Utils();
    private WorldGuardPlugin worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");

    private void breakBlocksInCube(Player player, Block start, int radius) {
        Material type = start.getType();
        if (radius < 0)
            return;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (!worldGuard.canBuild(player, start.getRelative(x, y, z)))
                        return;

                    if (type == Material.LAPIS_ORE || type == Material.IRON_ORE || type == Material.DIAMOND_ORE || type == Material.EMERALD_ORE) {
                        start.getRelative(x, y, z).setType(Material.AIR);
                    }
                    start.getRelative(x, y, z).breakNaturally();
                }
            }
        }


    }

    @EventHandler
    public void onMineListener(BlockBreakEvent event) {
        if (!RankupPlugin.getPlugin().mineracao.getBoolean("use")) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material type = block.getType();

        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        boolean b = type == Material.LAPIS_ORE || type == Material.IRON_ORE || type == Material.DIAMOND_ORE || type == Material.EMERALD_ORE;

        if (!worldGuard.canBuild(player, block))
            return;

        if (player.getItemInHand().equals(RankupPlugin.getPlugin().pickaxe3x3)) {
            utils.explosive3(player, block, b);
            breakBlocksInCube(player, block, 1);
        } else if (player.getItemInHand().equals(RankupPlugin.getPlugin().pickaxe6x6)) {
            utils.explosive6(player, block, b);
            breakBlocksInCube(player, block, 2);
        } else if (player.getItemInHand().equals(RankupPlugin.getPlugin().pickaxe9x9)) {
            utils.explosive9(player, block, b);
            breakBlocksInCube(player, block, 3);
        } else {
            utils.noExplosive(player, block, b);
        }

    }


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
