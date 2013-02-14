package icbm.sentry.terminal;

import icbm.sentry.gui.GuiTerminal;
import icbm.sentry.terminal.command.CommandHelp;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class CommandRegistry
{
	public static final List<TerminalCommand> COMMANDS = new ArrayList<TerminalCommand>();

	/**
	 * 
	 * @param prefix - what the command starts with for example /time
	 * @param cmd - Cmd instance that will execute the command
	 */
	public static void register(TerminalCommand cmd)
	{
		if (!COMMANDS.contains(cmd))
		{
			COMMANDS.add(cmd);
		}
	}

	/**
	 * When a player uses a command in any CMD machine it pass threw here first
	 * 
	 * @param player
	 * @param TE
	 * @param gui
	 * @param cmd
	 */
	public static void onCommand(EntityPlayer player, ISpecialAccess TE, GuiTerminal gui, String cmd)
	{
		boolean wasUsed = false;
		TerminalCommand command = null;
		String[] args = cmd.split(" ");
		if (args[0] != null)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				command = new CommandHelp();
				if (command.processCommand(player, TE, gui, args))
				{
					wasUsed = true;
				}
			}
			else
			{
				for (TerminalCommand cm : COMMANDS)
				{
					if (cm.getCommandPrefix().equalsIgnoreCase(args[0]))
					{
						if (!cm.canMachineUse(TE))
						{
							gui.addToConsole("N/A");
							wasUsed = true;
							break;
						}
						else
						{
							if (!cm.canPlayerUse(player, TE))
							{
								gui.addToConsole("Access Denied.");
								wasUsed = true;
								break;
							}
							else
							{
								if (cm.processCommand(player, TE, gui, args))
								{
									wasUsed = true;
									command = cm;
									break;
								}
							}
						}
					}
				}
			}
		}
		if (!wasUsed)
		{
			gui.addToConsole("Unkown Command.");
		}
	}

}
