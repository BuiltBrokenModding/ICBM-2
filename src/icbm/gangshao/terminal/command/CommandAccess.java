package icbm.gangshao.terminal.command;

import icbm.gangshao.ISpecialAccess;
import icbm.gangshao.access.AccessLevel;
import icbm.gangshao.platform.TPaoTaiZhan;
import icbm.gangshao.terminal.ITerminal;
import icbm.gangshao.terminal.TerminalCommand;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Manipulates the access level of the turret platform.
 * 
 * @author Darkguardsman, Calclavia
 */
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
		if (args[0].equalsIgnoreCase("access") && args.length > 1 && args[1] != null && terminal instanceof TPaoTaiZhan)
		{
			TPaoTaiZhan turret = (TPaoTaiZhan) terminal;
			AccessLevel userAccess = terminal.getUserAccess(player.username);

			if (args[1].equalsIgnoreCase("?"))
			{
				terminal.addToConsole("Access Level: " + turret.getUserAccess(player.username).displayName);
				return true;
			}
			else if (args[1].equalsIgnoreCase("set") && args.length > 3 && userAccess.ordinal() >= AccessLevel.ADMIN.ordinal())
			{
				String username = args[2];
				AccessLevel playerAccess = terminal.getUserAccess(player.username);
				AccessLevel currentAccess = terminal.getUserAccess(username);

				if (playerAccess.ordinal() >= AccessLevel.ADMIN.ordinal())
				{
					if (currentAccess != AccessLevel.NONE)
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
				else
				{
					terminal.addToConsole("Access denied!");
					return true;
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
		cmds.add("access set username level");
		cmds.add("access ?");
		return cmds;
	}

	@Override
	public boolean canMachineUse(ISpecialAccess mm)
	{
		return mm instanceof TPaoTaiZhan;
	}

}
