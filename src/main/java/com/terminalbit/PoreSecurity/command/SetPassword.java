package com.terminalbit.PoreSecurity.command;

import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

public class SetPassword implements CommandExecutor{

	public CommandResult execute(CommandSource cS, CommandContext args) throws CommandException {
		cS.sendMessage(Texts.of("setPassword!"));
		return CommandResult.empty();
	}

}
