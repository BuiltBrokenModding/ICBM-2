package com.builtbroken.icbm.content.launcher;

import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.api.ISave;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

/**
 * Used to log information about what happened when the missile launched
 * then impacted.
 * Created by robert on 4/3/2015.
 */
public class LauncherReport implements ISave
{
    //Not saved
    public UUID entityID;

    //Saved data
    public Missile missile;
    public long launchTime = 0L;
    public long deathTime = 0L;
    public boolean impacted;

    public LauncherReport(EntityMissile missile)
    {
        entityID = missile.getUniqueID();
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

        if (nbt.hasKey("UUIDMost", 4) && nbt.hasKey("UUIDLeast", 4))
        {
            this.entityID = new UUID(nbt.getLong("UUIDMost"), nbt.getLong("UUIDLeast"));
        }
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

        if(entityID != null)
        {
            nbt.setLong("UUIDMost", entityID.getMostSignificantBits());
            nbt.setLong("UUIDLeast", entityID.getLeastSignificantBits());
        }
        return nbt;
    }
}
