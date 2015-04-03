package com.builtbroken.icbm.content.missile.tracking;

import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.lib.transform.vector.Point;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class MissileTrackingData
{
    protected EntityMissile missile;
    protected Long ticks = 0L;
    protected Long respawnTicks = -1L;

    public MissileTrackingData(EntityMissile missile)
    {
        this.missile = missile;
        respawnTicks = (long) new Point(missile.posX, missile.posZ).distance(new Point(missile.target_pos.x(), missile.target_pos.z())) * 2;

    }

    public MissileTrackingData(World world, NBTTagCompound tag)
    {
        if (tag.hasKey("missile"))
        {
            Entity entity = EntityList.createEntityFromNBT(tag.getCompoundTag("missile"), world);
            if (entity instanceof EntityMissile)
            {
                missile = (EntityMissile) entity;
                if (missile.target_pos != null)
                    respawnTicks = (long) new Point(missile.posX, missile.posZ).distance(new Point(missile.target_pos.x(), missile.target_pos.z())) * 2;
            }
        }
    }

    public boolean shouldRespawn()
    {
        return ticks >= respawnTicks;
    }

    public NBTTagCompound save()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        if (missile != null)
        {
            NBTTagCompound m_save = new NBTTagCompound();
            missile.writeToNBTOptional(m_save);
            nbt.setTag("missile", m_save);
        }
        return nbt;
    }

    public boolean isValid()
    {
        return missile != null && respawnTicks > 0;
    }
}