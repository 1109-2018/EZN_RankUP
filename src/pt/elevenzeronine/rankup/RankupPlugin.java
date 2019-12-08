package pt.elevenzeronine.rankup;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pt.elevenzeronine.rankup.utils.HologramRankup;
import pt.elevenzeronine.rankup.utils.objects.SpigotConfig;
import pt.elevenzeronine.rankup.utils.Utils;
import pt.elevenzeronine.rankup.utils.objects.ItemBuilder;
import pt.elevenzeronine.rankup.hooks.PlaceholderHook;
import pt.elevenzeronine.rankup.commands.PickaxesCommand;
import pt.elevenzeronine.rankup.commands.RanksCommand;
import pt.elevenzeronine.rankup.database.ConnectionManager;
import pt.elevenzeronine.rankup.database.DataManager;
import pt.elevenzeronine.rankup.listeners.*;
import pt.elevenzeronine.rankup.factory.Rank;
import pt.elevenzeronine.rankup.commands.RankupCommand;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;

public class RankupPlugin extends JavaPlugin {

    public Utils utils = new Utils();
    public HologramRankup hologramRankup = new HologramRankup();

    public Permission permission = null;
    public Chat chat = null;
    public Economy economy = null;
    public SpigotConfig ranks;
    public SpigotConfig mysql;
    public SpigotConfig mineracao;
    public HashMap<Player, Rank> playerRankHashMap = new HashMap();
    public ArrayList<Rank> rankArrayList = new ArrayList();
    public ItemStack pickaxe3x3 = new ItemBuilder(Material.DIAMOND_PICKAXE, 1)
            .setName("§6Picareta Explosiva 3").setLore("§eQuebra 18 blocos de cada vez")
            .addEnchant(Enchantment.DURABILITY, 3).toItemStack();
    public ItemStack pickaxe6x6 = new ItemBuilder(Material.DIAMOND_PICKAXE, 1)
            .setName("§6Picareta Explosiva 6").setLore("§eQuebra 77 blocos de cada vez")
            .addEnchant(Enchantment.DURABILITY, 6).toItemStack();
    public ItemStack pickaxe9x9 = new ItemBuilder(Material.DIAMOND_PICKAXE, 1)
            .setName("§6Picareta Explosiva 9").setLore("§eQuebra 196 blocos de cada vez")
            .addEnchant(Enchantment.DURABILITY, 9).toItemStack();

    private ConnectionManager connectionMySql;

    public Rank defaultRank;

    @Override
    public void onEnable() {
        if (!getPlugin().getName().equalsIgnoreCase("EZN_RANKUP")) {
            Bukkit.getPluginManager().disablePlugin(this);
            int i;
            for (i =1; i <=50; i++) {
                Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §cNao mudes o nome do plugin, para ele funcionar!");
            }
            return;
        }


        hologramRankup.asRemove = new HashMap<>();
        hologramRankup.infoHologram = new HashMap<>();
        hologramRankup.cancelHologram = new HashMap<>();
        hologramRankup.acceptHologram = new HashMap<>();

        ranks = new SpigotConfig("ranks.yml");
        if (!ranks.exists()) {
            ranks.saveDefaultConfig();
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §eConfiguracao ranks.yml foi criada!");
        } else {
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §eConfiguracao ranks.yml carregada!");
        }

        mysql = new SpigotConfig("mysql.yml");
        if (!mysql.exists()) {
            mysql.saveDefaultConfig();
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §eConfiguracao mysql.yml foi criada!");
        } else {
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §eConfiguracao mysql.yml carregada!");
        }

        mineracao = new SpigotConfig("mineracao.yml");
        if (!mineracao.exists()) {
            mineracao.saveDefaultConfig();
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §eConfiguracao mineracao.yml foi criada!");
        } else {
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §eConfiguracao mineracao.yml carregada!");
        }

        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §eConfiguracao config.yml foi criada!");
        } else {
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §eConfiguracao config.yml carregada!");
        }




        getCommand("rankup").setExecutor(new RankupCommand());
        getCommand("ranks").setExecutor(new RanksCommand());
        if (mineracao.getBoolean("use")) {
            getCommand("givepickaxe").setExecutor(new PickaxesCommand());
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §eTodos os eventos foram carregados!");
        } else {
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §cDesativado sistema de mineracao!");
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §eTodos os eventos foram carregados!");
        }
        Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §eTodos os comandos foram carregados!");

        loadRanks();
        loadVault();
        registerListeners();

        connectionMySql = new ConnectionManager(this, "`player` VARCHAR(24) NULL, `uuid` VARCHAR(60) NULL, `rank` VARCHAR(250)",
                mysql.getString("Host")
                , mysql.getString("Username")
                , mysql.getString("Password")
                , mysql.getString("Database")
                , "ezn_rankup");
        runnableRanks();

        if (Bukkit.getPluginManager().getPlugin("MVdWPlaceholderAPI") != null && Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
            new PlaceholderHook();

            Bukkit.getConsoleSender().sendMessage("§7[Legendchat] EZN_RankUP added custom placeholder {ezn_rank}");
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §ePlaceHolders carregadas!");
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!DataManager.hasPlayer(player))
                DataManager.addPlayer(player);
            playerRankHashMap.put(player, getPlugin().utils.getRank(DataManager.getPlayer(player)));
        }
        try {
            InetAddress IP = InetAddress.getLocalHost();
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §6Plugin ativado no ip: " + IP.getHostAddress() + " na v"
                    + getDescription().getVersion());
            Bukkit.getConsoleSender().sendMessage("§7[EZN_RankUP] §6Reportar problemas: 1109#0191");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDisable() {

        savePlayers();
        try {
            RankupPlugin.getPlugin().getMySqlConnection().close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        playerRankHashMap.clear();
        rankArrayList.clear();
        Map<String, ArmorStand> localhash = hologramRankup.asRemove;


        if(localhash.isEmpty()) {
            return;
        }
        for (Map.Entry me : localhash.entrySet()) {
                ArmorStand as = localhash.get(me.getKey());
                as.remove();
        }

    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new RankupListener(), this);
        Bukkit.getPluginManager().registerEvents(new HologramEvents(), this);
    }

    private void savePlayers() {
        for (Player p : playerRankHashMap.keySet()) {
            DataManager.setPlayer(p, playerRankHashMap.get(p).getRank());
        }
        if (getConfig().getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage("§7[§fRankUP§7] Todos os players foram salvos");
        }
    }

    private void runnableRanks() { (new BukkitRunnable()
    {
        public void run() {
            savePlayers();
        }
    }).runTaskTimer(this, 180*20, 180*20); }

    private void loadRanks() {
        for (String rank : RankupPlugin.getPlugin().ranks.getSection("Ranks").getKeys(false)) {
            String nome = rank;
            String prefix = RankupPlugin.getPlugin().ranks.getString("Ranks." + rank + ".prefix").replace("&", "§");
            int posicao = RankupPlugin.getPlugin().ranks.getInt("Ranks." + rank + ".position");
            boolean defaultb = false;
            if (RankupPlugin.getPlugin().ranks.contains("Ranks." + rank + ".default")) {
                defaultb = RankupPlugin.getPlugin().ranks.getBoolean("Ranks." + rank + ".default");
            }
            if (defaultb) {
                Rank r = new Rank(nome, prefix, posicao, 0.0D, null, defaultb);
                rankArrayList.add(r);
                defaultRank = r; continue;
            }
            double price = RankupPlugin.getPlugin().ranks.getDouble("Ranks." + rank + ".price");
            List<String> commands = RankupPlugin.getPlugin().ranks.getStringList("Ranks." + rank + ".commands");
            Rank r = new Rank(nome, prefix, posicao, price, commands, defaultb);
            rankArrayList.add(r);
        }
    }

    public static RankupPlugin getPlugin() {
        return getPlugin(RankupPlugin.class);
    }

    private void loadVault() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager()
                .getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
                .getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (permissionProvider != null)
            permission = permissionProvider.getProvider();

    }


    public ConnectionManager getMySqlConnection() {
        return connectionMySql;
    }

}
