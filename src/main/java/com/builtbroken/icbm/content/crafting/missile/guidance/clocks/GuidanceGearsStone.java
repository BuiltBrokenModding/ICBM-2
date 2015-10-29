package com.builtbroken.icbm.content.crafting.missile.guidance.clocks;

import com.builtbroken.icbm.api.modules.IMissileModule;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.guidance.GuidanceModules;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Clock and gear timers that guide the missile to target
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public class GuidanceGearsStone extends Guidance implements IPostInit
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

    @Override
    public void onPostInit()
    {
        ItemStack guidance = GuidanceModules.WOOD_GEARS.newModuleStack();
        GameRegistry.addRecipe(new ShapedOreRecipe(guidance, "GSG", "PCP", "GSG", 'G', "stoneGear", 'S', "rodStone", 'P', "stone", 'C', Items.clock));
    }
}
