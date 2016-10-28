package com.builtbroken.icbm.content.crafting.missile.trigger.impact;

import com.builtbroken.icbm.content.crafting.missile.trigger.Triggers;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/27/2016.
 */
public class ImpactTriggerMechanical extends ImpactTrigger
{
    //TODO high failure chance, minimal energy value needed to trigger
    public ImpactTriggerMechanical(ItemStack item)
    {
        super(item, Triggers.MECHANICAL_IMPACT);
    }
}
