package com.builtbroken.icbm.content.crafting.missile.engine.fluid;

import com.builtbroken.icbm.content.crafting.missile.engine.Engine;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

/** Fluid only version of the engine
 * Created by robert on 12/28/2014.
 */
public class EngineFluid extends Engine implements IFluidTank
{
    protected FluidTank tank = new FluidTank(1);

    public EngineFluid(ItemStack item, String name)
    {
        super(item, name);
    }

    @Override
    public FluidStack getFluid()
    {
        return tank.getFluid();
    }

    @Override
    public int getFluidAmount()
    {
        return tank.getFluidAmount();
    }

    @Override
    public int getCapacity()
    {
        return tank.getCapacity();
    }

    @Override
    public FluidTankInfo getInfo()
    {
        return tank.getInfo();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return tank.drain(maxDrain, doDrain);
    }
}
