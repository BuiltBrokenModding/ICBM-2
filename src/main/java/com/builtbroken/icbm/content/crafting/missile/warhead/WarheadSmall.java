package com.builtbroken.icbm.content.crafting.missile.warhead;

import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
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
    public int getMissileSize()
    {
        return MissileCasings.SMALL.ordinal();
    }

    @Override
    public WarheadSmall clone()
    {
        WarheadSmall warhead = new WarheadSmall(this.item);
        copyDataInto(warhead);
        return warhead;
    }
}
