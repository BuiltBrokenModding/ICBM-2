package com.builtbroken.icbm.content.rail;

import com.builtbroken.icbm.content.rail.entity.EntityCart;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Applied to tiles that can be used as transport rails
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/30/2016.
 */
public interface IMissileRail
{
    /** Called each action that the cart is above the rail */
    void tickRailFromCart(EntityCart cart);

    /**
     * Called to check if the tile is currently
     * a rail that can be used. This will return
     * false normally if the tile is not actually
     * a rail but a supporting tile that uses
     * the same class that a normal rail uses.
     *
     * @return true if the rail can be used
     */
    boolean isUsableRail();

    /**
     * Side the rail is attached to
     *
     * @return
     */
    ForgeDirection getAttachedDirection();

    /**
     * Direction the rail is facing into
     *
     * @return
     */
    ForgeDirection getFacingDirection();

    /**
     * Height of the rail
     *
     * @return
     */
    double getRailHeight();
}
