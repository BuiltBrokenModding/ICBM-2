package com.builtbroken.icbm.content.items;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.launcher.ILauncher;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.api.items.listeners.IItemActivationListener;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.codegen.annotations.ItemWrapped;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketPlayerItem;
import com.builtbroken.mc.framework.item.ItemNode;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/13/2016.
 */
@ItemWrapped(className = ".gen.ItemWrapperRadarGun", wrappers = "IWorldPos;EnergyUE")
public class ItemRadarGun extends ItemNode implements IItemActivationListener
{
    public ItemRadarGun()
    {
        super(ICBM.DOMAIN, "radarGun");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            MovingObjectPosition objectMouseOver = player.rayTrace(200, 1);
            TileEntity tileEntity = world.getTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
            if (!(tileEntity instanceof ILauncher))
            {
                Engine.packetHandler.sendToServer(new PacketPlayerItem(player, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
            }
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hit_x, float hit_y, float hit_z)
    {
        if (world.isRemote)
        {
            return true;
        }

        Location location = new Location(world, x, y, z);
        TileEntity tile = location.getTileEntity();
        if (tile instanceof IMultiTile)
        {
            IMultiTileHost host = ((IMultiTile) tile).getHost();
            if (host instanceof TileEntity)
            {
                tile = (TileEntity) host;
            }
        }

        if (player.isSneaking())
        {
            stack.setTagCompound(null);
            stack.setItemDamage(0);
            LanguageUtility.addChatToPlayer(player, "gps.cleared");
            player.inventoryContainer.detectAndSendChanges();
            return true;
        }
        else
        {
            Location storedLocation = ((IWorldPosItem) item).getLocation(stack);
            if (storedLocation == null || !storedLocation.isAboveBedrock())
            {
                LanguageUtility.addChatToPlayer(player, "gps.error.pos.invalid");
                return true;
            }
            else if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof TileAbstractLauncher)
            {
                ((TileAbstractLauncher) ((ITileNodeHost) tile).getTileNode()).setTarget(storedLocation.toPos());
                LanguageUtility.addChatToPlayer(player, "gps.data.transferred");
                return true;
            }
        }
        return false;
    }

    @Override
    public void readPacketData(ByteBuf buf, EntityPlayer player, ItemStack stack)
    {
        ((IWorldPosItem) item).setLocation(stack, new Location(player.worldObj, buf.readInt(), buf.readInt(), buf.readInt()));
        player.addChatComponentMessage(new ChatComponentText("GPS data set"));
    }
}
