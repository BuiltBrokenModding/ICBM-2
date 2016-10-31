package com.builtbroken.icbm.content.rail;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/30/2016.
 */
public interface IMissileRail
{
    /** Called each action that the cart is above the rail */
    void tickRailFromCart(EntityCart cart);

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
    double railHeight();
}
