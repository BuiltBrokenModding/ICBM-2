package com.builtbroken.icbm.content.missile.tracking;

import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.lib.helper.NBTUtility;
import com.builtbroken.mc.lib.transform.vector.Point;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Object used to track missiles that are outside of the loaded world.
 *
 * @author DarkCow(Robert, DarkGuardsman)
 */
public class MissileTrackingData
{
    protected NBTTagCompound m_save;
    protected Missile missile;
    protected IPos3D target;
    protected Long ticks = 0L;
    protected Long respawnTicks = -1L;

    /**
     * Constructor for turning a missile into tracking data
     *
     * @param missile - entity that is a missile
     */
    public MissileTrackingData(EntityMissile missile)
    {
        this.m_save = new NBTTagCompound();
        missile.writeToNBTOptional(this.m_save);
        target = missile.target_pos;
        respawnTicks = (long) new Point(missile.posX, missile.posZ).distance(new Point(missile.target_pos.x(), missile.target_pos.z()));
        this.missile = missile.getMissile();
        if (missile.getMissile() != null && missile.getMissile().getEngine() != null)
        {
            respawnTicks = (long) ((respawnTicks / missile.getMissile().getEngine().getSpeed(missile.getMissile())) + 1);
        }
    }

    /**
     * Constructor for loading missile data from saves. If the save data
     * doesn't contain a missile it will not load correctly. However, it
     * will also not crash or error.
     *
     * @param world - world to use
     * @param tag   - save data
     */
    public MissileTrackingData(World world, NBTTagCompound tag)
    {
        if (tag.hasKey("missile"))
        {
            Entity entity = EntityList.createEntityFromNBT(tag.getCompoundTag("missile"), world);
            if (entity instanceof EntityMissile)
            {
                if (((EntityMissile) entity).target_pos != null)
                {
                    target = ((EntityMissile) entity).target_pos;
                    respawnTicks = (long) new Point(((EntityMissile) entity).posX, ((EntityMissile) entity).posZ).distance(new Point(((EntityMissile) entity).target_pos.x(), ((EntityMissile) entity).target_pos.z()));
                }
            }
        }
    }

    /**
     * Checks if the missile should respawn into the world
     *
     * @return true if it should respawn
     */
    public boolean shouldRespawn()
    {
        return ticks >= respawnTicks;
    }

    /**
     * Saves the missile data to NBT
     *
     * @return new NBT with the save data, should never be null
     */
    public NBTTagCompound save()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        if (m_save != null)
        {
            nbt.setTag("missile", m_save);
        }
        return nbt;
    }

    /**
     * Is the tracking data valid. Normally this checks if there is a missile save and the respawn ticks is greater than zero.
     *
     * @return true if the data is valid
     */

    public boolean isValid()
    {
        return m_save != null && respawnTicks > 0;
    }

    @Override
    public String toString()
    {
        return "MissileTrackingData[" + target + ", " + missile + "]";
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof MissileTrackingData)
        {
            //Checks if the targets equal each other
            if (((MissileTrackingData) object).target == null && target != null || ((MissileTrackingData) object).target != null && target == null)
                return false;

            if (!((MissileTrackingData) object).target.equals(target))
                return false;

            return NBTUtility.doTagsMatch(((MissileTrackingData) object).m_save, m_save);
        }
        return false;
    }
}