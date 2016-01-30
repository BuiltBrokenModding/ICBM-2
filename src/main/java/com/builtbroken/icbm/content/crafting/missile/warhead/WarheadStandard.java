package com.builtbroken.icbm.content.crafting.missile.warhead;

import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
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
    public int getMissileSize()
    {
        return MissileCasings.STANDARD.ordinal();
    }

    @Override
    public WarheadStandard clone()
    {
        WarheadStandard warhead = new WarheadStandard(this.item);
        copyDataInto(warhead);
        return warhead;
    }
}
