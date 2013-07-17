package io.github.Andrew_College.TradeMania;

import org.bukkit.plugin.java.JavaPlugin;

public final class TradeMania extends JavaPlugin {
 
	public void onEnable(){
		getLogger().info("onEnable has been invoked!");
	}
 
	public void onDisable(){
		getLogger().info("onDisable has been invoked!");
	}
}
