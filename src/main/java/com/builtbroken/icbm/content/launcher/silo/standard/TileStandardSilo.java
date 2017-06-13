package com.builtbroken.icbm.content.launcher.silo.standard;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.missile.parts.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.api.tile.listeners.IDestroyedListener;
import com.builtbroken.mc.api.tile.listeners.IPlacementListener;
import com.builtbroken.mc.codegen.annotations.MultiBlockWrapped;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * 7 block tall silo tube that requires a user to build a casing to contain the missile.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/23/2016.
 */
@TileWrapped(className = "TileWrapperStandardSilo")
@MultiBlockWrapped()
public class TileStandardSilo extends TileAbstractLauncher implements IPlacementListener, IDestroyedListener
{
    public TileStandardSilo()
    {
        super("silo.standard", ICBM.DOMAIN);
    }

    @Override
    public boolean canAcceptMissile(IMissile missile)
    {
        return super.canAcceptMissile(missile) && missile.getMissileSize() == MissileCasings.STANDARD.ordinal();
    }

    @Override
    public Pos getMissileLaunchOffset()
    {
        return new Pos(0, 7, 0);
    }

    //public String getInventoryName()
    //{
    //  return "tile.icbm:standardSilo.container";
    //}

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
}
