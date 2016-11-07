package com.builtbroken.icbm.content.launcher;

import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
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
    public UUID entityUUID;

    //Saved data
    public IMissile missile;
    public boolean impacted;

    //TODO add method to get flight time to string(hhmmss), and convert both times to readable values(hhmmss)
    public long launchTime = 0L;
    public long deathTime = 0L;

    public LauncherReport(EntityMissile missile)
    {
        entityUUID = missile.getUniqueID();
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
        {
            ItemStack missileStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("missile"));
            if(missileStack != null)
            {
                if (missileStack.getItem() instanceof IMissileItem)
                {
                    missile = ((IMissileItem) missileStack.getItem()).toMissile(missileStack);
                }
                else
                {
                    missile = MissileModuleBuilder.INSTANCE.buildMissile(missileStack);
                }
            }
        }
        if (nbt.hasKey("start"))
        {
            launchTime = nbt.getLong("start");
        }
        if (nbt.hasKey("end"))
        {
            deathTime = nbt.getLong("end");
        }

        impacted = nbt.getBoolean("impact");

        if (nbt.hasKey("UUID"))
        {
            this.entityUUID = UUID.fromString(nbt.getString("UUID"));
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
        {
            nbt.setTag("missile", missile.toStack().writeToNBT(new NBTTagCompound()));
        }
        if (launchTime != 0L)
        {
            nbt.setLong("start", launchTime);
        }
        if (deathTime != 0L)
        {
            nbt.setLong("end", deathTime);
        }
        if (impacted)
        {
            nbt.setBoolean("impact", true);
        }

        if (entityUUID != null)
        {
            nbt.setString("UUID", entityUUID.toString());
        }
        return nbt;
    }
}
