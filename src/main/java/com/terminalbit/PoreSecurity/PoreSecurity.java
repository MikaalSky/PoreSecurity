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
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.util.command.spec.CommandSpec;
import static org.spongepowered.api.util.command.args.GenericArguments.*;
import com.google.inject.Inject;
import com.terminalbit.PoreSecurity.command.Login;
import com.terminalbit.PoreSecurity.command.MainCommand;
import com.terminalbit.PoreSecurity.command.SetPassword;

@Plugin(id="PoreSecurity", name="Pore Security", version="0.1")
public class PoreSecurity {
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
				.build());
		subCommands.put(Arrays.asList("setpassword","setpass","sp"), CommandSpec.builder()
				.setExecutor(new SetPassword())
				.build());
		CommandSpec mainCommand = CommandSpec.builder()
				.setChildren(subCommands)
				.setExecutor(new MainCommand())
				.setArguments(none())
				.build();
		cD.register(this, mainCommand, "ps", "poresecurity","poresec");
	}
	
	@Subscribe
	public void onServerStart(ServerStartedEvent event) {
		logger.info("Pore Security v" + plugin.version() + " has started.");
	}
}
