package com.terminalbit.PoreSecurity.command;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.terminalbit.PoreSecurity.PoreSecurity;
import com.terminalbit.PoreSecurity.util.AccountManager;

public class Login implements CommandExecutor{

	public CommandResult execute(CommandSource cS, CommandContext args) throws CommandException {
		Player caller = PoreSecurity.access.game.getServer().getPlayer(cS.getName()).get();
		if(AccountManager.checkLogin(caller)){ // if the user is logged in
			cS.sendMessage(Texts.of("Oops! You're already logged in!"));
			return CommandResult.empty();
		}
		try{
			String password = (String) args.getOne("pass").get();
			if(AccountManager.userRegistered(caller)){
				if(AccountManager.checkPassword(caller, password)){
					cS.sendMessage(Texts.of("You successfully logged in."));
					return CommandResult.success();
				}else{
					cS.sendMessage(Texts.of("That is not the correct password for " + cS.getName()));
					return CommandResult.empty();
				}
			}else{
				cS.sendMessage(Texts.of("You are not registered on this server! Register with /poresecurity setpassword <password>"));
				return CommandResult.empty();
			}
		}catch(ClassCastException e){
			cS.sendMessage(Texts.of("Failed to log you in! D:"));
			return CommandResult.empty();
		}
	}

}
