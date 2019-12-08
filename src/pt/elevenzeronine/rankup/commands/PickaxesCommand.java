package pt.elevenzeronine.rankup.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pt.elevenzeronine.rankup.RankupPlugin;

public class PickaxesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {



        if (cmd.getName().equalsIgnoreCase("givepickaxe")) {
            if (args.length == 0 ) {
                sender.sendMessage("");
                sender.sendMessage("§fSintaxe: /givepickaxe <player> 3/6/9");
                sender.sendMessage("");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                if (args.length < 2) {
                    sender.sendMessage("");
                    sender.sendMessage("§fSintaxe: /givepickaxe <player> 3/6/9");
                    sender.sendMessage("");
                    return true;
                }
                if (args[1].equalsIgnoreCase("3")) {
                    target.getInventory().addItem(RankupPlugin.getPlugin().pickaxe3x3);
                    return true;
                }
                if (args[1].equalsIgnoreCase("6")) {
                    target.getInventory().addItem(RankupPlugin.getPlugin().pickaxe6x6);
                    return true;
                }
                if (args[1].equalsIgnoreCase("9")) {
                    target.getInventory().addItem(RankupPlugin.getPlugin().pickaxe9x9);
                    return true;
                }
            }
        }
        return false;
    }

}
