package icbm.sentry.terminal.command;

import icbm.sentry.platform.TileEntityTurretPlatform;
import icbm.sentry.terminal.AccessLevel;
import icbm.sentry.terminal.ISpecialAccess;
import icbm.sentry.terminal.ITerminal;
import icbm.sentry.terminal.TerminalCommand;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class CommandUser extends TerminalCommand
{
	@Override
	public String getCommandPrefix()
	{
		return "users";
	}

	@Override
	public boolean processCommand(EntityPlayer player, ITerminal terminal, String[] args)
	{
		if (args[0].equalsIgnoreCase("users") && args.length > 1 && args[1] != null && terminal instanceof TileEntityTurretPlatform)
		{
			TileEntityTurretPlatform turret = (TileEntityTurretPlatform) terminal;

			// ILockable
			if (args[1].equalsIgnoreCase("List"))
			{
				terminal.addToConsole("");
				terminal.addToConsole("Listing Users");
				for (int i = 0; i < turret.getUsers().size(); i++)
				{
					terminal.addToConsole(" " + i + ") " + turret.getUsers().get(i).username);
				}
				return true;
			}
			if (args[1].equalsIgnoreCase("remove") && args.length > 2)
			{
				if (args[2] != null)
				{
					if (turret.removeUser(args[2]))
					{
						terminal.addToConsole("Removed: " + args[2]);
						return true;
					}
					else
					{
						terminal.addToConsole(" User not found.");
						return true;
					}
				}
				else
				{
					terminal.addToConsole("Invalid username.");
					return true;
				}
			}
			if (args[1].equalsIgnoreCase("add") && args.length > 2)
			{
				if (args[2] != null)
				{
					if (turret.setAccess(args[2], AccessLevel.USER, true))
					{
						terminal.addToConsole("Added: " + args[2]);
						return true;
					}
					else
					{
						terminal.addToConsole("User already exists.");
						return true;
					}
				}
				else
				{
					terminal.addToConsole("Invalid username.");
					return true;
				}
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean canPlayerUse(EntityPlayer var1, ISpecialAccess mm)
	{
		return mm.getPlayerAccess(var1).ordinal() >= AccessLevel.OPERATOR.ordinal();
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
		cmds.add("users list");
		cmds.add("users add [player]");
		cmds.add("users remove [player]");
		return cmds;
	}

	@Override
	public boolean canMachineUse(ISpecialAccess mm)
	{
		return mm instanceof TileEntityTurretPlatform;
	}

}
