package icbm.sentry.platform.gui.user;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import calclavia.lib.access.AccessGroup;
import calclavia.lib.access.AccessProfile;
import calclavia.lib.access.IProfileContainer;
import calclavia.lib.prefab.terminal.ITerminal;
import calclavia.lib.prefab.terminal.ITerminalCommand;

public class CommandUser implements ITerminalCommand
{
    @Override
    public String getCommandName()
    {
        return "users";
    }

    @Override
    public boolean called(EntityPlayer player, ITerminal terminal, String[] args)
    {
        if (args[0].equalsIgnoreCase(this.getCommandName()) && args.length > 1 && args[1] != null)
        {
            if (terminal instanceof IProfileContainer)
            {
                IProfileContainer container = (IProfileContainer) terminal;
                AccessProfile profile = container.getAccessProfile();
                String sub_command = args[1];
                
                //Remove User
                if (sub_command.equalsIgnoreCase("remove") && args.length > 2)
                {
                    String username = args[2];
                    if (username != null)
                    {
                        if (container.getAccessProfile().setUserAccess(username, null, false))
                        {
                            terminal.addToConsole("Removed: " + username);
                            return true;
                        }
                        else
                        {
                            terminal.addToConsole(" User not found.");
                            return true;
                        }
                    }
                    else
                    {
                        terminal.addToConsole("Invalid username.");
                        return true;
                    }
                }
                //Add user
                if (sub_command.equalsIgnoreCase("add") && args.length > 2 && args[2] != null)
                {
                    AccessGroup group = container.getAccessProfile().getGroup(args[2]);
                    if (group != null && args.length > 3)
                    {
                        String username = args[3];
                        if (group.isMemeber(username))
                        {
                            terminal.addToConsole("User already exists.");
                        }
                        else if (container.getAccessProfile().setUserAccess(username, group, true))
                        {
                            terminal.addToConsole("Added: " + username + " to group " + group.getName());
                        }
                        else
                        {
                            terminal.addToConsole("Invalid username.");
                        }
                    }
                    else
                    {
                        terminal.addToConsole("Invalid group.");
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean canSupport(ITerminal mm)
    {
        return mm instanceof IProfileContainer;
    }

    @Override
    public Set<String> getPermissionNodes()
    {
        Set<String> nodes = new HashSet<String>();
        nodes.add("add");
        nodes.add("remove");
        nodes.add("list");
        return nodes;
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
                    if (args[1] != null && args[1].equalsIgnoreCase("add"))
                    {
                        return "users.add";
                    }
                    if (args[1] != null && args[1].equalsIgnoreCase("remove"))
                    {
                        return "users.remove";
                    }
                    if (args[1] != null && args[1].equalsIgnoreCase("list"))
                    {
                        return "users.list";
                    }
                }
                return "users";
            }
        }
        return null;
    }
}
