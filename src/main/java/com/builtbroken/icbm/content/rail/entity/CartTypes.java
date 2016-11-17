package com.builtbroken.icbm.content.rail.entity;

import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.missile.InventoryFilterMissile;
import com.builtbroken.mc.prefab.inventory.filters.IInventoryFilter;

/**
 * List of cart types that are supported by {@link EntityCart}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/2/2016.
 */
public enum CartTypes
{
    MICRO(0.7f, 0.7f, MissileCasings.SMALL),
    SMALL(0.7f, 2.4f, MissileCasings.SMALL),
    ThreeByThree(2.4f, 2.4f, MissileCasings.STANDARD);
    //LARGE(4, 4, MissileCasings.STANDARD);

    public final float width;
    public final float length;
    /** Size of missiles that are supported */
    public final MissileCasings supportedCasingSize; //TODO change to list

    public final IInventoryFilter filter;

    CartTypes(float width, float length, MissileCasings supportedCasingSize)
    {
        this.width = width;
        this.length = length;
        this.supportedCasingSize = supportedCasingSize;
        this.filter = new InventoryFilterMissile(supportedCasingSize.ordinal());
    }
}
