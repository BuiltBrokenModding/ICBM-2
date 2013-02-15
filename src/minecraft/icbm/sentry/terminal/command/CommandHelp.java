package icbm.sentry.terminal.command;

import icbm.sentry.gui.GuiTerminal;
import icbm.sentry.terminal.CommandRegistry;
import icbm.sentry.terminal.ISpecialAccess;
import icbm.sentry.terminal.TerminalCommand;

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
	public boolean processCommand(EntityPlayer player, ISpecialAccess TE, GuiTerminal gui, String[] args)
	{
		if (args.length > 1)
		{
			List<String> displayed = new ArrayList<String>();
			for (TerminalCommand cc : CommandRegistry.COMMANDS)
			{
				if (cc.getCommandPrefix().equalsIgnoreCase(args[1]) && cc.showOnHelp(player, TE) && cc.canMachineUse(TE))
				{
					gui.addToConsole("----------------------");
					gui.addToConsole(args[1] + " commands");
					gui.addToConsole("----------------------");
					List<String> ccList = cc.getCmdUses(player, TE);

					for (String cm : ccList)
					{
						if (!displayed.contains(cm.toLowerCase()))
						{
							gui.addToConsole(cm);
							displayed.add(cm.toLowerCase());
						}
					}
					gui.addToConsole("----------------------");
				}
			}
			return true;
		}
		else
		{
			gui.addToConsole("----------------------");
			gui.addToConsole("Listing commands");
			gui.addToConsole("----------------------");
			gui.addToConsole("Help command");
			
			for (TerminalCommand cc : CommandRegistry.COMMANDS)
			{
				if (cc.showOnHelp(player, TE) && cc.canMachineUse(TE))
				{
					List<String> ccList = cc.getCmdUses(player, TE);
					for (String cm : ccList)
					{
						gui.addToConsole(cm);
					}
				}
			}
			gui.addToConsole("-----------------------");
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
