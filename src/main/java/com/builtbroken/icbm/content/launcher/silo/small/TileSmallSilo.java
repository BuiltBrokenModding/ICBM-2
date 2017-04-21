package com.builtbroken.icbm.content.launcher.silo.small;

import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.api.tile.listeners.IDestroyedListener;
import com.builtbroken.mc.api.tile.listeners.IPlacementListener;
import com.builtbroken.mc.codegen.annotations.MultiBlockWrapped;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Small 2 block tall self contained launcher for small missiles.
 * Created by robert on 3/28/2015.
 */
@TileWrapped(className = "TileWrapperSmallSilo")
@MultiBlockWrapped()
public class TileSmallSilo extends TileAbstractLauncher implements IPlacementListener, IDestroyedListener, IRotation
{
    private ForgeDirection rotationCache;

    @Override
    public boolean canAcceptMissile(IMissile missile)
    {
        return super.canAcceptMissile(missile) && missile.getMissileSize() == MissileCasings.SMALL.ordinal();
    }

    @Override
    public boolean canPlaceOnSide(int side)
    {
        return side == ForgeDirection.UP.ordinal();
    }

    @Override
    public boolean removedByPlayer(EntityPlayer player, boolean willHarvest)
    {
        if (willHarvest && getMissileItem() != null)
        {
            InventoryUtility.dropItemStack(toLocation(), getMissileItem());
            getInventory().setInventorySlotContents(0, null);
        }
        return false;
    }

    @Override
    public ForgeDirection getDirection()
    {
        if (rotationCache == null)
        {
            rotationCache = ForgeDirection.getOrientation(getHost().getHostMeta()).getOpposite();
        }
        return rotationCache;
    }
}
