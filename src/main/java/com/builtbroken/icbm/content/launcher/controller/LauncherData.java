package com.builtbroken.icbm.content.launcher.controller;

import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.missile.parts.MissileModuleBuilder;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.imp.transform.vector.Pos;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Generic data about the launcher linked to a controller. Used mainly
 * to provide information to the GUI.
 * <p/>
 * Created by robert on 4/17/2015.
 */
public class LauncherData
{
    public Pos location;
    public IMissile missile;

    public LauncherData(TileAbstractLauncher launcher)
    {
        location = launcher.toPos();
        missile = launcher.getMissile();
    }

    //Load
    public LauncherData(NBTTagCompound tag)
    {
        location = new Pos(tag);
        ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
        if(stack != null)
        {
            if (stack.getItem() instanceof IMissileItem)
            {
                missile = ((IMissileItem) stack.getItem()).toMissile(stack);
            }
            else
            {
                missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
            }
        }
    }

    //Save
    public NBTTagCompound toNBT()
    {
        //TODO maybe save inside sub tags
        NBTTagCompound nbt = new NBTTagCompound();
        if (location != null)
            location.writeNBT(nbt);
        if (missile != null)
            missile.toStack().writeToNBT(nbt);
        return nbt;
    }

    public boolean isValid()
    {
        return location != null && location.isAboveBedrock();
    }
}
