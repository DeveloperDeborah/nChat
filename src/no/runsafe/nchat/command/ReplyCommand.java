package no.runsafe.nchat.command;

import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.nchat.Constants;
import no.runsafe.nchat.handlers.MuteHandler;
import no.runsafe.nchat.handlers.WhisperHandler;
import org.apache.commons.lang.StringUtils;

public class ReplyCommand extends RunsafeCommand
{
	public ReplyCommand(WhisperHandler whisperHandler, MuteHandler muteHandler)
	{
		super("reply", "message");
		this.whisperHandler = whisperHandler;
		this.muteHandler = muteHandler;
		setConsoleLogging(false);
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.nchat.whisper";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		RunsafePlayer whisperer = this.whisperHandler.getLastWhisperedBy(executor);

		if (whisperer != null)
		{
			if (this.whisperHandler.canWhisper(executor, whisperer))
			{
				if (!this.muteHandler.isPlayerMuted(executor))
				{
					String message = StringUtils.join(args, " ", 0, args.length);
					this.whisperHandler.sendWhisper(executor, whisperer, message);
				}
				else
				{
					executor.sendMessage(Constants.CHAT_MUTED);
				}
			}
			else
				executor.sendMessage(String.format(Constants.WHISPER_TARGET_OFFLINE, whisperer.getName()));
		}
		else
		{
			executor.sendMessage(Constants.WHISPER_NO_REPLY_TARGET);
		}

		return null;
	}

	private WhisperHandler whisperHandler;
	private MuteHandler muteHandler;
}
