package io.github.Andrew_College.TradeMania;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class TradeMania extends JavaPlugin implements CommandExecutor {
	private ArrayList<PlayerStuff> playerLog;
	public void onEnable() {
		BufferedReader br = null;
		try {
			getLogger().info(
					"Reading Player backLog from file \"playerNews.log\"");
			br = new BufferedReader(new FileReader("playerNews.log"));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			br.close();
			
		} catch (IOException e) {
			try {
				if ((new File("playerNews.log")).createNewFile()) {
					getLogger().info("File didn't exist, now it does :D");
				} else {
					getLogger()
							.info("Problem reading from file \"playerNews.log\", corruption may have occured");
					getLogger().info("\"playerNews.log\" will be rewritten");
					File file = new File("playerNews.log");
					file.setWritable(true);
					BufferedWriter output = new BufferedWriter(new FileWriter(
							file));
					output.write("");
					output.close();
				}

			} catch (IOException e1) {
				getLogger().info(
						"End of world, the TradeMania corp. is responsible");
			}
		}
	}

	public void onDisable() {
		getLogger().info("Saving Player backLog to file \"playerNews.log\"");
		try {
			File file = new File("playerNews.log");
			file.setWritable(true);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write("");
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

	private class PlayerStuff{
		String Name = "";
		ArrayList<String> logs = null;
		PlayerStuff(String name){
			this.Name = name;
			this.logs = new ArrayList<String>(0);
		}
	}
}
