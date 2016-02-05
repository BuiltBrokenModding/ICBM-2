package com.builtbroken.icbm.content.crafting.missile.guidance.chips;

import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.guidance.GuidanceModules;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public class GuidanceChipTwo extends Guidance implements IPostInit
{
    public GuidanceChipTwo(ItemStack item)
    {
        super(item, "guidance.chip.2");
    }

    @Override
    public float getChanceToFail(IMissile missile)
    {
        return 0.03f;
    }

    @Override
    public float getFallOffRange(IMissile missile)
    {
        return 40f;
    }

    @Override
    public void onPostInit()
    {
        ItemStack guidance = GuidanceModules.CHIP_TWO.newModuleStack();
        GameRegistry.addRecipe(new ShapedOreRecipe(guidance, "CrC", "wtw", "CrC", 't', "circuitAdvanced", 'r', Items.redstone, 'w', "wireCopper", 'C', "gearIron"));
    }
}
