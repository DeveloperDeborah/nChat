package no.runsafe.nchat.events;

import no.runsafe.framework.event.player.IPlayerDeathEvent;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.event.player.RunsafePlayerDeathEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.nchat.Constants;
import no.runsafe.nchat.Death;
import no.runsafe.nchat.DeathParser;
import no.runsafe.nchat.handlers.ChatHandler;

public class PlayerDeath implements IPlayerDeathEvent
{
	public PlayerDeath(ChatHandler chatHandler, DeathParser deathParser, IOutput console)
	{
		this.chatHandler = chatHandler;
		this.deathParser = deathParser;
		this.console = console;
	}

	@Override
	public void OnPlayerDeathEvent(RunsafePlayerDeathEvent runsafePlayerDeathEvent)
	{
		String originalMessage = runsafePlayerDeathEvent.getDeathMessage();
		Death deathType = this.deathParser.getDeathType(originalMessage);
		if (deathType == Death.UNKNOWN)
		{
			console.writeColoured("Unknown death message detected: \"%s\"", originalMessage);
		}
		String deathName = deathType.name().toLowerCase();

		String deathTag = deathName;
		String entityName = null;
		String killerName = null;

		if (deathType.hasEntityInvolved())
		{
			killerName = this.deathParser.getInvolvedEntityName(originalMessage, deathType);
			entityName = this.deathParser.isEntityName(killerName);
			deathTag = String.format(
				"%s_%s",
				deathName,
				(entityName != null ? entityName : "Player")
			);
		}

		String customDeathMessage = this.deathParser.getCustomDeathMessage(deathTag);

		if (customDeathMessage == null)
		{
			console.writeColoured("No custom death message '%s' defined, using original..", deathTag);
			return;
		}
		RunsafePlayer player = runsafePlayerDeathEvent.getEntity();

		if (deathType.hasEntityInvolved() && entityName == null)
		{
			RunsafePlayer killer = RunsafeServer.Instance.getPlayer(killerName);

			if (killer != null)
				runsafePlayerDeathEvent.setDeathMessage(String.format(customDeathMessage, player.getPrettyName(), killer.getPrettyName()));
		}
		else
		{
			runsafePlayerDeathEvent.setDeathMessage(String.format(customDeathMessage, player.getPrettyName()));
		}


	}

	private final ChatHandler chatHandler;
	private final DeathParser deathParser;
	private final IOutput console;
}
