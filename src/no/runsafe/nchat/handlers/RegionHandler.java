package no.runsafe.nchat.handlers;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.List;
import java.util.Map;

public class RegionHandler implements IConfigurationChanged
{
	public RegionHandler(WorldGuardInterface worldGuard)
	{
		this.worldGuard = worldGuard;
	}

	public String getRegionTag(RunsafePlayer player)
	{
		String worldName = player.getWorld().getName();
		List<String> regions = this.worldGuard.getRegionsAtLocation(player.getLocation());

		if (regions != null)
		{
			for (String region : regions)
			{
				String regionName = worldName + "-" + region;
				if (this.regionTags.containsKey(regionName))
					return this.regionTags.get(regionName);
			}
		}
		return null;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration iConfiguration)
	{
		this.regionTags = iConfiguration.getConfigValuesAsMap("regionPrefixes");
	}

	private Map<String, String> regionTags;
	private WorldGuardInterface worldGuard;
}
