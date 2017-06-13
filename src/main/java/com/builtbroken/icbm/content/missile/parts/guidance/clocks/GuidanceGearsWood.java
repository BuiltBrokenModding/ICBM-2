package com.builtbroken.icbm.content.missile.parts.guidance.clocks;

import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.missile.parts.guidance.Guidance;
import com.builtbroken.icbm.content.missile.parts.guidance.GuidanceModules;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
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
public class GuidanceGearsWood extends Guidance implements IPostInit
{
    public GuidanceGearsWood(ItemStack item)
    {
        super(item, "guidance.gears.wood");
    }

    @Override
    public float getFallOffRange(IMissile missile)
    {
        return 100f;
    }

    @Override
    public void onPostInit()
    {
        ItemStack guidance = GuidanceModules.WOOD_GEARS.newModuleStack();
        GameRegistry.addRecipe(new ShapedOreRecipe(guidance, "GSG", "PCP", "GSG", 'G', OreNames.GEAR_WOOD, 'S', OreNames.WOOD_STICK, 'P', OreNames.WOOD, 'C', Items.clock));
    }
}
