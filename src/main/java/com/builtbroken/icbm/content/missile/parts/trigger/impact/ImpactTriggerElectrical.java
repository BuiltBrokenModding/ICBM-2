package com.builtbroken.icbm.content.missile.parts.trigger.impact;

import com.builtbroken.icbm.content.missile.parts.trigger.Triggers;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/27/2016.
 */
public class ImpactTriggerElectrical extends ImpactTrigger
{
    //TODO add low failure chance, max energy breaking point
    //TODO add scaled energy values - %mass% * constant
    //TODO add pre-programmed hardness to energy value comparators (eg stone is 1K energy or something)
    public ImpactTriggerElectrical(ItemStack item)
    {
        super(item, Triggers.ELECTRICAL_IMPACT);
    }
}
