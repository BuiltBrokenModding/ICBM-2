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
public class GuidanceGearsIron extends Guidance implements IPostInit
{
    public GuidanceGearsIron(ItemStack item)
    {
        super(item, "guidance.gears.iron");
    }

    @Override
    public float getChanceToFail(IMissile missile)
    {
        return 0.3f;
    }

    @Override
    public float getFallOffRange(IMissile missile)
    {
        return 80f;
    }

    @Override
    public void onPostInit()
    {
        ItemStack guidance = GuidanceModules.IRON_GEARS.newModuleStack();
        GameRegistry.addRecipe(new ShapedOreRecipe(guidance, "GSG", "PCP", "GSG", 'G', OreNames.GEAR_IRON, 'S', OreNames.ROD_IRON, 'P', OreNames.PLATE_IRON, 'C', Items.clock));
    }
}
