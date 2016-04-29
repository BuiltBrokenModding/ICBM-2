package com.builtbroken.icbm.content.items;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * Extended version of {@link ItemRemoteDetonator} that can target blocks in a line of sight.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class ItemLaserDetonator extends ItemRemoteDetonator
{
    //TODO make a version called presidential football

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        recipes.add(newShapedRecipe(this, "GLP", "BDW", "ECE", 'G', Items.glass_bottle, 'B', Items.blaze_rod, 'L', Blocks.redstone_lamp, 'P', OreNames.PLATE_STEEL, 'W', OreNames.WIRE_COPPER, 'E', Items.repeater, 'C', UniversalRecipe.CIRCUIT_T2.get(), 'D', ICBM.itemRemoteDetonator));
    }
}
