package icbm.sentry;

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
import universalelectricity.api.vector.VectorWorld;
import calclavia.lib.prefab.terminal.CommandRegistry;

/** Command used to interact and debug sentry guns using a command line chat system
 * 
 * @author DarkGuardsman */
public class CommandSentry extends CommandBase
{
    public static HashMap<String, VectorWorld> selection = new HashMap<String, VectorWorld>();

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
            }
            else if (sender instanceof EntityPlayer && selection.containsKey(((EntityPlayer) sender).username))
            {
                VectorWorld selected = selection.get(((EntityPlayer) sender).username);
                if (selected != null && selected.getTileEntity() instanceof TileTurret)
                {
                    if (args[0].equalsIgnoreCase("terminal"))
                    {
                        if (args.length > 1)
                        {
                            String terminal_cmd = args[1];
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
    public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand) par1Obj);
    }
}