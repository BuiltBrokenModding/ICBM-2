package icbm.gangshao.terminal.command;

import icbm.gangshao.ISpecialAccess;
import icbm.gangshao.ITerminal;
import icbm.gangshao.terminal.CommandRegistry;
import icbm.gangshao.terminal.TerminalCommand;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class CommandHelp extends TerminalCommand
{
	@Override
	public String getCommandPrefix()
	{
		return "help";
	}

	@Override
	public boolean processCommand(EntityPlayer player, ITerminal TE, String[] args)
	{
		if (args.length > 1)
		{
			List<String> displayed = new ArrayList<String>();

			for (TerminalCommand cc : CommandRegistry.COMMANDS)
			{
				if (cc.getCommandPrefix().equalsIgnoreCase(args[1]) && cc.showOnHelp(player, TE) && cc.canMachineUse(TE))
				{
					TE.addToConsole("----------------------");
					TE.addToConsole(args[1] + " commands");
					TE.addToConsole("----------------------");
					List<String> ccList = cc.getCmdUses(player, TE);

					for (String cm : ccList)
					{
						if (!displayed.contains(cm.toLowerCase()))
						{
							TE.addToConsole(cm);
							displayed.add(cm.toLowerCase());
						}
					}
					TE.addToConsole("----------------------");
				}
			}
			return true;
		}
		else
		{
			TE.addToConsole("----------------------");
			TE.addToConsole("Listing commands");
			TE.addToConsole("----------------------");
			TE.addToConsole("Help command");

			for (TerminalCommand cc : CommandRegistry.COMMANDS)
			{
				if (cc.showOnHelp(player, TE) && cc.canMachineUse(TE))
				{
					List<String> ccList = cc.getCmdUses(player, TE);
					for (String cm : ccList)
					{
						TE.addToConsole(cm);
					}
				}
			}
			TE.addToConsole("-----------------------");
			return true;
		}
	}

	@Override
	public boolean canPlayerUse(EntityPlayer var1, ISpecialAccess mm)
	{
		return true;
	}

	@Override
	public boolean showOnHelp(EntityPlayer player, ISpecialAccess mm)
	{
		return false;
	}

	@Override
	public List<String> getCmdUses(EntityPlayer player, ISpecialAccess mm)
	{
		return null;
	}

	@Override
	public boolean canMachineUse(ISpecialAccess mm)
	{
		return true;
	}

}
