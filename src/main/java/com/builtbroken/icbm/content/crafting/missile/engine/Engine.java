package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModule;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

/**
 * Created by robert on 12/28/2014.
 */
public abstract class Engine extends MissileModule
{
    public Engine(ItemStack item, String name)
    {
        super(item, name);
    }


}
