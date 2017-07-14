package com.builtbroken.icbm.content.missile.tracking;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.missile.data.missile.Missile;
import com.builtbroken.icbm.content.missile.entity.EntityMissile;
import com.builtbroken.mc.api.IVirtualObject;
import com.builtbroken.mc.core.handler.SaveManager;
import com.builtbroken.mc.lib.helper.NBTUtility;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
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
import java.util.Random;

/**
 * Tick handler that tracks the missiles as they fly outside of the loaded map
 * Created by robert on 2/9/2015.
 */
public class MissileTracker implements IVirtualObject
{
    public static int MAX_SPAWN_OUT_Y = 400;
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
        debug("Adding missile " + missile);
        MissileTracker tracker = getTrackerForWorld(missile.worldObj);
        if (tracker != null)
        {
            tracker.add(missile);
        }
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
        debug((location.world.isRemote ? "Client" : "Server") + "spawning missile over target area. D:" + data + "  L:" + location + "  P:" + player);
        spawnMissileOverTarget(data, location);
    }

    public static void spawnMissileOverTarget(Missile missileModuleData, Location location)
    {
        EntityMissile missile = new EntityMissile(location.world);
        missile.setMissile(missileModuleData);
        spawnMissileOverTarget(missile, location);
    }

    public static void spawnMissileOverTarget(EntityMissile missile, Location location)
    {
        Random rand = location.world.rand;

        //Generate spawn in point
        Pos pos = location.toPos();
        pos = pos.sub(0, 10 + (100 * rand.nextFloat()), 0);
        float accuracy = 100f;
        if (missile.getMissile() != null && missile.getMissile().getGuidance() != null)
        {
            accuracy = missile.getMissile().getGuidance().getFallOffRange(missile.getMissile());
        }
        //Randomize by accuracy pattern
        pos = pos.add(accuracy * rand.nextFloat() - accuracy * rand.nextFloat(), accuracy * rand.nextFloat() - accuracy * rand.nextFloat(), accuracy * rand.nextFloat() - accuracy * rand.nextFloat());
        //Set new postion
        missile.setPosition(pos.x(), pos.y(), pos.z());

        //TODO aim missile at target
        //Pos m = new Pos(missile).toEulerAngle(location).toVector();
        missile.motionY = -1;
        location.world.spawnEntityInWorld(missile);
        //TODO add chance for guidance to update aim and fire thrusters
        missile.setIntoMotion();

        debug("Spawned in missile[" + missile.getUniqueID() + "]  over target " + location + " with data" + missile.getMissile());
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
                            spawnMissileOverTarget((EntityMissile) entity, new Location(loc.world, loc.x(), MAX_SPAWN_OUT_Y, loc.z()));
                        }
                        else
                        {
                            debug("Error, entity != missile, loading missile data " + data);
                        }
                        it.remove();
                    }
                }
            }
            else
            {
                debug("Removed invalid missile data " + data);
                it.remove();
            }
        }
        return true;
    }

    public void add(EntityMissile missile)
    {
        //Prevents the missile from reporting when being forced removed from the world
        missile.noReport = true;
        //Cache missile for respawn with matching data
        MissileTrackingData data = new MissileTrackingData(missile);
        world.removeEntity(missile);
        missiles.add(data);

        debug("Missile[" + missile.getUniqueID() + "] added " + data);
    }

    @Override
    public File getSaveFile()
    {
        return new File(NBTUtility.getSaveDirectory(), "/bbm/icbm/MissileSaveDim" + dim + ".dat");
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

    protected static void debug(String msg)
    {
        if (ICBM.DEBUG_MISSILE_MANAGER)
        {
            ICBM.INSTANCE.logger().info("[MissileTracker]" + msg);
        }
    }
}
