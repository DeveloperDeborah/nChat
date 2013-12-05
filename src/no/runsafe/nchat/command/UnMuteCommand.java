package no.runsafe.nchat.command;

import no.runsafe.framework.api.IConsole;
import no.runsafe.framework.api.command.argument.PlayerArgument;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.nchat.chat.MuteHandler;

import java.util.Map;

public class UnMuteCommand extends PlayerCommand
{
	public UnMuteCommand(IConsole console, MuteHandler muteHandler)
	{
		super(
			"unmute", "Unmutes a previously muted player", "runsafe.nchat.mute",
			new PlayerArgument()
		);
		this.console = console;
		this.muteHandler = muteHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer player, Map<String, String> args)
	{
		String unMutePlayerName = args.get("player");

		if (unMutePlayerName.equalsIgnoreCase("server"))
		{
			if (player.hasPermission("nChat.commands.muteServer"))
			{
				this.muteHandler.unMuteServer();
				return "&bGlobal chat has been un-muted! Praise the sun.";
			}
			else
			{
				return "&cYou do not have permission to do that.";
			}
		}
		if (!player.hasPermission("nChat.commands.mutePlayer"))
			return "&cYou do not have permission to do that.";

		RunsafePlayer unMutePlayer = RunsafeServer.Instance.getPlayer(unMutePlayerName);

		if (unMutePlayer == null)
			return "&cTry to pick a player who exists.";

		if (unMutePlayer.hasPermission("nChat.muteExempt"))
			return "&cThat player is exempt from being un-muted, silly as it sounds.";

		console.logInformation(String.format("%s un-muted %s", player.getName(), unMutePlayer.getName()));
		this.muteHandler.unMutePlayer(unMutePlayer);
		return String.format("&bUnmuted %s.", unMutePlayer.getPrettyName());
	}

	private final IConsole console;
	private final MuteHandler muteHandler;
}
