package icbm.sentry.platform.gui.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import calclavia.lib.access.AccessGroup;
import calclavia.lib.access.AccessProfile;
import calclavia.lib.access.IProfileContainer;
import calclavia.lib.access.Nodes;
import calclavia.lib.prefab.terminal.ITerminal;
import calclavia.lib.prefab.terminal.ITerminalCommand;

/** Command used to modify the access profile of a tile
 * 
 * @author DarkGuardsman */
public class TerminalAccessCMD implements ITerminalCommand
{
    @Override
    public String getCommandName()
    {
        return "access";
    }

    @Override
    public List<String> called(EntityPlayer player, ITerminal terminal, String[] args)
    {
        if (args[0].equalsIgnoreCase(this.getCommandName()) && args.length > 1 && args[1] != null)
        {
            String command = args[1];

            if (terminal instanceof IProfileContainer)
            {
                IProfileContainer container = (IProfileContainer) terminal;
                AccessProfile profile = container.getAccessProfile();
                List<String> output_to_console = new ArrayList<String>();

                if (command != null && command.equalsIgnoreCase("user"))
                {
                    String user_sub_command = args[2];

                    //Remove User
                    if (user_sub_command.equalsIgnoreCase("remove") && args.length > 3)
                    {
                        String username = args[3];
                        if (username != null)
                        {
                            if (container.getAccessProfile().setUserAccess(username, null, false))
                            {
                                output_to_console.add("Removed: " + username);
                            }
                            else
                            {
                                output_to_console.add(" User not found.");
                            }
                        }
                        else
                        {
                            output_to_console.add("Invalid username.");
                        }
                    }
                    else
                    //Add user
                    if (user_sub_command.equalsIgnoreCase("add") && args.length > 3 && args[3] != null)
                    {
                        AccessGroup group = container.getAccessProfile().getGroup(args[3]);
                        if (group != null && args.length > 4)
                        {
                            String username = args[4];
                            if (group.isMemeber(username))
                            {
                                output_to_console.add("User already exists.");
                            }
                            else if (container.getAccessProfile().setUserAccess(username, group, true))
                            {
                                output_to_console.add("Added: " + username + " to group " + group.getName());
                            }
                            else
                            {
                                output_to_console.add("Invalid username.");
                            }
                        }
                        else
                        {
                            output_to_console.add("Invalid group.");
                        }
                        return output_to_console;
                    }
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public boolean canSupport(ITerminal mm)
    {
        return mm instanceof IProfileContainer;
    }

    @Override
    public Set<String> getPermissionNodes()
    {
        return null;
    }

    @Override
    public String getNode(String[] args)
    {
        if (args != null && args.length >= 1)
        {
            if (args[0] != null && args[0].equalsIgnoreCase(this.getCommandName()))
            {
                if (args.length >= 2)
                {
                    if (args[1] != null && args[1].equalsIgnoreCase("user"))
                    {
                        if (args[2] != null && args[2].equalsIgnoreCase("add"))
                        {
                            return Nodes.GROUP_ADD_USER;
                        }
                        if (args[2] != null && args[2].equalsIgnoreCase("remove"))
                        {
                            return Nodes.GROUP_REMOVE_USER;
                        }
                        if (args[2] != null && args[2].equalsIgnoreCase("list"))
                        {
                            return "group.user.list";
                        }
                    }
                }
                return "users";
            }
        }
        return null;
    }
}
