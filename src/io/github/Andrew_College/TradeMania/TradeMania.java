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
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class TradeMania extends JavaPlugin implements CommandExecutor {
	private HashMap<String, ArrayList<String>> playerLog;
	private boolean Intervention = true;
	public void onEnable() {
		BufferedReader br = null;
		try {
			getLogger().info("Reading Player backLog from file \"playerNews.log\"");
			//Reading messages that Players didn't get to see before onDisable was called
			//e.g. server shut down before they logged in.
			br = new BufferedReader(new FileReader("playerNews.log"));
			HashMap<String, ArrayList<String>> inputInfo = new HashMap<String, ArrayList<String>>();
			String line = null;
			String lName = null;
			Player tempPlayer = null;
			ArrayList<String> existing = null;
			
			do {
				line = br.readLine();
				////////////
				///////////
				//do line parsing stuff here
				/////////
				////////
				if(line == null){//first time running script
					break;
				}
				if(line.contains("Name;")){ 
					lName = line.substring(line.indexOf(" ")+1);
				}
				////////////
				///////////
				//done* line parsing stuff
				/////////
				////////
				tempPlayer = Bukkit.getPlayer(lName);
				if(tempPlayer != null && tempPlayer.isOnline() && line.contains("Log;")){
					//the player is online, thus there is no need to store them in anything
					tempPlayer.sendMessage(line.substring(line.indexOf(" ")+1));
					//turns "log; How are you?" into "How are you?"
				}
				else if((existing = inputInfo.get(lName)) == null){
					//the key, value pair doesn't exist
					inputInfo.put(lName, new ArrayList<String>());
					inputInfo.get(lName).add(line);
				}else if(line.contains("Log;")){
					//Key, value pair does exist
					existing = inputInfo.get(lName);
					existing.add(line.substring(line.indexOf(" ")+1));
					inputInfo.put(lName, existing);
				}
			} while (line != null);
			br.close();

		} catch (IOException e ) {//The file doesn't exist or messing has occurred
			mkFile();
		}
		catch(NullPointerException n){
			mkFile();
		}
	}

	public void onDisable() {
		if(playerLog == null){
			getLogger().info("Nothing to save, file writing unnecessary.");
			return;
		}
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
			//Pity the plight of young coders
			//closing statements they do need
			//With complex emotional file reading attachments
			//manually using a seek....
		} catch (IOException e) {

		}
		getLogger().info("Saved Player backLog to file \"playerNews.log\"");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (cmd.getName().equalsIgnoreCase("mkTrade")) {
			if(args.length == 0 || ((sender.isOp()||!(sender instanceof Player)) && args.length < 2)){
				sender.sendMessage("To send your stuff to anyone, use \"/market" + 
				((sender.isOp()||!(sender instanceof Player))?" [player]\" ":"\" ") + "instead");
				
				sender.sendMessage("Otherwise, use;");
				return false;
			}
			if((sender.isOp()||!(sender instanceof Player)) 
				&&	args[0].equalsIgnoreCase("Intervention") 
				&& (args[1] == "0" || args[1] == "1")){
				Intervention = (args[1] == "1")?true:false;
			}
			if ((sender.isOp()||!(sender instanceof Player))//not a player
				&& args[0].equalsIgnoreCase("Intervention")  
				&& !(args[1] == "0" || args[1] == "1")) {
				//Intervention used in an illegal manner (number other than 0/1 used)
				sender.sendMessage("If you wanted to disable the anti-spam admin requirement");
				sender.sendMessage("use \"/mkTrade Intervention 1(enable<Admin needed>)/0(disable<Admin not needed>)\"");
				return false;
			}
			if(Bukkit.getPlayer(args[0]) == null || !Bukkit.getPlayer(args[0]).isOnline()){
				sender.sendMessage(args[0] + " doesn't exist or is offline");
				return false;
			}
			if (!mkTrade(sender, args)) {
				//Insanity...
				//this..is...UNNACCEPTABLE!!!!
				sender.sendMessage("Sorry, something went wrong in mkTrade");
				return false;
			}
			if(!Intervention || sender.isOp() || security())return true;
			
		}

		return false;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	 public void onPlayerInteract(PlayerInteractEvent event) {
	      if (!event.hasBlock()) {
	    	  
	            return;
	 
	        }
	 
	        Block block = event.getClickedBlock();
	        Player player = event.getPlayer();
	        
	        String sign = ((Sign) block.getState()).toItemStack().getItemMeta().getDisplayName();
	        if(sign.contains("TradePost")){
	        	player.sendMessage("you just popped down a sign for some people!");
	        }
	}
	
	private boolean security() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void mkFile(){
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

	private boolean mkTrade(CommandSender sender, String[] args) {
		try {
			ItemStack tradePost = new ItemStack(Material.SIGN_POST, 1);
			ItemMeta data = tradePost.getItemMeta();
			String signName = "TradePost";
			
			data.setDisplayName(signName);
			tradePost.setItemMeta(data);
			Player player = Bukkit.getPlayer(sender.getName());
			player.getInventory().addItem(tradePost);
			return true;
		} catch (Exception e) {
			//Something nope'd
			return false;
		}
	}
}
