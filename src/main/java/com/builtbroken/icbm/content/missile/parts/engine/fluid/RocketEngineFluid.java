package com.builtbroken.icbm.content.missile.parts.engine.fluid;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.missile.parts.engine.RocketEngine;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

/**
 * Fluid only version of the engine
 * Created by robert on 12/28/2014.
 */
public class RocketEngineFluid extends RocketEngine implements IFluidTank, IPostInit
{
    private FluidTank _tank;
    public final int tank_volume;

    public RocketEngineFluid(ItemStack item, String name, int volume)
    {
        super(item, name);
        this.tank_volume = volume;
    }

    @Override
    public FluidStack getFluid()
    {
        return getTank().getFluid();
    }

    @Override
    public int getFluidAmount()
    {
        return getTank().getFluidAmount();
    }

    @Override
    public int getCapacity()
    {
        return getTank().getCapacity();
    }

    @Override
    public FluidTankInfo getInfo()
    {
        return getTank().getInfo();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        return getTank().fill(resource, doFill);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return getTank().drain(maxDrain, doDrain);
    }

    @Override
    public boolean generatesFire(IMissileEntity missile, IMissile missileModule)
    {
        return getTank().getFluidAmount() > 0;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("fuelTank"))
        {
            getTank().readFromNBT(nbt.getCompoundTag("fuelTank"));
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (getTank().getFluidAmount() > 0)
        {
            nbt.setTag("fuelTank", getTank().writeToNBT(new NBTTagCompound()));
        }
        return nbt;
    }

    /**
     * Gets the primary fluid tank for the engine
     * <p>
     * Will init tank if null
     *
     * @return fluid tank
     */
    public FluidTank getTank()
    {
        if (_tank == null)
        {
            _tank = new FluidTank(tank_volume);
        }
        return _tank;
    }

    @Override
    public void onPostInit()
    {

    }
}
