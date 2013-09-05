package io.github.Andrew_College.TradeMania;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.Andrew_College.TradeMania.ClickSign;

public final class TradeMania extends JavaPlugin implements CommandExecutor {

	public static HashMap<String, ArrayList<SignData>> tradingSigns;
	public final ClickSign onPlayerClick = new ClickSign();
	private HashMap<String, ArrayList<String>> playerLog;
	private boolean Intervention = true;

	public void onEnable() {

		getServer().getPluginManager().registerEvents(this.onPlayerClick, this);

		BufferedReader br = null;
		try {
			getLogger().info(
					"Reading Player backLog from file \"playerNews.log\"");
			// Reading messages that Players didn't get to see before onDisable
			// was called
			// e.g. server shut down before they logged in.
			br = new BufferedReader(new FileReader("playerNews.log"));
			HashMap<String, ArrayList<String>> inputInfo = new HashMap<String, ArrayList<String>>();
			String line = null;
			String lName = null;
			Player tempPlayer = null;
			ArrayList<String> existing = null;

			do {
				line = br.readLine();
				// //////////
				// /////////
				// do line parsing stuff here
				// ///////
				// //////
				if (line == null) {// first time running script
					break;
				}
				if (line.contains("Name;")) {
					lName = line.substring(line.indexOf(" ") + 1);
				}
				// //////////
				// /////////
				// done* line parsing stuff
				// ///////
				// //////
				tempPlayer = Bukkit.getPlayer(lName);
				if (tempPlayer != null && tempPlayer.isOnline()
						&& line.contains("Log;")) {
					// the player is online, thus there is no need to store them
					// in anything
					tempPlayer
							.sendMessage(line.substring(line.indexOf(" ") + 1));
					// turns "log; How are you?" into "How are you?"
				} else if ((existing = inputInfo.get(lName)) == null) {
					// the key, value pair doesn't exist
					inputInfo.put(lName, new ArrayList<String>());
					inputInfo.get(lName).add(line);
				} else if (line.contains("Log;")) {
					// Key, value pair does exist
					existing = inputInfo.get(lName);
					existing.add(line.substring(line.indexOf(" ") + 1));
					inputInfo.put(lName, existing);
				}
			} while (line != null);
			br.close();

		} catch (IOException e) {// The file doesn't exist or messing has
									// occurred
			mkFile();
		} catch (NullPointerException n) {
			mkFile();
		}
	}

	public void onDisable() {
		if (playerLog == null) {
			getLogger().info("Nothing to save, file writing unnecessary.");
			return;
		}
		getLogger().info("Saving Player backLog to file \"playerNews.log\"");
		try {
			File file = new File("playerNews.log");
			file.setWritable(true);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write("");// Clear contents of file, the entirety of the file
								// should be in-game
			for (Map.Entry<String, ArrayList<String>> entry : playerLog
					.entrySet()) {
				output.append("Name; " + entry.getKey() + "\n");
				for (String log : entry.getValue()) {
					output.append("Log; " + log + "\n");
				}
			}
			output.close();
			// Pity the plight of young coders
			// closing statements they do need
			// With complex emotional file reading attachments
			// manually using a seek....
		} catch (IOException e) {

		}
		getLogger().info("Saved Player backLog to file \"playerNews.log\"");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (cmd.getName().equalsIgnoreCase("mkTrade")) {

			if (args.length < 1) {
				// Silly goat didnt add any players
				String numPlayers = (sender instanceof Player) ? "2 people"
						: "1 person";
				sender.sendMessage("No players supplied, I need at least "
						+ numPlayers + " man!");
				sender.sendMessage("You need to specify who the trades are for.");
				sender.sendMessage("e.g. /mktrade ben sarah thomas ... Notch  HeroBrine");
				return false;
			}

			if ((sender.isOp() || !(sender instanceof Player))) {
				// Admin or Console cases
				if (args[0].equalsIgnoreCase("Intervention")) {
					// modifying intervention
					if (args.length < 2 || args.length > 2) {
						String onOff = Intervention ? "on" : "off";
						sender.sendMessage("Intervention;" + onOff);
						return true;
					} else if ((args[1].equals("0") || args[1].equals("1"))) {
						Intervention = args[1].equals("1") ? true : false;
						return true;
					} else {
						sender.sendMessage("Improper use of intervention");
						sender.sendMessage("Use 1 for true(admin needed)");
						sender.sendMessage("or 0 for false(admin not needed)");
						return false;
					}
				}

			}
			String[] argsa = null;
			if (!(sender instanceof Player)) {
				// Console specific cases
				if (args.length < 2) {
					sender.sendMessage("Number of specified players is too low");
					sender.sendMessage("You need to specify a trade post placer player");
					sender.sendMessage("and who the trades are for.");
					sender.sendMessage("e.g. mktrade ben(post placer) sarah(trade recipient) ...");
					return false;
				}

				sender = getServer().getPlayer(args[0]);
				args = (String[]) ArrayUtils.removeElement(args, args[0]);
				argsa = Arrays.copyOf(args, args.length-1);
			}
			
			if(argsa == null) argsa = args; 
				
				
			if (!mkTrade(sender, argsa)) {// run mktrade method, if all goes
										// well, hurray, otherwise...
				// Insanity...
				// this..is...UNNACCEPTABLE!!!!
				sender.sendMessage("Sorry, something went wrong in mkTrade");
				return false;
			}
			return true;

		}

		return false;
	}

	public void setMetadata(Player player, String key, Object value,
			Plugin plugin) {
		player.setMetadata(key, new FixedMetadataValue(plugin, value));
	}

	public Object getMetadata(Player player, String key, Plugin plugin) {
		List<MetadataValue> values = player.getMetadata(key);
		for (MetadataValue value : values) {
			if (value.getOwningPlugin().getDescription().getName()
					.equals(plugin.getDescription().getName())) {
				return value.value();
			}
		}
		return null;
	}

	private boolean security() {
		// TODO Auto-generated method stub
		return false;
	}

	private void mkFile() {
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
			getLogger()
					.info("End of world, the TradeMania corp. is potentially not responsible");
		}
	}

	private boolean mkTrade(CommandSender sender, String[] args) {
		try {
			if (tradingSigns == null) {
				tradingSigns = new HashMap<String, ArrayList<SignData>>();
			}

			ItemStack tradePost = new ItemStack(Material.SIGN_POST, 1);
			ItemMeta data = tradePost.getItemMeta();
			String temp = "";

			for (int i = 0; i < args.length; i++)
				temp += args[i] + ", ";

			String signName = "TradePost "
					+ ((args.length > 2) ? "multiple recipients" : temp
							.substring(0, temp.length() - 2));

			data.setDisplayName(signName);
			tradePost.setItemMeta(data);

			Player player = Bukkit.getPlayer(sender.getName());
			player.getInventory().addItem(tradePost);

			SignData sData = new SignData(data.getDisplayName(),
					sender.getName(), 0, 0, 0, false);

			if (!tradingSigns.containsKey(sender.getName())) {
				ArrayList<SignData> signs = new ArrayList<SignData>();
				signs.add(sData);
				tradingSigns.put(sender.getName(), signs);
			} else {
				tradingSigns.get(sender.getName()).add(sData);
			}

			return true;
		} catch (Exception e) {
			// Something nope'd
			sender.sendMessage("Sorry, I had a problem in \"mkTrade method\".");
			return false;
		}
	}
}

class SignData {
	private String m_signName;
	private String m_signOwner;
	private int m_x;
	private int m_y;
	private int m_z;
	private boolean placed;

	protected SignData(String m_signName, String m_signOwner, int m_x, int m_y,
			int m_z, boolean placed) {
		this.m_signName = m_signName;
		this.m_signOwner = m_signOwner;
		this.m_x = m_x;
		this.m_y = m_y;
		this.m_z = m_z;
		this.placed = placed;
	}

	public String getSignName() {
		return m_signName;
	}

	public void setSignName(String m_signName) {
		this.m_signName = m_signName;
	}

	public String getSignOwner() {
		return m_signOwner;
	}

	public void setSignOwner(String m_signOwner) {
		this.m_signOwner = m_signOwner;
	}

	public int getX() {
		return m_x;
	}

	public void setX(int m_x) {
		this.m_x = m_x;
	}

	public int getY() {
		return m_y;
	}

	public void setY(int m_y) {
		this.m_y = m_y;
	}

	public int getZ() {
		return m_z;
	}

	public void setZ(int m_z) {
		this.m_z = m_z;
	}

	public boolean isPlaced() {
		return placed;
	}

	public void setPlaced(boolean placed) {
		this.placed = placed;
	}

}
