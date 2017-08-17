package com.builtbroken.icbm.content.launcher.silo.small;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.mc.api.data.ActionResponse;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.framework.block.imp.IDestroyedListener;
import com.builtbroken.mc.framework.block.imp.IPlacementListener;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Small 2 block tall self contained launcher for small missiles.
 * Created by robert on 3/28/2015.
 */
@TileWrapped(className = "TileWrapperSmallSilo", wrappers = "MultiBlock")
public class TileSmallSilo extends TileAbstractLauncher implements IPlacementListener, IDestroyedListener, IRotation
{
    private ForgeDirection rotationCache;

    public TileSmallSilo()
    {
        super("silo.small", ICBM.DOMAIN);
    }

    @Override
    public boolean canAcceptMissile(IMissile missile)
    {
        return super.canAcceptMissile(missile) && missile.getMissileSize() == MissileSize.SMALL.ordinal();
    }

    @Override
    public ActionResponse canPlaceOnSide(int side)
    {
        return side == ForgeDirection.UP.ordinal() ? ActionResponse.DO : ActionResponse.CANCEL;
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
