package com.builtbroken.icbm.content.missile.parts.warhead;

import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
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
    public int getMaxExplosives()
    {
        return 64;
    }

    @Override
    public int getMissileSize()
    {
        return MissileSize.MEDIUM.ordinal();
    }

    @Override
    public WarheadMedium clone()
    {
        WarheadMedium warhead = new WarheadMedium(this.getItem());
        copyDataInto(warhead);
        return warhead;
    }
}
