package io.github.Andrew_College.TradeMania;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClickSign implements Listener {

	@EventHandler
	public void onPlayerClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		if (TradeMania.tradingSigns == null 
			&& player.getItemInHand().getItemMeta().getDisplayName().contains("TradePost")){
			player.sendMessage("No records exist, try running mktrade again");
			return;//nothing in trading signs, stops unnecessary excess crap happening
		}
		
		if (TradeMania.tradingSigns.containsKey(player.getName()) 
			&& player.getItemInHand().getItemMeta().getDisplayName().contains("TradePost")) {
			//Are they in the db and do they have the sign in hand
			player.sendMessage("you placed a sign!");
			return;
		}
		player.sendMessage("");
		
	}
}
