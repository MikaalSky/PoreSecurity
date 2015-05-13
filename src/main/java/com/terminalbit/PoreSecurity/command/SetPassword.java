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

public class SetPassword implements CommandExecutor{

	public CommandResult execute(CommandSource cS, CommandContext args) throws CommandException {
		Player caller = PoreSecurity.access.game.getServer().getPlayer(cS.getName()).get();
		String pass = (String) args.getOne("pass").get();
		AccountManager.setPassword(caller, pass);
		cS.sendMessage(Texts.of("Your password has been set!"));
		return CommandResult.success();
	}

}
