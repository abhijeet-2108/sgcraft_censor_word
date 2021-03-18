package me.arrow.sgcraft_censor_word;

import net.minecraft.server.v1_16_R3.ICrafting;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

import java.util.*;

public class Main extends JavaPlugin implements Listener, TabCompleter {
	private FileConfiguration config = this.getConfig();
	@Override
	public void onEnable() {
		//this.config = YamlConfiguration.loadConfiguration(new File("plugins/data/test.yml"));
		//this.saveDefaultConfig();
		this.saveConfig();
		PluginManager join = getServer().getPluginManager();
		join.registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		
	}
	public void loadConfig() {
		config.options().copyDefaults(true);
		saveConfig();
	}
	
	
	private int amount = this.getConfig().getInt("wordconfig.amount");
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(e.isCancelled()) {
			return;
		}
			
		Player player = (Player) e.getPlayer();
		String msg = e.getMessage();
		Boolean is = false;
		//player.sendMessage("§4§l Dont swear");
		//player.sendMessage(words[1]);
		//player.sendMessage("§4§l Dont swear");
		//String[] words = msg.split(" ");
		String formatted =msg;
		//player.sendMessage(String.valueOf(msg.length()));
//		for(int j=0;j<words.length;j++){
//			String format = words[j];

			for(int i=0;i<amount;i++) {
				String sentence=this.getConfig().getString("filter.word"+i+".word");
				//Bukkit.broadcastMessage(sentence);
				formatted = formatted.replaceAll("(?i)"+sentence,this.getConfig().getString("filter.word"+i+".replaceword")+" ");

				e.setMessage(formatted);

			}
	}

	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		List<String> list = new ArrayList<>();

		if (sender instanceof Player) {
			if (cmd.getName().equalsIgnoreCase("censorword")) {
				if (!sender.hasPermission("censorword.wordconfig.service")) {
					sender.sendMessage(ChatColor.RED + "You don't have permission");
					return null;
				}
				if (args.length == 1) {

					list.add("add");
					list.add("reload");
					list.add("help");
					return list;
				}
			}
		}

		return null;
	}

	@Override
	public boolean onCommand (CommandSender sender, Command cmd, String lable, String[] args) {
		if(lable.equalsIgnoreCase("censorword")) {
			Player player = (Player) sender;

			if (!player.hasPermission("censorword.wordconfig.service")) {
				player.sendMessage(ChatColor.RED + "You don't have permission");
				return true;
			}
			if (args.length == 0) {
				player.sendMessage("§kaa §2Incorrect use, Type §6/censorword help §2for more info  §f§kaa ");
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				this.saveConfig();
				this.reloadConfig();

				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("wordconfig.reload")));
			} else if (args[0].equalsIgnoreCase("add")) {
				if(args.length == 3 ){
					config.getString("filter.word" + amount + ".word");
					String[] str1 = args[1].split("");
					String str2 = "";
					for(int i=0;i<args[1].length();i++){
						if(i == 0){
							str2 += "(";
							str2 += str1[i];
						}
						else if(i == (args[1].length() - 1)){
							str2 += "+(\\W|\\d|_)*";
							str2 += str1[i];
							str2 += "+(\\W|\\d|_)*)";
						}
						else{

							str2 += "+(\\W|\\d|_)*";
							str2 += str1[i];
						}
					}
					config.set("filter.word" + amount + ".word", str2);
					config.set("filter.word" + amount + ".replaceword", args[2]);
					amount++;
					config.set("wordconfig.amount", amount);
					saveConfig();
					player.sendMessage("§6Added word §2" + args[2] + " §6for replacing word §2" + args[1]);
				}else {
					player.sendMessage("§kaa §2Incorrect use, §6Type /censorword help §2for more info  §f§kaa ");
					return true;
				}

			} else if (args[0].equalsIgnoreCase("help")) {
				for (String msg : this.getConfig().getStringList("wordconfig.help")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
				}
			} else {
				player.sendMessage("§kaa §2Incorrect use, Type §6/censorword help §2for more info  §f§kaa ");
			}

		}
		return true;
	}

	
	
}
