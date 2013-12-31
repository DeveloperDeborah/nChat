package no.runsafe.nchat.database;

import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.IRow;
import no.runsafe.framework.api.database.ISet;
import no.runsafe.framework.api.database.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IgnoreDatabase extends Repository
{
	public IgnoreDatabase(IDatabase database)
	{
		this.database = database;
	}

	@Override
	public String getTableName()
	{
		return "nchat_ignore";
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> queries = new HashMap<Integer, List<String>>(1);
		List<String> sql = new ArrayList<String>(1);
		sql.add(
			"CREATE TABLE `nchat_ignore` (" +
				"`player` VARCHAR(16) NULL," +
				"`ignore` VARCHAR(16) NULL," +
				"PRIMARY KEY (`player`, `ignore`)" +
				')'
		);
		queries.put(1, sql);
		return queries;
	}

	public HashMap<String, List<String>> getIgnoreList()
	{
		HashMap<String, List<String>> ignoreList = new HashMap<String, List<String>>(1);
		ISet result = database.query("SELECT `player`, `ignore` FROM nchat_ignore");
		for (IRow row : result)
		{
			String ignoredPlayer = row.String("ignore");
			if (!ignoreList.containsKey(ignoredPlayer))
				ignoreList.put(ignoredPlayer, new ArrayList<String>(1));

			ignoreList.get(ignoredPlayer).add(row.String("player"));
		}

		return ignoreList;
	}

	public void ignorePlayer(ICommandExecutor player, ICommandExecutor ignore)
	{
		database.update("INSERT IGNORE INTO nchat_ignore (`player`, `ignore`) VALUES(?, ?)", player.getName(), ignore.getName());
	}

	public void removeIgnorePlayer(ICommandExecutor player, ICommandExecutor ignore)
	{
		database.update("DELETE FROM nchat_ignore WHERE `player` = ? AND `ignore` = ?", player.getName(), ignore.getName());
	}

	private final IDatabase database;
}
