package com.builtbroken.icbm.content.launcher;

import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.api.ISave;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Used to log information about what happened when the missile launched
 * then impacted.
 * Created by robert on 4/3/2015.
 */
public class LauncherReport implements ISave
{
    //Not saved
    public int entityID;

    //Saved data
    public Missile missile;
    public Long launchTime;
    public Long deathTime;
    public boolean impacted;

    public LauncherReport(EntityMissile missile)
    {
        entityID = missile.getEntityId();
        this.missile = missile.getMissile();
        this.launchTime = System.nanoTime();
    }

    public LauncherReport(NBTTagCompound nbt)
    {
        load(nbt);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("missile"))
            missile = MissileModuleBuilder.INSTANCE.buildMissile(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("missile")));
        if (nbt.hasKey("start"))
            launchTime = nbt.getLong("start");
        if (nbt.hasKey("end"))
            deathTime = nbt.getLong("end");
        
        impacted = nbt.getBoolean("impact");
    }

    public NBTTagCompound save()
    {
        return save(new NBTTagCompound());
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (missile != null)
            nbt.setTag("missile", missile.toStack().writeToNBT(new NBTTagCompound()));
        if (launchTime != 0L)
            nbt.setLong("start", launchTime);
        if (deathTime != 0L)
            nbt.setLong("end", deathTime);
        if (impacted)
            nbt.setBoolean("impact", true);
        return nbt;
    }
}
