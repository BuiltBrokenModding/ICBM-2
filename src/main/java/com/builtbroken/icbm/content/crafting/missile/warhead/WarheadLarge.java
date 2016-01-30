package com.builtbroken.icbm.content.crafting.missile.warhead;

import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 12/28/2014.
 */
public class WarheadLarge extends Warhead
{
    public WarheadLarge(ItemStack warhead)
    {
        super(warhead, WarheadCasings.EXPLOSIVE_LARGE);
    }

    @Override
    public int getMissileSize()
    {
        return MissileCasings.LARGE.ordinal();
    }

    @Override
    public WarheadLarge clone()
    {
        WarheadLarge warheadLarge = new WarheadLarge(this.item);
        copyDataInto(warheadLarge);
        return warheadLarge;
    }
}
