package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.IVirtualObject;
import com.builtbroken.mc.core.handler.SaveManager;
import com.builtbroken.mc.lib.helper.NBTUtility;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Point;
import com.builtbroken.mc.lib.transform.vector.Pos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by robert on 2/9/2015.
 */
public class MissileTracker implements IVirtualObject
{
    private static HashMap<Integer, MissileTracker> trackers = new HashMap();

    private ArrayList<MissileData> missiles = new ArrayList();
    private final int dim;

    static
    {
        SaveManager.registerClass("MissileTracker", MissileTracker.class);
    }

    private MissileTracker(int dim)
    {
        this.dim = dim;
        SaveManager.register(this);
        File file = getSaveFile();
        if(file != null && file.exists())
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
                trackers.put(dim, new MissileTracker(dim));
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
        Iterator<MissileData> it = missiles.iterator();
        while (it.hasNext())
        {
            MissileData data = it.next();
            data.ticks++;
            if (data.ticks >= data.respawnTicks)
            {
                //TODO add chunk loading
                Location loc = new Location(world, data.target);
                if(loc.isChunkLoaded())
                {
                    //Its unlikely but this may cause threading issues if this is called as a thread
                    spawnMissileOverTarget(data.missile, loc , null);
                    it.remove();
                }
            }
        }
        return true;
    }

    public void add(EntityMissile missile)
    {
        //Cache data to respawn missile
        MissileData data = new MissileData();
        data.missile = missile.getMissile();
        data.target = new Pos(missile.target_pos);

        data.respawnTicks = (int) new Point(missile.posX, missile.posZ).distance(new Point(data.target.x(), data.target.z())) * 2;

        //Kill old missile as we do not need it
        missile.worldObj.removeEntity(missile);

        missiles.add(data);
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
        NBTTagList list = nbt.getTagList("missiles", 10);
        for(int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            MissileData data = new MissileData();
            data.missile = MissileModuleBuilder.INSTANCE.buildMissile(ItemStack.loadItemStackFromNBT(tag));
            data.target = new Pos(tag);
            missiles.add(data);
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        NBTTagList list = new NBTTagList();
        Iterator<MissileData> it = missiles.iterator();
        while (it.hasNext())
        {
            MissileData data = it.next();
            NBTTagCompound tag = new NBTTagCompound();
            data.target.writeNBT(tag);
            data.missile.toStack().writeToNBT(tag);
            list.appendTag(tag);
        }
        nbt.setTag("missiles", list);
        return nbt;
    }

    public static class MissileData
    {
        Missile missile;
        Pos target;
        int ticks;
        int respawnTicks;
    }
}
