package icbm.gangshao.terminal.command;

import icbm.gangshao.platform.TileEntityTurretPlatform;
import icbm.gangshao.turret.sentries.TileEntityAutoTurret;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import dark.library.access.AccessLevel;
import dark.library.access.interfaces.ISpecialAccess;
import dark.library.access.interfaces.ITerminal;
import dark.library.terminal.commands.TerminalCommand;

public class CommandTarget extends TerminalCommand
{
	@Override
	public String getCommandPrefix()
	{
		return "target";
	}

	@Override
	public boolean processCommand(EntityPlayer player, ITerminal terminal, String[] args)
	{
		if (terminal instanceof TileEntityTurretPlatform)
		{
			TileEntityTurretPlatform turret = (TileEntityTurretPlatform) terminal;
			if (turret.getTurret() instanceof TileEntityAutoTurret)
			{
				TileEntityAutoTurret sentry = ((TileEntityAutoTurret) turret.getTurret());
				if (args.length > 1)
				{
					String obj = args[1];
					String bool = "";
					boolean change = false;
					if (args.length > 2)
					{
						bool = args[2];
						change = Boolean.getBoolean(bool);
					}
					if (obj.equalsIgnoreCase("players"))
					{
						if (!bool.isEmpty())
						{
							sentry.targetPlayers = change;
						}
						else
						{
							sentry.targetPlayers = !sentry.targetPlayers;
						}
						return true;
					}
					else if (obj.equalsIgnoreCase("mobs"))
					{
						if (!bool.isEmpty())
						{
							sentry.targetLiving = change;
						}
						else
						{
							sentry.targetLiving = !sentry.targetLiving;
						}
						return true;
					}
					else if (obj.equalsIgnoreCase("missiles"))
					{
						if (!bool.isEmpty())
						{
							sentry.targetMissiles = change;
						}
						else
						{
							sentry.targetMissiles = !sentry.targetMissiles;
						}
						return true;
					}
					else if (obj.equalsIgnoreCase("crafts"))
					{
						if (!bool.isEmpty())
						{
							sentry.targetCrafts = change;
						}
						else
						{
							sentry.targetCrafts = !sentry.targetCrafts;
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean canPlayerUse(EntityPlayer var1, ISpecialAccess mm)
	{
		return mm.getUserAccess(var1.username).ordinal() >= AccessLevel.ADMIN.ordinal();
	}

	@Override
	public boolean showOnHelp(EntityPlayer player, ISpecialAccess mm)
	{
		return this.canPlayerUse(player, mm);
	}

	@Override
	public List<String> getCmdUses(EntityPlayer player, ISpecialAccess mm)
	{
		List<String> cmds = new ArrayList<String>();
		cmds.add("target <obj> [bool]");
		return cmds;
	}

	@Override
	public boolean canMachineUse(ISpecialAccess mm)
	{
		if (mm instanceof TileEntityTurretPlatform)
		{
			return ((TileEntityTurretPlatform) mm).getTurret() instanceof TileEntityAutoTurret;
		}
		return false;
	}

}
