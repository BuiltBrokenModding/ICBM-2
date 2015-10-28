package com.builtbroken.icbm.content.crafting.missile.guidance.clocks;

import com.builtbroken.icbm.api.modules.IMissileModule;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import net.minecraft.item.ItemStack;

/**
 * Clock and gear timers that guide the missile to target
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public class GuidanceGearsStone extends Guidance
{
    public GuidanceGearsStone(ItemStack item)
    {
        super(item, "guidance.gears.stone");
    }

    @Override
    public float getChanceToFail(IMissileModule missile)
    {
        return 0.43f;
    }

    @Override
    public float getFallOffRange(IMissileModule missile)
    {
        return 100f;
    }
}
