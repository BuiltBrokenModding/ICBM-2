package icbm.sentry;

import java.util.HashMap;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import universalelectricity.api.vector.VectorWorld;

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
                    if (hit.getBlockID() == ICBMSentry.blockTurret.blockID)
                    {
                        event.entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Selecting sentry at " + hit.toString()));
                        event.entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Sentry is awaiting orders");
                    }
                }
            }
        }
    }
}