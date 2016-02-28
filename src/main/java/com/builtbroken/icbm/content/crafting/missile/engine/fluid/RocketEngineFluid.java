package com.builtbroken.icbm.content.crafting.missile.engine.fluid;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.engine.RocketEngine;
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
    protected FluidTank tank;

    public RocketEngineFluid(ItemStack item, String name, int volume)
    {
        super(item, name);
        tank = new FluidTank(volume);
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

    @Override
    public boolean generatesFire(IMissileEntity missile, IMissile missileModule)
    {
        return tank != null && tank.getFluidAmount() > 0;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("fuelTank"))
        {
            tank.readFromNBT(nbt.getCompoundTag("fuelTank"));
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (tank.getFluidAmount() > 0)
        {
            nbt.setTag("fuelTank", tank.writeToNBT(new NBTTagCompound()));
        }
        return nbt;
    }

    @Override
    public void onPostInit()
    {

    }
}
