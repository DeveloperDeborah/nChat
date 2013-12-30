package no.runsafe.nchat.chat;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.nchat.antispam.SpamHandler;
import no.runsafe.nchat.chat.formatting.ChatFormatter;

import java.util.Collections;
import java.util.List;

public class PlayerChatEngine
{
	public PlayerChatEngine(ChatFormatter chatFormatter, MuteHandler muteHandler, SpamHandler spamHandler, IgnoreHandler ignoreHandler, IConsole console, IServer server)
	{
		this.chatFormatter = chatFormatter;
		this.muteHandler = muteHandler;
		this.spamHandler = spamHandler;
		this.ignoreHandler = ignoreHandler;
		this.console = console;
		this.server = server;
	}

	/**
	 * Used to send a broadcast from a player, will be subject to mute and spam checks.
	 * Returns true if the message was broadcast successfully, otherwise false.
	 *
	 * @param player  The player to broadcast the message.
	 * @param message The message the player will broadcast.
	 */
	public void playerBroadcast(IPlayer player, String message)
	{
		if (isMuted(player))
			return;

		String filteredMessage = spamHandler.getFilteredMessage(player, message);

		if (filteredMessage != null)
			broadcastMessageAsPlayer(player, filteredMessage);
	}

	/**
	 * Used to broadcast a system message from a player, will be subject to mute and spam checks.
	 * Will not be seen by people ignoring the player.
	 *
	 * @param player  The player to broadcast the message.
	 * @param message The message the player will broadcast.
	 */
	public void playerSystemBroadcast(ICommandExecutor player, String message)
	{
		if (isMuted(player))
			return;

		broadcastMessage(message, ignoreHandler.getPlayersIgnoring(player), true);
	}

	/**
	 * Broadcast a message from a player, not subject to any spam/mute checks.
	 * Will not be seen by people ignoring the player.
	 *
	 * @param player  The player to broadcast the message.
	 * @param message The message the player will broadcast.
	 */
	public void broadcastMessageAsPlayer(IPlayer player, String message)
	{
		InternalChatEvent event = new InternalChatEvent(player, message);
		event.Fire();
		broadcastMessage(chatFormatter.formatChatMessage(player, event.getMessage()), ignoreHandler.getPlayersIgnoring(player), true);
	}

	/**
	 * Broadcast a message to the server.
	 *
	 * @param message The message to broadcast.
	 */
	public void broadcastMessage(String message)
	{
		broadcastMessage(message, null, false);
	}

	/**
	 * Broadcast a message to the server.
	 *
	 * @param message         The message to be broadcast.
	 * @param excludedPlayers A list of players who will not see this message.
	 * @param highlightName Should the player who receives the broadcast have their name highlighted?
	 */
	public void broadcastMessage(String message, List<String> excludedPlayers, boolean highlightName)
	{
		message = message.replace("%", "%%");

		excludedPlayers = excludedPlayers == null ? Collections.<String>emptyList() : excludedPlayers;
		List<IPlayer> worldPlayers = server.getOnlinePlayers();

		for (IPlayer worldPlayer : worldPlayers)
		{
			if (!excludedPlayers.contains(worldPlayer.getName()))
			{
				String playerName = worldPlayer.getName();
				String sendMessage = highlightName ? message.replace(playerName, "&a" + playerName + "&r") : message;
				worldPlayer.sendColouredMessage(sendMessage);
			}
		}

		console.logInformation(message);
	}

	/**
	 * A mute check.
	 *
	 * @param player The player to check.
	 * @return False if the player is muted.
	 */
	private boolean isMuted(ICommandExecutor player)
	{
		// Mute check.
		if (muteHandler.isPlayerMuted(player))
		{
			player.sendColouredMessage("&cYou cannot broadcast messages right now.");
			return true;
		}
		return false;
	}

	private final ChatFormatter chatFormatter;
	private final MuteHandler muteHandler;
	private final SpamHandler spamHandler;
	private final IgnoreHandler ignoreHandler;
	private final IConsole console;
	private final IServer server;
}
