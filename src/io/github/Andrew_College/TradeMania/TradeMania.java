package io.github.Andrew_College.TradeMania;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class TradeMania extends JavaPlugin implements CommandExecutor{

	public void onEnable() {
		getLogger().info("onEnable has been invoked!");
	}

	public void onDisable() {
		getLogger().info("onDisable has been invoked!");
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

}
