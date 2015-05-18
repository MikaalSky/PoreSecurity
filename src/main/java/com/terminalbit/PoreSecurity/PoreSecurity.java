package com.terminalbit.PoreSecurity;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerMoveEvent;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.google.inject.Inject;
import com.terminalbit.PoreSecurity.command.Login;
import com.terminalbit.PoreSecurity.command.MainCommand;
import com.terminalbit.PoreSecurity.command.SetPassword;

@Plugin(id="PoreSecurity", name="PoreSecurity", version="0.1")
public class PoreSecurity {
	//Le access :D
	public static PoreSecurity access;
	
	//Get the config root.
	@Inject
	@ConfigDir(sharedRoot = false)
	private File configDir;
	//set up the configloaders
	public ConfigurationLoader<CommentedConfigurationNode> mainConfig = null;
	public ConfigurationLoader<CommentedConfigurationNode> userData = null;
	public ConfigurationNode config = null;
	//ye olde config loader creator
	public ConfigurationLoader<CommentedConfigurationNode> getLoader(String configName) throws IOException {
	    File configFile = new File(this.configDir, configName);
	    if(!configFile.exists()){
	    	configFile.getParentFile().mkdirs();
	    	configFile.createNewFile();
	    }
	    return HoconConfigurationLoader.builder().setFile(configFile).build();
	}
	
	//Important Variable Initialization
	@Inject
	public Game game;
	
	@Inject
	public Logger logger;
	
	Plugin plugin = PoreSecurity.class.getAnnotation(Plugin.class);
	
	@Subscribe
	private void PreInitalization(PreInitializationEvent event){
		access = this;
		logger.info("Pore Security is starting...");
		try{
			//load teh configs!!!
			mainConfig = getLoader("config.conf");
			userData = getLoader("userdata.conf");
		}catch(IOException e){
			logger.warn("Failed to initalize configs!");
		}
		try{
			config = mainConfig.load();
			config.getNode("version").setValue(plugin.version());
			if(config.getNode("OPforcePassword").isVirtual()){
				config.getNode("OPforcePassword").setValue(true);
			}
			mainConfig.save(config);
		}catch(IOException e){
			logger.warn("Failed to load config.conf!");
		}
		try{
			config = userData.load();
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = new Date();
			config.getNode("lastUpdated").setValue(dateFormat.format(date));
			userData.save(config);
		}catch(IOException e){
			logger.warn("Failed to load userData.conf!");
		}
	}
	HashMap<List<String>, CommandSpec> subCommands = new HashMap<>();
	@Subscribe
	public void Initalization(InitializationEvent event) {
		logger.info("Registering Pore Security's commands...");
		CommandService cD = game.getCommandDispatcher();
		subCommands.put(Arrays.asList("login","l"), CommandSpec.builder()
				.setExecutor(new Login())
				.setArguments(GenericArguments.string(Texts.of("pass")))
				.build());
		subCommands.put(Arrays.asList("setpassword","setpass","sp"), CommandSpec.builder()
				.setExecutor(new SetPassword())
				.setArguments(GenericArguments.string(Texts.of("pass")))
				.build());
		CommandSpec mainCommand = CommandSpec.builder()
				.setExecutor(new MainCommand())
				.setDescription(Texts.of("The main PoreSecurity command."))
				.setChildren(subCommands)
				.build();
		cD.register(this, mainCommand, "ps", "poresecurity","poresec");
	}
	
	@Subscribe
	public void onServerStart(ServerStartedEvent event) {
		logger.info("Pore Security v" + plugin.version() + " has started.");
	}
	
	//Login-enforce not allowing people to log in
	@Subscribe
	public void onPlayerMove(PlayerMoveEvent event){
		if(game.getServer().getOnlineMode()){
			try {
				config = userData.load();
				if(!config.getNode("users",game.getServer().getPlayer(event.getUser().getName()).get().getIdentifier()).isVirtual()){
					event.setCancelled(true);
					event.getUser().sendMessage(Texts.of("Please log in!"));
				}
			} catch (IOException e) {}
		}else{
			try {
				config = userData.load();
				if(!config.getNode("users_offlinemode",event.getUser().getName()).isVirtual()){
					event.setCancelled(true);
					game.getServer().getPlayer(event.getUser().getName()).get().sendMessage(Texts.of("Please log in!"));
				}
			} catch (IOException e) {}
		}
		logger.info("moved");
	}
}
