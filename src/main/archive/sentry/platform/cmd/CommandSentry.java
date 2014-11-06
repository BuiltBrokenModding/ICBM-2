package icbm.sentry.platform.cmd;

import icbm.sentry.turret.block.TileTurret;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import resonant.lib.prefab.terminal.CommandRegistry;
import universalelectricity.api.vector.VectorWorld;

/** Command used to interact and debug sentry guns using a command line chat system
 * 
 * @author DarkGuardsman */
public class CommandSentry extends CommandBase
{
    public static HashMap<String, VectorWorld> selection = new HashMap<String, VectorWorld>();
    public static HashMap<EntityPlayer, Long> terminalSet = new HashMap<EntityPlayer, Long>();

    @Override
    public String getCommandName()
    {
        return "sentry";
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/icbm help";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args != null && args.length > 0 && args[0] != null)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/Sentry Terminal"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/Sentry Terminal set [true/false]"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/Sentry debug [on/off]"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/Sentry kills"));
            }
            else if (sender instanceof EntityPlayer && selection.containsKey(((EntityPlayer) sender).username))
            {
                VectorWorld selected = selection.get(((EntityPlayer) sender).username);
                if (terminalSet.containsKey((EntityPlayer) sender) && System.currentTimeMillis() - terminalSet.get((EntityPlayer) sender) > 1000000)
                {
                    terminalSet.remove((EntityPlayer) sender);
                }
                if (selected != null && selected.getTileEntity() instanceof TileTurret)
                {
                    if (args[0].equalsIgnoreCase("kills"))
                    {
                        sender.sendChatToPlayer(ChatMessageComponent.createFromText("Total kills: " + ((TileTurret) selected.getTileEntity()).getTurret().getKillCount()));
                    }
                    else if (args[0].equalsIgnoreCase("debug"))
                    {
                        boolean on = true;
                        if (args.length > 1 && args[1] != null)
                        {
                            if (args[1].equalsIgnoreCase("on"))
                            {
                                on = true;
                            }
                            else if (args[1].equalsIgnoreCase("off"))
                            {
                                on = false;
                            }
                        }
                        else
                        {
                            on = !((TileTurret) selected.getTileEntity()).getTurret().getAi().debugMode;
                        }
                        ((TileTurret) selected.getTileEntity()).getTurret().getAi().debugMode = on;
                        sender.sendChatToPlayer(ChatMessageComponent.createFromText("Debug mode switched to " + on));
                    }
                    else if (args[0].equalsIgnoreCase("terminal") || terminalSet.containsKey((EntityPlayer) sender))
                    {
                        if (!args[0].equalsIgnoreCase("terminal"))
                        {
                            String[] newArgs = new String[args.length + 1];
                            for(int i = 0; i < args.length; i++)
                            {
                                newArgs[i + 1] = args[i];
                            }
                            args = newArgs;
                        }
                        if (args.length > 1)
                        {
                            String terminal_cmd = args[1];
                            if (terminal_cmd.equalsIgnoreCase("set"))
                            {
                                boolean to = terminalSet.containsKey((EntityPlayer) sender) && System.currentTimeMillis() - terminalSet.get((EntityPlayer) sender) < 1000000 ? false : true;
                                if (args.length > 2)
                                {
                                    if (args[2] != null && args[2].equalsIgnoreCase("true"))
                                    {
                                        to = true;
                                    }
                                }
                                if (to)
                                    terminalSet.put((EntityPlayer) sender, System.currentTimeMillis());
                                else if (terminalSet.containsKey((EntityPlayer) sender))
                                    terminalSet.remove((EntityPlayer) sender);
                                sender.sendChatToPlayer(ChatMessageComponent.createFromText("Terminal lock set to " + to));
                            }
                            else
                            {
                                for (int i = 2; i < args.length; i++)
                                {
                                    terminal_cmd += " " + args[i];
                                }
                                List<String> out = CommandRegistry.onCommand(((EntityPlayer) sender), (TileTurret) selected.getTileEntity(), terminal_cmd);
                                if (out != null && !out.isEmpty())
                                {
                                    for (String output : out)
                                    {
                                        sender.sendChatToPlayer(ChatMessageComponent.createFromText(output));
                                    }
                                }
                                else
                                {
                                    sender.sendChatToPlayer(ChatMessageComponent.createFromText("Error in terminal command"));
                                }
                            }
                        }
                        else
                        {
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText("You must provide a terminal command to run"));
                        }
                    }
                    else
                    {
                        sender.sendChatToPlayer(ChatMessageComponent.createFromText("/Sentry help"));
                    }
                }
                else
                {
                    sender.sendChatToPlayer(ChatMessageComponent.createFromText("No sentry selected"));
                }
            }
            else
            {
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("You must select a sentry"));
            }
        }
        else
        {
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("/Sentry help"));
        }
    }

    @ForgeSubscribe
    public void onPlayInteract(PlayerInteractEvent event)
    {
        if (event.action == Action.RIGHT_CLICK_BLOCK)
        {
            if (event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().itemID == Item.blazeRod.itemID)
            {
                if (event.entityPlayer.isSneaking())
                {
                    VectorWorld hit = new VectorWorld(event.entity.worldObj, event.x, event.y, event.z);
                    TileEntity tile = hit.getTileEntity();
                    if (tile instanceof TileTurret)
                    {
                        TileTurret sentry = (TileTurret) tile;
                        event.entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Selecting sentry at " + hit.toString()));
                        if (sentry.canAccess(event.entityPlayer.username) || event.entityPlayer.capabilities.isCreativeMode)
                        {
                            event.entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Sentry is awaiting orders"));
                            CommandSentry.selection.put(event.entityPlayer.username, hit);
                        }
                        else
                        {
                            event.entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Sentry rejects your access"));
                        }
                        if (event.isCancelable())
                            event.setCanceled(true);
                    }
                }
            }
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] { "reset", "terminal" }) : null;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return true;
    }

    @Override
    public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand) par1Obj);
    }
}