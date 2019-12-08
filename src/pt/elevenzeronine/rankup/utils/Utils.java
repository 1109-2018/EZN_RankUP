package pt.elevenzeronine.rankup.utils;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import pt.elevenzeronine.rankup.RankupPlugin;
import pt.elevenzeronine.rankup.factory.Rank;
import pt.elevenzeronine.rankup.events.ChangeRankEvent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class Utils {

    public Rank getRank(Player player) {
        return RankupPlugin.getPlugin().playerRankHashMap.get(player);
    }

    private Rank getRank(int position) {
        return RankupPlugin.getPlugin().rankArrayList.stream()
                .filter(r -> r.getPosition() == position).findFirst().orElse(null);
    }

    public Rank getRank(String rank) {
        return RankupPlugin.getPlugin().rankArrayList.stream()
                .filter(r -> r.getRank().equalsIgnoreCase(rank)).findFirst().orElse(null);
    }

    private void setRank(Player player, Rank r, Rank f) {
        if (r.getCommands() != null) {
            for (String cmd : r.getCommands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%rank%", r.getRank())
                        .replace("%old_rank%", f.getRank()).replace("%player%", player.getName()));
            }
        }
        RankupPlugin.getPlugin().playerRankHashMap.replace(player, r);

        Bukkit.getPluginManager().callEvent(new ChangeRankEvent(player, r));
    }

    public Rank getNextRank(Player p) {
        return getRank(getRank(p).getPosition() + 1);
    }

    public void rankUP(Player player) {
        setRank(player, getNextRank(player), getRank(player));
    }

    public List<Rank> getRanks() {
        return RankupPlugin.getPlugin().rankArrayList;
    }

    //end ranks

    //others

    public String format(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(new Locale("pt", "BR")));
        return decimalFormat.format(value);
    }

    public String getFormat(double value) {
        String[] simbols = {"", "k", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D", "UN", "DD", "TD",
                "QD", "QID", "SD", "SSD", "OD", "ND"};
        int index;
        for (index = 0; value / 1000.0D >= 1.0D; ) {
            value /= 1000.0D;
            index++;
        }

        return String.valueOf(format(value)) + simbols[index];
    }

    public void deleteHolograms(String playerName) {
        Hologram hologram = RankupPlugin.getPlugin().hologramRankup.infoHologram.get(playerName);
        Hologram hologram2 = RankupPlugin.getPlugin().hologramRankup.acceptHologram.get(playerName);
        Hologram hologram3 = RankupPlugin.getPlugin().hologramRankup.cancelHologram.get(playerName);

        ArmorStand as = RankupPlugin.getPlugin().hologramRankup.asRemove.get(playerName);
        as.remove();

        hologram2.delete();
        hologram3.delete();
        hologram.delete();

        RankupPlugin.getPlugin().hologramRankup.infoHologram.remove(playerName);
        RankupPlugin.getPlugin().hologramRankup.acceptHologram.remove(playerName);
        RankupPlugin.getPlugin().hologramRankup.cancelHologram.remove(playerName);
        RankupPlugin.getPlugin().hologramRankup.asRemove.remove(playerName);
    }

    public void sendActionBar(Player p, String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
        (((CraftPlayer) p).getHandle()).playerConnection.sendPacket(packet);
    }

    //end others

    //Break Event

    private void depositMoney(Player player, double d) {
        RankupPlugin.getPlugin().economy.depositPlayer(player, d);
    }

    public void explosive3(Player player, Block block, boolean b) {
        if (b) {
            depositMoney(player, (RankupPlugin.getPlugin().mineracao.getDouble("money") * 3));
        }
        sendActionBar(player, RankupPlugin.getPlugin().mineracao.getString("actionbar_2")
                .replaceAll("&", "§")
                .replace("%bloco%", MaterialName.valueOf(block.getType()).getName().toString())
                .replace("%money%", format(RankupPlugin.getPlugin().mineracao.getDouble("money") * 3)));
    }

    public void explosive6(Player player, Block block, boolean b) {
        if (b) {
            depositMoney(player, (RankupPlugin.getPlugin().mineracao.getDouble("money") * 6));
        }
        sendActionBar(player, RankupPlugin.getPlugin().mineracao.getString("actionbar_2")
                .replaceAll("&", "§")
                .replace("%bloco%", MaterialName.valueOf(block.getType()).getName().toString())
                .replace("%money%", getFormat(RankupPlugin.getPlugin().mineracao.getDouble("money") * 6)));
    }

    public void explosive9(Player player, Block block, boolean b) {
        if (b) {
            depositMoney(player, (RankupPlugin.getPlugin().mineracao.getDouble("money") * 9));
        }
        sendActionBar(player, RankupPlugin.getPlugin().mineracao.getString("actionbar_2")
                .replaceAll("&", "§")
                .replace("%bloco%", MaterialName.valueOf(block.getType()).getName().toString())
                .replace("%money%", getFormat(RankupPlugin.getPlugin().mineracao.getDouble("money") * 9)));
    }

    public void noExplosive(Player player, Block block, boolean b) {
        if (b) {
            if (player.getItemInHand().getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS) == 0) {
                depositMoney(player, RankupPlugin.getPlugin().mineracao.getDouble("money"));
                sendActionBar(player, RankupPlugin.getPlugin().mineracao.getString("actionbar_1")
                        .replaceAll("&", "§")
                        .replace("%bloco%", MaterialName.valueOf(block.getType()).getName().toString())
                        .replace("%money%", getFormat(RankupPlugin.getPlugin().mineracao.getDouble("money"))));
                block
                        .setType(Material.AIR);
                return;
            }
            depositMoney(player, (RankupPlugin.getPlugin().mineracao.getDouble("money") *
                    player.getItemInHand().getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS)));
            sendActionBar(player, RankupPlugin.getPlugin().mineracao.getString("actionbar_1")
                    .replaceAll("&", "§")
                    .replace("%bloco%", MaterialName.valueOf(block.getType()).getName().toString())
                    .replace("%money%", getFormat(RankupPlugin.getPlugin().mineracao.getDouble("money")
                            * player.getItemInHand().getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS))));

            block.setType(Material.AIR);
        } else {
            if (RankupPlugin.getPlugin().mineracao.getBoolean("actionbar_0.use")) {
                sendActionBar(player, RankupPlugin.getPlugin().mineracao.getString("actionbar_0.message")
                        .replaceAll("&", "§")
                        .replace("%bloco%", MaterialName.valueOf(block.getType()).getName().toString()));
            }
        }
    }


}
