package pt.elevenzeronine.rankup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pt.elevenzeronine.rankup.RankupPlugin;
import pt.elevenzeronine.rankup.factory.Rank;

public class RanksCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas players podem usar esse comando");
            return true;
        }


        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("ranks")) {
            if (args.length == 0) {
                for (String msg : RankupPlugin.getPlugin().getConfig().getStringList("commands.command_ranks")) {
                    player.sendMessage(msg.replaceAll("&", "§"));
                }
                return true;
            }


            if (args[0].equalsIgnoreCase("listar")) {
                for (String msg : RankupPlugin.getPlugin().getConfig().getStringList("commands.ranks_list")) {
                    player.sendMessage(msg.replaceAll("&","§"));
                }
                for (Rank rank : RankupPlugin.getPlugin().utils.getRanks()) {
                    player.sendMessage(RankupPlugin.getPlugin().getConfig().getString("commands.ranks")
                            .replace("%position%", ""+rank.getPosition())
                            .replace("%price%", RankupPlugin.getPlugin().utils.getFormat(rank.getPrice()))
                            .replace("%prefix%", rank.getPrefix())
                            .replaceAll("&", "§"));
                }
                player.sendMessage("");
                return true;
            }

            if (player.hasPermission("ijrankup.admin")) {
                if (args[0].equalsIgnoreCase("reload")) {
                    RankupPlugin.getPlugin().reloadConfig();
                    RankupPlugin.getPlugin().ranks.reloadConfig();
                    RankupPlugin.getPlugin().mysql.reloadConfig();
                    player.sendMessage("§aConfig reloaded!");
                    return true;
                }
            } else {
                player.sendMessage(RankupPlugin.getPlugin().getConfig().getString("commands.no_perm").replaceAll("&", "§"));
                return true;
            }
        }


        return false;
    }
}
