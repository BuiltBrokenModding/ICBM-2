package com.builtbroken.icbm.content.crafting.missile.guidance.chips;

import com.builtbroken.icbm.api.modules.IMissileModule;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public class GuidanceChipTwo extends Guidance
{
    public GuidanceChipTwo(ItemStack item)
    {
        super(item, "guidance.chip.2");
    }

    @Override
    public float getChanceToFail(IMissileModule missile)
    {
        return 0.03f;
    }

    @Override
    public float getFallOffRange(IMissileModule missile)
    {
        return 40f;
    }
}
