package io.github.Andrew_College.TradeMania;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class TradeMania extends JavaPlugin implements CommandExecutor {
	private HashMap<String, ArrayList<String>> playerLog;

	public void onEnable() {
		BufferedReader br = null;
		try {
			getLogger().info(
					"Reading Player backLog from file \"playerNews.log\"");
			br = new BufferedReader(new FileReader("playerNews.log"));
			HashMap<String, ArrayList<String>> inputInfo = new HashMap<String, ArrayList<String>>();
			String line = null;
			String lName = null;
			do {
				line = br.readLine();
				////////////
				///////////
				//do line parsing stuff here
				/////////
				////////
				if(line.contains("Name;")){ 
					lName = line.substring(line.indexOf(" ")+1);
				}
				////////////
				///////////
				//done* line parsing stuff
				/////////
				////////
				ArrayList<String> existing = inputInfo.get(lName);//change with line name
				if(existing == null){//the key, value pair doesn't exist
					inputInfo.put(lName, new ArrayList<String>());
					inputInfo.get(lName).add(line);
				}else if(line.contains("Log;")){//Key, value pair does exist
					existing.add(line.substring(line.indexOf(" ")+1));
					inputInfo.put(lName, existing);
				}
			} while (line != null);
			br.close();

		} catch (IOException e) {//The file doesn't exist or messing has occurred
			try {
				File file = new File("playerNews.log");
				if ((new File("playerNews.log")).createNewFile()) {
					getLogger().info(
							"\"playerNews.log\" didn't exist, now it does :D");
				} else {
					getLogger()
							.info("Problem reading from file \"playerNews.log\", corruption may have occured");
					getLogger().info("\"playerNews.log\" will be rewritten");
					file = new File("playerNews.log");
					file.setWritable(true);
					
				}

			} catch (IOException e1) {
				getLogger().info(
						"End of world, the TradeMania corp. is potentially not responsible");
			}
		}
	}

	public void onDisable() {
		getLogger().info("Saving Player backLog to file \"playerNews.log\"");
		try {
			File file = new File("playerNews.log");
			file.setWritable(true);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write("");//Clear contents of file, the entirety of the file should be in-game
			for (Map.Entry<String, ArrayList<String>> entry : playerLog.entrySet()) {
				output.append("Name; " + entry.getKey() + "\n");
				for(String log: entry.getValue()){
					output.append("Log; " + log+"\n");
				}
			}
			output.close();
		} catch (IOException e) {

		}
		getLogger().info("Saved Player backLog to file \"playerNews.log\"");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (cmd.getName().equalsIgnoreCase("mkTrade")) {
			if (!(sender instanceof Player) && args.length < 2) {
				sender.sendMessage("This is the player-specific command.");
				sender.sendMessage("You need to specify a recipient.");
				sender.sendMessage("e.g. \"/mkTrade [recipient] [player] etc.\"");
				return false;
			}
			if (!mkTrade(sender, args)) {
				sender.sendMessage("Sorry, something went wrong in mkTrade");
				return false;
			}
			return true;
		}

		return false;
	}

	private boolean mkTrade(CommandSender sender, String[] args) {
		try {
			ItemStack tradePost = new ItemStack(Material.SIGN_POST, 1);
			ItemMeta data = tradePost.getItemMeta();
			data.setDisplayName("TradePost");
			tradePost.setItemMeta(data);
			Player player = Bukkit.getPlayer(sender.getName());
			player.getInventory().addItem(tradePost);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private class PlayerStuff {
		String Name = "";
		ArrayList<String> logs = null;

		PlayerStuff(String name) {
			this.Name = name;
			this.logs = new ArrayList<String>(0);
		}
	}
}
