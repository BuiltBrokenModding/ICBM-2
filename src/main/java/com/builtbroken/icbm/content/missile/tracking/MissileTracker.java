package com.builtbroken.icbm.content.missile.tracking;

import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.api.IVirtualObject;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.handler.SaveManager;
import com.builtbroken.mc.lib.helper.NBTUtility;
import com.builtbroken.mc.lib.transform.vector.Location;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by robert on 2/9/2015.
 */
public class MissileTracker implements IVirtualObject
{
    private static HashMap<Integer, MissileTracker> trackers = new HashMap();

    private ArrayList<MissileTrackingData> missiles = new ArrayList();
    private final int dim;
    private final World world;

    static
    {
        SaveManager.registerClass("MissileTracker", MissileTracker.class);
    }

    private MissileTracker(World world)
    {
        this.world = world;
        this.dim = world.provider.dimensionId;
        SaveManager.register(this);
        File file = getSaveFile();
        if (file != null && file.exists())
        {
            load(NBTUtility.loadData(file));
        }
    }

    public static void addToTracker(EntityMissile missile)
    {
        MissileTracker tracker = getTrackerForWorld(missile.worldObj);
        if (tracker != null)
            tracker.add(missile);
    }

    public static MissileTracker getTrackerForWorld(World world)
    {
        if (world != null)
        {
            int dim = world.provider.dimensionId;
            if (!trackers.containsKey(dim))
            {
                trackers.put(dim, new MissileTracker(world));
            }
            return trackers.get(dim);
        }
        return null;
    }

    public static void spawnMissileOverTarget(Missile data, Location location, EntityPlayer player)
    {
        EntityMissile missile = new EntityMissile(location.world);
        missile.setMissile(data);
        missile.setPosition(location.x() + (10 * location.world.rand.nextFloat()), 500 + (100 * location.world.rand.nextFloat()), location.z() + (10 * location.world.rand.nextFloat()));

        //TODO aim missile at target
        //Pos m = new Pos(missile).toEulerAngle(location).toVector();
        missile.setVelocity(0, -1, 0);
        location.world.spawnEntityInWorld(missile);
        missile.setIntoMotion();
    }

    public boolean update(World world)
    {
        Iterator<MissileTrackingData> it = missiles.iterator();
        while (it.hasNext())
        {
            MissileTrackingData data = it.next();
            if (data.isValid())
            {
                data.ticks++;
                if (data.shouldRespawn())
                {
                    //TODO add chunk loading
                    Location loc = new Location(world, data.target);
                    if (loc.isChunkLoaded())
                    {
                        Entity entity = EntityList.createEntityFromNBT(data.m_save, world);
                        if (entity instanceof EntityMissile)
                        {
                            EntityMissile missile = (EntityMissile) entity;
                            missile.setPosition(data.target.x() + (10 * world.rand.nextFloat()), 500 + (100 * world.rand.nextFloat()), data.target.z() + (10 * world.rand.nextFloat()));
                            //TODO change to simulate terminal velocity based on drop height
                            missile.setVelocity(0, -2, 0);
                            missile.setIntoMotion();

                            world.spawnEntityInWorld(missile);

                            if (Engine.runningAsDev)
                                Engine.instance.logger().info("Spawned in missile[" + missile.getUniqueID() + "]  " + data);
                        }
                        else
                        {
                            if (Engine.runningAsDev)
                                Engine.instance.logger().error("Error, entity != missile, loading missile data " + data);
                        }
                        it.remove();
                    }
                }
            }
            else
            {
                if (Engine.runningAsDev)
                    Engine.instance.logger().error("Removed invalid missile data " + data);
                it.remove();
            }
        }
        return true;
    }

    public void add(EntityMissile missile)
    {
        //Cache data to respawn missile
        MissileTrackingData data = new MissileTrackingData(missile);
        List l = new ArrayList();
        l.add(missile);
        world.removeEntity(missile);

        missiles.add(data);

        if (Engine.runningAsDev)
            Engine.instance.logger().info("Missile[" + missile.getUniqueID() + "] added " + data);
    }

    @Override
    public File getSaveFile()
    {
        return new File(NBTUtility.getSaveDirectory(), "/icbm/MissileSaveDim" + dim + ".dat");
    }

    @Override
    public void setSaveFile(File file)
    {

    }

    @Override
    public boolean shouldSaveForWorld(World world)
    {
        return world != null && world.provider.dimensionId == dim;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        NBTTagList list = nbt.getTagList("missileEntities", 10);
        for (int i = 0; i < list.tagCount(); i++)
        {
            missiles.add(new MissileTrackingData(world, list.getCompoundTagAt(i)));
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        NBTTagList list = new NBTTagList();
        Iterator<MissileTrackingData> it = missiles.iterator();
        while (it.hasNext())
        {
            list.appendTag(it.next().save());
        }
        nbt.setTag("missileEntities", list);
        return nbt;
    }


}
