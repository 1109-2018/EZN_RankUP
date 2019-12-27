package pt.elevenzeronine.rankup.utils;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pt.elevenzeronine.rankup.RankupPlugin;
import pt.elevenzeronine.rankup.factory.Rank;

import java.util.Map;


public class HologramRankup {



    public  Map<String, ArmorStand> asRemove;
    public  Map<String, Hologram> infoHologram;
    public  Map<String, Hologram> acceptHologram;
    public  Map<String, Hologram> cancelHologram;
    public  Utils utils = new Utils();

    @SuppressWarnings("deprecation")
    public static void rankup(Player player) {

        if (RankupPlugin.getPlugin().hologramRankup.acceptHologram.containsKey(player.getName()))
            return;

        Rank r = RankupPlugin.getPlugin().utils.getRank(player);
        Rank nr = RankupPlugin.getPlugin().utils.getNextRank(player);
        double price = nr.getPrice();

        //
        Location userLocation = player.getLocation().add(0, 2, 0).clone();
        userLocation.setPitch(0F);
        Vector direction = userLocation.clone().getDirection();

        Vector position = direction.clone().multiply(2.6).setY(0).add(direction.clone().crossProduct(new Vector(0, 1, 0)).multiply(0));
        Location location = userLocation.clone().add(0, .4, 0).add(position);

        Vector positionSides = direction.clone().multiply(2).setY(0);
        Location locationLeft = userLocation.clone().add(0, .4, 0).add(positionSides.clone().add(direction.clone().crossProduct(new Vector(0, 1, 0)).multiply(-1.9)));
        Location locationRight = userLocation.clone().add(0, .4, 0).add(positionSides.add(direction.clone().crossProduct(new Vector(0, 1, 0)).multiply(1.9)));
        //

        Hologram hologramInfo = HologramsAPI.createHologram(RankupPlugin.getPlugin(), location);
        hologramInfo.getVisibilityManager().setVisibleByDefault(false);
        hologramInfo.getVisibilityManager().showTo(player);

        Hologram hologramConfirm = HologramsAPI.createHologram(RankupPlugin.getPlugin(), locationLeft);
        hologramConfirm.getVisibilityManager().setVisibleByDefault(false);
        hologramConfirm.getVisibilityManager().showTo(player);

        Hologram hologramCancel = HologramsAPI.createHologram(RankupPlugin.getPlugin(), locationRight);
        hologramCancel.getVisibilityManager().setVisibleByDefault(false);
        hologramCancel.getVisibilityManager().showTo(player);

        RankupPlugin.getPlugin().hologramRankup.infoHologram.put(player.getName(), hologramInfo);
        RankupPlugin.getPlugin().hologramRankup.acceptHologram.put(player.getName(), hologramConfirm);
        RankupPlugin.getPlugin().hologramRankup.cancelHologram.put(player.getName(), hologramCancel);

        //
        ArmorStand as = player.getLocation().getWorld().spawn(player.getLocation(), ArmorStand.class);
        as.setGravity(false);
        as.setVisible(false);
        as.setCustomNameVisible(false);
        as.setHelmet(new ItemStack(RankupPlugin.getPlugin().getConfig().getInt("Hologram.bench.id")
                , 1, (short) RankupPlugin.getPlugin().getConfig().getInt("Hologram.bench.data")));
        as.setPassenger(player);
        RankupPlugin.getPlugin().hologramRankup.asRemove.put(player.getName(), as);
        //


        TouchHandler touchHandlerConfirm = player1 -> {
            RankupPlugin.getPlugin().utils.deleteHolograms(player.getName());

            if ((RankupPlugin.getPlugin().economy.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())) >= price)) {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                for (String success : RankupPlugin.getPlugin().getConfig().getStringList("messages.rankup")) {
                    player.sendMessage(success.replaceAll("&", "§")
                            .replace("%price%", RankupPlugin.getPlugin().utils.getFormat(price))
                            .replace("%next_rank%", nr.getPrefix()));
                }
                RankupPlugin.getPlugin().economy.withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), nr.getPrice());
                RankupPlugin.getPlugin().utils.rankUP(player);
                for (String broadcast : RankupPlugin.getPlugin().getConfig().getStringList("messages.broadcast_rankup")) {
                    Bukkit.broadcastMessage(broadcast.replaceAll("&", "§")
                            .replace("%player_name%", player.getName())
                            .replace("%rank%", RankupPlugin.getPlugin().utils.getRank(player).getPrefix()));
                }
                RankupPlugin.getPlugin().utils.sendActionBar(player, RankupPlugin.getPlugin().getConfig().getString("messages.actionbar_rankup")
                        .replace("%player_name%", player.getName())
                        .replace("%rank%", RankupPlugin.getPlugin().utils.getRank(player).getPrefix())
                        .replaceAll("&", "§"));
            } else {
                String pricedf = "0k";

                if (price - RankupPlugin.getPlugin().economy.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())) >= 0.0D) {
                    pricedf = RankupPlugin.getPlugin().utils.getFormat(price - RankupPlugin.getPlugin().economy.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())));
                }

                player.playSound(player.getLocation(), Sound.BAT_DEATH, 1.0F, 1.0F);
                for (String error : RankupPlugin.getPlugin().getConfig().getStringList("messages.no_money")) {
                    player.sendMessage(error.replaceAll("&", "§")
                            .replace("%price%", pricedf));
                }
            }
        };

        TouchHandler touchHandlerCancel = player1 -> {
            RankupPlugin.getPlugin().utils.deleteHolograms(player.getName());
            player.playSound(player.getLocation(), Sound.BAT_DEATH, 1.0F, 1.0F);
            for (String cancel : RankupPlugin.getPlugin().getConfig().getStringList("messages.cancel")) {
                player.sendMessage(cancel.replaceAll("&", "§")
                        .replace("%next_rank%", nr.getPrefix().replaceAll("&", "§")));
            }
        };


        for (String hologramAccept : RankupPlugin.getPlugin().getConfig().getStringList("Hologram.accept")) {
            hologramConfirm.appendTextLine(hologramAccept.replaceAll("&", "§")).setTouchHandler(touchHandlerConfirm);
        }

        for (String hologramInformation : RankupPlugin.getPlugin().getConfig().getStringList("Hologram.info")) {
            hologramInfo.appendTextLine(hologramInformation.replace("%rank%", r.getPrefix())
                    .replace("%next_rank%", nr.getPrefix())
                    .replace("%price%", RankupPlugin.getPlugin().utils.getFormat(nr.getPrice()))
                    .replaceAll("&", "§"));
        }

        for (String hologramDeny : RankupPlugin.getPlugin().getConfig().getStringList("Hologram.cancel")) {
            hologramCancel.appendTextLine(hologramDeny.replaceAll("&", "§")).setTouchHandler(touchHandlerCancel);
        }


    }

}
