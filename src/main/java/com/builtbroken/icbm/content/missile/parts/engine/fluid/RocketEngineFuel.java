package com.builtbroken.icbm.content.missile.parts.engine.fluid;

import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.missile.parts.engine.Engines;
import com.builtbroken.mc.core.content.parts.CraftingParts;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public class RocketEngineFuel extends RocketEngineFluid
{
    public RocketEngineFuel(ItemStack item)
    {
        super(item, "engine.fuel", FluidContainerRegistry.BUCKET_VOLUME);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (resource != null && resource.getFluid().getName().equalsIgnoreCase("fuel"))
        {
            return super.fill(resource, doFill);
        }
        return 0;
    }

    @Override
    public float getSpeed(IMissile missile)
    {
        return 0.6f;
    }

    @Override
    public float getMaxDistance(IMissile missile)
    {
        if (tank.getFluidAmount() > 0)
        {
            return 3f * tank.getFluidAmount();
        }
        return 0f;
    }

    @Override
    public void initFuel()
    {
        if (FluidRegistry.getFluid("fuel") != null)
        {
            tank.fill(new FluidStack(FluidRegistry.getFluid("fuel"), tank.getCapacity()), true);
        }
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(Engines.FUEL_ENGINE.newModuleStack(), "SCS", "PGP", "SIS", 'S', OreNames.ROD_IRON, 'P', OreNames.PLATE_IRON, 'G', Engines.OIL_ENGINE.newModuleStack(), 'I', Items.flint_and_steel, 'C', CraftingParts.MOTOR.oreName));
    }
}
