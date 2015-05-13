package com.terminalbit.PoreSecurity.util;

import java.io.IOException;

import org.spongepowered.api.entity.player.Player;

import com.terminalbit.PoreSecurity.PoreSecurity;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class AccountManager {
	private static ConfigurationLoader<CommentedConfigurationNode> userData = PoreSecurity.access.userData;
	private static ConfigurationNode config;
	public static boolean userRegistered(Player player) {
		try{
			config = userData.load();
			return !config.getNode("users",player.getIdentifier()).isVirtual();
		}catch(IOException e){
			return false;
		}
	}
	public static boolean checkLogin(Player player){
		// TODO: Have some sort of check. :P
		return false;
	}
	public static boolean checkPassword(Player player, String pass){
		try{
			config = userData.load();
			if(!config.getNode("users",player.getIdentifier()).isVirtual()){
				if(config.getNode("users",player.getIdentifier(),"password").getString().equals(pass)){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}catch(IOException e){
			return false;
		}
	}
	public static void setPassword(Player player, String pass){
		try{
			config = userData.load();
			config.getNode("users",player.getIdentifier(),"password").setValue(pass);
			userData.save(config);
		}catch(IOException e){
		}
	}
}
