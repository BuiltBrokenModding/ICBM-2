package com.builtbroken.icbm.content.items;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.api.items.listeners.IItemActivationListener;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.codegen.annotations.ItemWrapped;
import com.builtbroken.mc.framework.item.ItemNode;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Basic item that can stored the location it right clicks
 * Created by Dark on 6/2/2015.
 */
@ItemWrapped(className = ".gen.ItemWrapperGPSFlag", wrappers = "IWorldPos;EnergyUE")
public class ItemGPSFlag extends ItemNode implements IItemActivationListener
{
    public ItemGPSFlag()
    {
        super(ICBM.DOMAIN, "gpsFlag");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (player.isSneaking())
        {
            stack.setTagCompound(null);
            stack.setItemDamage(0);
            LanguageUtility.addChatToPlayer(player, "gps.cleared");
            player.inventoryContainer.detectAndSendChanges();
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hit_x, float hit_y, float hit_z)
    {
        if (stack.getItem() instanceof IWorldPosItem)
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
                ((IWorldPosItem) stack.getItem()).setLocation(stack, location);
                LanguageUtility.addChatToPlayer(player, "gps.pos.set");
                stack.setItemDamage(1);
                player.inventoryContainer.detectAndSendChanges();
                return true;
            }
            else
            {
                Location storedLocation = ((IWorldPosItem) stack.getItem()).getLocation(stack);
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
        }
        return false;
    }
}
