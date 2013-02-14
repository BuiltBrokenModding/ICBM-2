package icbm.sentry.terminal.commands;

import icbm.sentry.gui.GuiConsole;
import icbm.sentry.platform.TileEntityTurretPlatform;
import icbm.sentry.terminal.AccessLevel;
import icbm.sentry.terminal.ConsoleCommand;
import icbm.sentry.terminal.ISpecialAccess;
import icbm.sentry.terminal.UserAccess;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class CmdGet extends ConsoleCommand
{

	@Override
	public String getCommandPrefix()
	{
		return "get";
	}

	@Override
	public boolean processCommand(EntityPlayer player, ISpecialAccess TE, GuiConsole gui, String[] args)
	{
		if (args[0].equalsIgnoreCase("get") && args.length > 1 && args[1] != null && TE instanceof TileEntityTurretPlatform)
		{
			TileEntityTurretPlatform turret = (TileEntityTurretPlatform) TE;
			if (args[1].equalsIgnoreCase("owner"))
			{
				List<UserAccess> userList = turret.getUsersWithAcess(AccessLevel.OWNER);
				for (UserAccess access : userList)
				{
					gui.addToConsole("" + access.username);
				}
				return true;
			}
			else if (args[1].equalsIgnoreCase("loc"))
			{
				gui.addToConsole("Loc: " + turret.xCoord + "x " + turret.yCoord + "y " + turret.zCoord + "z ");
				return true;
			}
			else if (args[1].equalsIgnoreCase("kills"))
			{
				// TODO track
				gui.addToConsole("Not yet useable");
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
		cmds.add("get kills");
		cmds.add("get ammo");
		cmds.add("get ammoTypes");
		cmds.add("get attackTypes");
		cmds.add("get loc");
		cmds.add("get termanal/root");
		return cmds;
	}

	@Override
	public boolean canMachineUse(ISpecialAccess mm)
	{
		return mm instanceof TileEntityTurretPlatform;
	}

}
