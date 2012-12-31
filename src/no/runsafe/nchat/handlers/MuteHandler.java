package no.runsafe.nchat.handlers;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.nchat.database.MuteDatabase;

import java.util.List;

public class MuteHandler implements IConfigurationChanged
{
	public MuteHandler(MuteDatabase muteDatabase)
	{
		this.muteDatabase = muteDatabase;
		this.loadMuteList();
	}

	public void loadMuteList()
	{
		this.mutedPlayers = this.muteDatabase.getMuteList();
	}

	public boolean isPlayerMuted(RunsafePlayer player)
	{
		return (this.isPlayerMuted(player.getName()) && !player.hasPermission("runsafe.nchat.mute.exempt"));
	}

	private boolean isPlayerMuted(String playerName)
	{
		return (this.serverMute || this.mutedPlayers.contains(playerName));
	}

	public void mutePlayer(RunsafePlayer player)
	{
		this.mutePlayer(player.getName());
	}

	public void mutePlayer(String playerName)
	{
		this.mutedPlayers.add(playerName);
		this.muteDatabase.mutePlayer(playerName);
	}

	public void unMutePlayer(RunsafePlayer player)
	{
		this.unMutePlayer(player.getName());
	}

	public void unMutePlayer(String playerName)
	{
		this.mutedPlayers.remove(playerName);
		this.muteDatabase.unMutePlayer(playerName);
	}

	public void muteServer()
	{
		this.serverMute = true;
	}

	public void unMuteServer()
	{
		this.serverMute = false;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration iConfiguration)
	{
		this.serverMute = iConfiguration.getConfigValueAsBoolean("spamControl.muteChat");
	}

	private List<String> mutedPlayers;
	private MuteDatabase muteDatabase;
	private boolean serverMute;
}
