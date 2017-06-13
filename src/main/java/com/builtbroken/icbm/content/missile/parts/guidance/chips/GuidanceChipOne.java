package com.builtbroken.icbm.content.missile.parts.guidance.chips;

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
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public class GuidanceChipOne extends Guidance implements IPostInit
{
    public GuidanceChipOne(ItemStack item)
    {
        super(item, "guidance.chip.1");
    }

    @Override
    public float getChanceToFail(IMissile missile)
    {
        return 0.05f;
    }

    @Override
    public float getFallOffRange(IMissile missile)
    {
        return 50f;
    }

    @Override
    public void onPostInit()
    {
        ItemStack guidance = GuidanceModules.CHIP_ONE.newModuleStack();
        GameRegistry.addRecipe(new ShapedOreRecipe(guidance, "CrC", "wtw", "CrC", 't', "circuitBasic", 'r', Items.redstone, 'w', OreNames.WIRE_TIN, 'C', OreNames.GEAR_IRON));
    }
}
