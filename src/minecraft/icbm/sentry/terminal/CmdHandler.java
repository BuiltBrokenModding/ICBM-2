package icbm.sentry.terminal;

import icbm.sentry.gui.GuiConsole;
import icbm.sentry.terminal.commands.CmdHelp;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class CmdHandler
{
	public static List<ConsoleCommand> cmds = new ArrayList<ConsoleCommand>();

	/**
	 * 
	 * @param prefix - what the command starts with for example /time
	 * @param cmd - Cmd instance that will execute the command
	 */
	public static void regCmd(ConsoleCommand cmd)
	{
		if (!cmds.contains(cmd))
		{
			cmds.add(cmd);
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
	public static void onCmd(EntityPlayer player, ISpecialAccess TE, GuiConsole gui, String cmd)
	{
		boolean wasUsed = false;
		ConsoleCommand command = null;
		String[] args = cmd.split(" ");
		if (args[0] != null)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				command = new CmdHelp();
				if (command.processCommand(player, TE, gui, args))
				{
					wasUsed = true;
				}

			}
			else
			{
				for (ConsoleCommand cm : cmds)
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
								gui.addToConsole("no access");
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
			gui.addToConsole("unkown command");
		}
	}

}
