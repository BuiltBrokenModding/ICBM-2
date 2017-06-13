package com.builtbroken.icbm.content.missile.parts.warhead;

import com.builtbroken.icbm.content.missile.parts.casing.MissileCasings;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 12/28/2014.
 */
public class WarheadMicro extends Warhead
{
    public WarheadMicro(ItemStack warhead)
    {
        super(warhead, WarheadCasings.EXPLOSIVE_MICRO);
    }

    @Override
    public int getMaxExplosives()
    {
        return 1;
    }

    @Override
    public int getMissileSize()
    {
        return MissileCasings.MICRO.ordinal();
    }

    @Override
    public WarheadMicro clone()
    {
        WarheadMicro warhead = new WarheadMicro(this.getItem());
        copyDataInto(warhead);
        return warhead;
    }
}
