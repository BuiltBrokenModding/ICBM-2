package com.builtbroken.icbm.content.missile.parts.warhead;

import com.builtbroken.icbm.content.missile.parts.casing.MissileSize;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 12/28/2014.
 */
public class WarheadStandard extends Warhead
{
    public WarheadStandard(ItemStack warhead)
    {
        super(warhead, WarheadCasings.EXPLOSIVE_STANDARD);
    }

    @Override
    public int getMaxExplosives()
    {
        return 20;
    }

    @Override
    public int getMissileSize()
    {
        return MissileSize.STANDARD.ordinal();
    }

    @Override
    public WarheadStandard clone()
    {
        WarheadStandard warhead = new WarheadStandard(this.getItem());
        copyDataInto(warhead);
        return warhead;
    }
}
