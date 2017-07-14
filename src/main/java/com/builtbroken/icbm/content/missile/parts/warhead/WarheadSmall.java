package com.builtbroken.icbm.content.missile.parts.warhead;

import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 12/28/2014.
 */
public class WarheadSmall extends Warhead
{
    public WarheadSmall(ItemStack warhead)
    {
        super(warhead, WarheadCasings.EXPLOSIVE_SMALL);
    }

    @Override
    public int getMaxExplosives()
    {
        return 5;
    }

    @Override
    public int getMissileSize()
    {
        return MissileSize.SMALL.ordinal();
    }

    @Override
    public WarheadSmall clone()
    {
        WarheadSmall warhead = new WarheadSmall(this.getItem());
        copyDataInto(warhead);
        return warhead;
    }
}
