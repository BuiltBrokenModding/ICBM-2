package com.builtbroken.icbm.content.missile.parts.warhead;

import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
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
    public int getMaxExplosives()
    {
        return 128;
    }

    @Override
    public int getMissileSize()
    {
        return MissileSize.LARGE.ordinal();
    }

    @Override
    public WarheadLarge clone()
    {
        WarheadLarge warheadLarge = new WarheadLarge(this.getItem());
        copyDataInto(warheadLarge);
        return warheadLarge;
    }
}
