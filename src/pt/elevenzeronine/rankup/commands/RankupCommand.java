package pt.elevenzeronine.rankup.commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pt.elevenzeronine.rankup.RankupPlugin;
import pt.elevenzeronine.rankup.utils.Utils;
import pt.elevenzeronine.rankup.factory.Rank;
import pt.elevenzeronine.rankup.utils.HologramRankup;


public class RankupCommand implements CommandExecutor {

    private Utils utils = new Utils();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas players podem usar este comando");
            return true;
        }

        Player player = (Player) sender;


        if (cmd.getName().equalsIgnoreCase("rankup")) {
            if (args.length == 0) {
                for (String worlds : RankupPlugin.getPlugin().getConfig().getStringList("BlackList-Worlds")) {
                    if (player.getWorld().getName().equals(worlds)) {
                        for (String msg : RankupPlugin.getPlugin().getConfig()
                                .getStringList("BlackList-Message")) {
                            player.sendMessage(msg.replaceAll("&", "§"));
                        }
                        return true;
                    }
                }
                Rank r = utils.getRank(player);
                Rank nr = utils.getNextRank(player);
                if (!RankupPlugin.getPlugin().playerRankHashMap.containsKey(player)) {
                    player.playSound(player.getLocation(), Sound.BAT_DEATH, 1.0F, 1.0F);
                    for (String error : RankupPlugin.getPlugin().getConfig().getStringList("messages.invalid")) {
                        player.sendMessage(error.replaceAll("&", "§"));
                    }
                    return false;
                }
                if (nr == null) {
                    player.playSound(player.getLocation(), Sound.BAT_DEATH, 1.0F, 1.0F);
                    for (String error : RankupPlugin.getPlugin().getConfig().getStringList("messages.last_rank")) {
                        player.sendMessage(error.replaceAll("&", "§"));
                    }
                    return false;
                }
                for (String success : RankupPlugin.getPlugin().getConfig().getStringList("messages.rank_found")) {
                    player.sendMessage(success.replaceAll("&", "§")
                            .replace("%rank%", r.getPrefix())
                            .replace("%next_rank%", nr.getPrefix()));
                }
                HologramRankup.rankup(player);
            }
        }

        return false;
    }
}
