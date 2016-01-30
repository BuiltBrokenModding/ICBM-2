package com.builtbroken.icbm.content.crafting.missile.warhead;

import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 12/28/2014.
 */
public class WarheadMedium extends Warhead
{
    public WarheadMedium(ItemStack warhead)
    {
        super(warhead, WarheadCasings.EXPLOSIVE_MEDIUM);
    }

    @Override
    public int getMissileSize()
    {
        return MissileCasings.MEDIUM.ordinal();
    }

    @Override
    public WarheadMedium clone()
    {
        WarheadMedium warhead = new WarheadMedium(this.item);
        copyDataInto(warhead);
        return warhead;
    }
}
