package no.runsafe.nchat.handlers;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.HashMap;
import java.util.logging.Level;

public class WhisperHandler implements IConfigurationChanged
{
	public WhisperHandler(IOutput console, ChatHandler chatHandler)
	{
		this.console = console;
		this.lastWhisperList = new HashMap<String, String>();
		this.chatHandler = chatHandler;
	}

	public void sendWhisper(RunsafePlayer fromPlayer, RunsafePlayer toPlayer, String message)
	{
		fromPlayer.sendColouredMessage(
			this.whisperToFormat.replace("#target", toPlayer.getPrettyName()).replace("#message", message)
		);
		toPlayer.sendColouredMessage(
			this.whisperFromFormat.replace("#source", fromPlayer.getPrettyName()).replace("#message", message)
		);

		this.setLastWhisperedBy(toPlayer, fromPlayer);

		console.writeColoured(
			"%s -> %s: %s",
			Level.INFO,
			fromPlayer.getPrettyName(),
			toPlayer.getPrettyName(),
			message
		);
	}

	private void setLastWhisperedBy(RunsafePlayer player, RunsafePlayer whisperer)
	{
		this.lastWhisperList.put(player.getName(), whisperer.getName());
	}

	public void deleteLastWhisperedBy(RunsafePlayer player)
	{
		if (this.lastWhisperList.containsKey(player.getName()))
			this.lastWhisperList.remove(player.getName());
	}

	public RunsafePlayer getLastWhisperedBy(RunsafePlayer player)
	{
		String playerName = player.getName();

		if (this.lastWhisperList.containsKey(playerName))
		{
			RunsafePlayer whisperer = RunsafeServer.Instance.getPlayer(this.lastWhisperList.get(playerName));
			if (whisperer != null)
				return whisperer;
		}
		return null;
	}

	public boolean canWhisper(RunsafePlayer player, RunsafePlayer target)
	{
		return (target.isOnline() && player.canSee(target));
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		this.whisperToFormat = configuration.getConfigValueAsString("chatMessage.whisperTo");
		this.whisperFromFormat = configuration.getConfigValueAsString("chatMessage.whisperFrom");
	}

	private String whisperToFormat;
	private String whisperFromFormat;
	private IOutput console;
	private HashMap<String, String> lastWhisperList;
	private ChatHandler chatHandler;
}
