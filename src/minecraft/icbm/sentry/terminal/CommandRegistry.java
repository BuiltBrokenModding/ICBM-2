package icbm.sentry.terminal;

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
	 * @param terminal - The terminal, can be cast to TileEntity.
	 */
	public static void onCommand(EntityPlayer player, ITerminal terminal, String cmd)
	{
		if (cmd != null && cmd != "")
		{
			TerminalCommand currentCommand = null;
			String[] args = cmd.split(" ");
			terminal.addToConsole("\u00a7A" + player.username + ": " + cmd);

			if (args[0] != null)
			{
				for (TerminalCommand command : COMMANDS)
				{
					if (command.getCommandPrefix().equalsIgnoreCase(args[0]))
					{
						if (!command.canMachineUse(terminal))
						{
							terminal.addToConsole("N/A");
							return;
						}
						else
						{
							if (!command.canPlayerUse(player, terminal))
							{
								terminal.addToConsole("Access Denied.");
								return;
							}
							else
							{
								if (command.processCommand(player, terminal, args))
								{
									currentCommand = command;
									return;
								}
							}
						}
					}
				}
			}

			terminal.addToConsole("Unkown Command.");
		}
	}

}
