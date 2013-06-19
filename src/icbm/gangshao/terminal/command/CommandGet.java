package icbm.gangshao.terminal.command;

import icbm.gangshao.platform.TileEntityTurretPlatform;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import dark.core.api.ISpecialAccess;
import dark.core.api.ITerminal;
import dark.library.access.AccessLevel;
import dark.library.access.UserAccess;
import dark.library.machine.terminal.TerminalCommand;

public class CommandGet extends TerminalCommand
{
	@Override
	public String getCommandPrefix()
	{
		return "get";
	}

	@Override
	public boolean processCommand(EntityPlayer player, ITerminal TE, String[] args)
	{
		if (args[0].equalsIgnoreCase("get") && args.length > 1 && args[1] != null && TE instanceof TileEntityTurretPlatform)
		{
			TileEntityTurretPlatform turret = (TileEntityTurretPlatform) TE;
			if (args[1].equalsIgnoreCase("owner"))
			{
				List<UserAccess> userList = turret.getUsersWithAcess(AccessLevel.OWNER);

				if (userList.size() > 0)
				{
					for (UserAccess access : userList)
					{
						TE.addToConsole("" + access.username);
					}
				}
				else
				{
					TE.addToConsole("No owners");
				}
				return true;
			}
			else if (args[1].equalsIgnoreCase("position"))
			{
				TE.addToConsole("position: " + turret.xCoord + "x " + turret.yCoord + "y " + turret.zCoord + "z ");
				return true;
			}
			else if (args[1].equalsIgnoreCase("kills"))
			{
				// TODO track
				TE.addToConsole("Not yet useable");
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canPlayerUse(EntityPlayer var1, ISpecialAccess mm)
	{
		return true;
	}

	@Override
	public boolean showOnHelp(EntityPlayer player, ISpecialAccess mm)
	{
		return true;
	}

	@Override
	public List<String> getCmdUses(EntityPlayer player, ISpecialAccess mm)
	{
		List<String> cmds = new ArrayList<String>();
		cmds.add("get owner");
		cmds.add("get position");
		// cmds.add("get kills");
		// cmds.add("get ammo");
		// cmds.add("get ammoTypes");
		// cmds.add("get attackTypes");
		// cmds.add("get terminal/root");
		return cmds;
	}

	@Override
	public boolean canMachineUse(ISpecialAccess mm)
	{
		return mm instanceof TileEntityTurretPlatform;
	}

}
