package icbm.sentry.platform.gui.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import calclavia.lib.access.AccessGroup;
import calclavia.lib.access.AccessProfile;
import calclavia.lib.access.AccessUser;
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

                if (command != null && command.equalsIgnoreCase("help"))
                {
                    output_to_console.add("Listing commands");
                    output_to_console.add("-------------------------------------");
                    output_to_console.add("/access user list - lists all users");
                    output_to_console.add("/access user add [group] [username] - adds a user to the group");
                    output_to_console.add("/access user remove [username] - removes a user");
                    output_to_console.add("/access user gset [username] [group] - sets t user's group");
                    output_to_console.add("/access user addnode [username] [node] - adds a permission node to the user");
                    output_to_console.add("/access user removenode [username] [node] - removes a permission node from a user");
                    output_to_console.add("/access group list");
                    output_to_console.add("/access group create [group] <extend> - creates a new group, optional group to extend");
                    output_to_console.add("/access group del [group] <move_group> - removes a group, optional group to move users to");
                    output_to_console.add("/access group gset [group] [extend] - sets the group to extend another group");
                    output_to_console.add("/access group addnode [group] [node] - adds a permission node to the group");
                    output_to_console.add("/access group removenode [group] [node] - removes a permission node from a group");
                    output_to_console.add("-------------------------------------");
                    return output_to_console;
                }
                else if (command != null && command.equalsIgnoreCase("group"))
                {
                    if (args.length > 2 && args[2] != null)
                    {
                        String group_sub_command = args[2];
                        if (group_sub_command.equalsIgnoreCase("list"))
                        {
                            output_to_console.add("Listing groups " + profile.getName());
                            output_to_console.add("-------------------------------------");
                            for (AccessGroup group : profile.getGroups())
                            {
                                output_to_console.add("--" + group.getName() + (group.getExtendGroup() != null ? " extends " + group.getExtendGroup().getName() : ""));
                            }
                            output_to_console.add("-------------------------------------");
                            return output_to_console;
                        }
                        else if (group_sub_command.equalsIgnoreCase("create"))
                        {
                            if (args.length > 3 && args[3] != null)
                            {
                                AccessGroup group = profile.getGroup(args[3]);
                                if (group != null)
                                {
                                    output_to_console.add("Group with that name already exists");
                                }
                                else
                                {
                                    group = new AccessGroup(args[3]);
                                    profile.getGroups().add(group);
                                    if (args.length > 4 && args[4] != null)
                                    {
                                        AccessGroup extendGroup = profile.getGroup(args[4]);
                                        if (extendGroup != null)
                                        {
                                            group.setToExtend(extendGroup);
                                        }
                                        else
                                        {
                                            output_to_console.add("Unable to find group to extend");
                                        }
                                    }
                                    output_to_console.add("Group created");
                                }
                            }
                            else
                            {
                                output_to_console.add("Missing group name");
                            }
                            return output_to_console;
                        }
                        else if (group_sub_command.equalsIgnoreCase("del"))
                        {
                            if (args.length > 3 && args[3] != null)
                            {
                                AccessGroup group = profile.getGroup(args[3]);
                                if (group != null)
                                {
                                    if (args.length > 4)
                                    {
                                        AccessGroup moveGroup = profile.getGroup(args[4]);
                                        if (moveGroup != null)
                                        {
                                            moveGroup.addMemebers(group.getMembers());
                                            group.getMembers().clear();
                                            output_to_console.add("Users move to '" + moveGroup.getName() + "'");
                                        }
                                        else
                                        {
                                            output_to_console.add("Invalid group to move users into");
                                        }
                                    }
                                    profile.getGroups().remove(group);
                                    output_to_console.add("Group removed and destroyed");
                                }
                                else
                                {
                                    output_to_console.add("No group found");
                                }
                            }
                            else
                            {
                                output_to_console.add("Missing group name");
                            }
                            return output_to_console;
                        }
                        else if (group_sub_command.equalsIgnoreCase("gset"))
                        {
                            if (args.length > 3 && args[3] != null)
                            {
                                AccessGroup group = profile.getGroup(args[3]);
                                if (group != null)
                                {
                                    if (args.length > 4 && args[4] != null)
                                    {
                                        AccessGroup extendGroup = profile.getGroup(args[4]);
                                        if (extendGroup != null)
                                        {
                                            group.setToExtend(extendGroup);
                                        }
                                        else
                                        {
                                            output_to_console.add("Unable to find group to extend");
                                        }
                                    }
                                    else
                                    {
                                        output_to_console.add("Missing name of the group to extend");
                                    }
                                }
                                else
                                {
                                    output_to_console.add("Unable to find group");
                                }
                            }
                            else
                            {
                                output_to_console.add("Missing group name");
                            }
                            return output_to_console;
                        }
                        else if (group_sub_command.equalsIgnoreCase("addnode"))
                        {
                            if (args.length > 3 && args[3] != null)
                            {
                                AccessGroup group = profile.getGroup(args[3]);
                                if (group != null)
                                {
                                    if (args.length > 4 && args[4] != null)
                                    {
                                        if (!group.hasNode(args[4]))
                                        {
                                            group.addNode(args[4]);
                                            output_to_console.add("Node added");
                                        }
                                        else
                                        {
                                            output_to_console.add("Node already added, or include in super group");
                                        }
                                    }
                                    else
                                    {
                                        output_to_console.add("Provide a permission node to add to the group");
                                    }
                                }
                                else
                                {
                                    output_to_console.add("Unable to find group");
                                }
                            }
                            else
                            {
                                output_to_console.add("Missing group name");
                            }
                            return output_to_console;
                        }
                        else if (group_sub_command.equalsIgnoreCase("removenode"))
                        {
                            if (args.length > 3 && args[3] != null)
                            {
                                AccessGroup group = profile.getGroup(args[3]);
                                if (group != null)
                                {
                                    if (args.length > 4 && args[4] != null)
                                    {
                                        if (group.hasNode(args[4]))
                                        {
                                            group.removeNode(args[4]);
                                            output_to_console.add("Node removed");
                                        }
                                        else
                                        {
                                            output_to_console.add("Node not found");
                                        }
                                    }
                                    else
                                    {
                                        output_to_console.add("Provide a permission node to remove from the group");
                                    }
                                }
                                else
                                {
                                    output_to_console.add("Unable to find group");
                                }
                            }
                            else
                            {
                                output_to_console.add("Missing group name");
                            }
                            return output_to_console;
                        }
                    }
                    else
                    {
                        output_to_console.add("Too few arguments");
                        return output_to_console;
                    }
                }
                else if (command != null && command.equalsIgnoreCase("user"))
                {
                    String user_sub_command = args[2];
                    if (user_sub_command.equalsIgnoreCase("list"))
                    {
                        output_to_console.add("Listing users for access profile " + profile.getName());
                        output_to_console.add("-------------------------------------");
                        for (AccessGroup group : profile.getGroups())
                        {
                            output_to_console.add("--Group: " + group.getName());
                            for (AccessUser user : group.getMembers())
                            {
                                output_to_console.add("      --" + user.getName());
                            }
                            output_to_console.add(" ");
                        }
                        output_to_console.add("-------------------------------------");
                        return output_to_console;
                    }
                    else
                    //Remove User
                    if (user_sub_command.equalsIgnoreCase("remove") && args.length > 3)
                    {
                        String username = args[3];
                        if (username != null)
                        {
                            if (username.equalsIgnoreCase(player.username))
                            {
                                output_to_console.add("You can not remove yourself");
                            }
                            else if (profile.setUserAccess(username, null, false))
                            {
                                output_to_console.add("Removed: " + username);
                            }
                            else
                            {
                                output_to_console.add("User not found.");
                            }
                        }
                        else
                        {
                            output_to_console.add("Invalid username.");
                        }
                        return output_to_console;
                    }
                    else
                    //Add user
                    if (user_sub_command.equalsIgnoreCase("add") && args.length > 3)
                    {
                        AccessGroup group = profile.getGroup(args[3]);
                        if (group != null && args.length > 4)
                        {
                            String username = args[4];
                            if (group.isMemeber(username))
                            {
                                output_to_console.add("User is already a member of the group.");
                            }
                            else if (profile.setUserAccess(username, group, true))
                            {
                                output_to_console.add("Added: '" + username + "' to group '" + group.getName() + "'");
                            }
                            else
                            {
                                output_to_console.add("Error adding user to group");
                            }
                        }
                        else
                        {
                            output_to_console.add("Invalid group.");
                        }
                        return output_to_console;
                    }//Set group
                    else if (user_sub_command.equalsIgnoreCase("gset"))
                    {
                        if (args.length > 3 && args[3] != null)
                        {
                            if (args.length > 4 && args[4] != null)
                            {
                                AccessGroup group = profile.getGroup(args[4]);
                                if (group.isMemeber(args[3]))
                                {
                                    output_to_console.add("User is already a member of the group.");
                                }
                                else
                                {
                                    AccessUser user = profile.getUserAccess(args[3]);
                                    if (user != null && user.getGroup() != null)
                                    {
                                        user.getGroup().removeMemeber(user);
                                        user.setGroup(group);
                                        output_to_console.add("Added: '" + args[3] + "' to group '" + group.getName() + "'");
                                    }
                                    else
                                    {
                                        output_to_console.add("Invalid user");
                                    }
                                }
                            }
                            else
                            {
                                output_to_console.add("Missing group");
                            }
                        }
                        else
                        {
                            output_to_console.add("Missing username");
                        }
                        return output_to_console;
                    }
                    else if (user_sub_command.equalsIgnoreCase("addnode"))
                    {
                        output_to_console.add("Not implemented");
                        return output_to_console;
                    }
                    else if (user_sub_command.equalsIgnoreCase("removenode"))
                    {
                        output_to_console.add("Not implemented");
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
