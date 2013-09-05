package io.github.Andrew_College.TradeMania;

import java.util.ArrayList;

import org.bukkit.Location;
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
		if (TradeMania.tradingSigns != null) {
			// plugin hasn't yet been invoked

			if (action == Action.RIGHT_CLICK_BLOCK) {
				if (player.getItemInHand() == null
						|| player.getItemInHand().getItemMeta() == null) {
					// signs have meta stuff and exist, so the player is either
					// clicking on a sign to get information
					// or just doing other stuff
					for (SignData sData : TradeMania.tradingSigns.get(player
							.getName())) {
						//Check if the player clicking is part of the trade
						if (sData.getSignName().contains(player.getName())) {
							if (sData.isPlaced()) {
								if (sData.getX() == block.getX()
								&& sData.getY() == block.getY()
								&& sData.getZ() == block.getZ()) {
									
									player.sendMessage("SignName; "
											+ sData.getSignName());
									player.sendMessage("Owner; "
											+ sData.getSignOwner());
									player.sendMessage("X Coord; "
											+ sData.getX());
									player.sendMessage("Y Coord; "
											+ sData.getY());
									player.sendMessage("Z Coord; "
											+ sData.getZ());
									
								}
							}
						}
					}
					return;
				} else {
					if (player.getItemInHand().getItemMeta().getDisplayName()
							.contains("TradePost")) {
						// Sign placed, notify all who are involved and update
						// signs coords
						int i = 0;
						for (SignData sData : TradeMania.tradingSigns
								.get(player.getName())) {
							// go through the players arraylist of signs and
							// select appropriate

							if (sData.getSignName().equals(
									player.getItemInHand().getItemMeta()
											.getDisplayName())) {
								if (!sData.isPlaced()) {
									sData.setPlaced(true);
									sData.setX(block.getX());
									sData.setY(block.getY());
									sData.setZ(block.getZ());
									player.sendMessage("you placed a sign!");
									player.sendMessage("SignName; "
											+ sData.getSignName());
									player.sendMessage("Owner; "
											+ sData.getSignOwner());
									player.sendMessage("X Coord; "
											+ sData.getX());
									player.sendMessage("Y Coord; "
											+ sData.getY());
									player.sendMessage("Z Coord; "
											+ sData.getZ());
									player.getInventory().removeItem(
											player.getInventory()
													.getItemInHand());
									// update the ArrayList
									TradeMania.tradingSigns.get(
											player.getName()).set(i, sData);
									return;
								}
							}
							i++;
						}

					}
				}

			}
		}
	}
}
