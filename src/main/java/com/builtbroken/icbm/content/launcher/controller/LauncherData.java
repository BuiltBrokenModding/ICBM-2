package com.builtbroken.icbm.content.launcher.controller;

import com.builtbroken.icbm.content.crafting.missile.MissileModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.lib.transform.vector.Location;
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
    protected Location location;
    protected Missile missile;

    public LauncherData(TileAbstractLauncher launcher)
    {
        location = launcher.toLocation();
        missile = launcher.getMissile();
    }

    //Load
    public LauncherData(NBTTagCompound tag)
    {
        location = new Location(tag);
        ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
        if(stack != null)
        {
            missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
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
