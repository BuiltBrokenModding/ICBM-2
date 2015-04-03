package com.builtbroken.icbm.content.missile.tracking;

import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.lib.transform.vector.Point;
import com.builtbroken.mc.lib.transform.vector.Pos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class MissileTrackingData
{
    protected NBTTagCompound m_save;
    protected IPos3D target;
    protected Long ticks = 0L;
    protected Long respawnTicks = -1L;

    public MissileTrackingData(EntityMissile missile)
    {
        this.m_save = new NBTTagCompound();
        missile.writeToNBTOptional(this.m_save);
        target = missile.target_pos;
        respawnTicks = (long) new Point(missile.posX, missile.posZ).distance(new Point(missile.target_pos.x(), missile.target_pos.z())) * 2;

    }

    public MissileTrackingData(World world, NBTTagCompound tag)
    {
        if (tag.hasKey("missile"))
        {
            Entity entity = EntityList.createEntityFromNBT(tag.getCompoundTag("missile"), world);
            if (entity instanceof EntityMissile)
            {
                if (((EntityMissile)entity).target_pos != null)
                {
                    target = ((EntityMissile)entity).target_pos;
                    respawnTicks = (long) new Point(((EntityMissile) entity).posX, ((EntityMissile) entity).posZ).distance(new Point(((EntityMissile) entity).target_pos.x(), ((EntityMissile) entity).target_pos.z())) * 2;
                }
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
        if (m_save != null)
        {
            nbt.setTag("missile", m_save);
        }
        return nbt;
    }

    public boolean isValid()
    {
        return m_save != null && respawnTicks > 0;
    }
}