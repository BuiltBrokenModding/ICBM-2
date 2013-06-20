package icbm.gangshao.terminal.command;

import icbm.gangshao.platform.TileEntityTurretPlatform;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import dark.core.api.ISpecialAccess;
import dark.core.api.ITerminal;
import dark.library.access.AccessLevel;
import dark.library.machine.terminal.TerminalCommand;

/** Manipulates the access level of the turret platform.
 * 
 * @author Darkguardsman, Calclavia */
public class CommandAccess extends TerminalCommand
{

	@Override
	public String getCommandPrefix()
	{
		return "access";
	}

	@Override
	public boolean processCommand(EntityPlayer player, ITerminal terminal, String[] args)
	{
		if (args[0].equalsIgnoreCase("access") && args.length > 1 && args[1] != null && terminal instanceof TileEntityTurretPlatform)
		{
			TileEntityTurretPlatform turret = (TileEntityTurretPlatform) terminal;
			AccessLevel userAccess = terminal.getUserAccess(player.username);

			if (args[1].equalsIgnoreCase("?"))
			{
				terminal.addToConsole("Access Level: " + turret.getUserAccess(player.username).displayName);
				return true;
			}
			else if (args[1].equalsIgnoreCase("set") && args.length > 3 && userAccess.ordinal() >= AccessLevel.ADMIN.ordinal())
			{
				String username = args[2];
				AccessLevel currentAccess = terminal.getUserAccess(username);

				if (username.equalsIgnoreCase("~root"))
				{
					terminal.addToConsole("WIP");
				}
				else if (currentAccess != AccessLevel.NONE)
				{
					AccessLevel newAccess = AccessLevel.get(args[3]);

					if (currentAccess != AccessLevel.OWNER || turret.getUsersWithAcess(AccessLevel.OWNER).size() > 1)
					{
						if (newAccess != AccessLevel.NONE && terminal.addUserAccess(username, newAccess, true))
						{
							terminal.addToConsole(username + " set to " + newAccess.displayName);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean canPlayerUse(EntityPlayer var1, ISpecialAccess mm)
	{
		return mm.getUserAccess(var1.username).ordinal() >= AccessLevel.BASIC.ordinal() || var1.capabilities.isCreativeMode;
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
		cmds.add("access set ~root [pass]");
		cmds.add("access set username level");
		cmds.add("access ?");
		return cmds;
	}

	@Override
	public boolean canMachineUse(ISpecialAccess mm)
	{
		return mm instanceof TileEntityTurretPlatform;
	}

}
