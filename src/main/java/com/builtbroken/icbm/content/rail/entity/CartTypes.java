package com.builtbroken.icbm.content.rail.entity;

import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;

/**
 * List of cart types that are supported by {@link EntityCart}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/2/2016.
 */
public enum CartTypes
{
    MICRO(1, 1, MissileCasings.SMALL),
    SMALL(1, 3, MissileCasings.SMALL),
    MEDIUM(3, 3, MissileCasings.MEDIUM),
    LARGE(5, 5, MissileCasings.MEDIUM);

    public final float width;
    public final float length;
    /** Size of missiles that are supported */
    public final MissileCasings supportedCasingSize; //TODO change to list

    CartTypes(float width, float length, MissileCasings supportedCasingSize)
    {
        this.width = width;
        this.length = length;
        this.supportedCasingSize = supportedCasingSize;
    }
}
