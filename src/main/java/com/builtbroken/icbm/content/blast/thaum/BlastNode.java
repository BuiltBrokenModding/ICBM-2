package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.edit.IWorldChangeAction;
import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.lib.transform.vector.Location;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Spawns a node at the location, requires a node jar to craft.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2015.
 */
public class BlastNode implements IWorldChangeAction, IWorldPosition
{
    protected final Location center;
    protected final NBTTagCompound tag;

    public BlastNode(Location center, NBTTagCompound tag)
    {
        this.center = center;
        this.tag = tag;
    }

    @Override
    public int shouldThreadAction()
    {
        return -1;
    }

    @Override
    public List<IWorldEdit> getEffectedBlocks()
    {
        List<IWorldEdit> list = new ArrayList();
        if(center.isAirBlock())
        {

        }
        else
        {

        }
        return list;
    }

    @Override
    public void handleBlockPlacement(IWorldEdit blocks)
    {
        //TODO if fails to place drop items
        blocks.place();
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {

    }

    @Override
    public World world()
    {
        return center.world();
    }

    @Override
    public double x()
    {
        return center.x();
    }

    @Override
    public double y()
    {
        return center.y();
    }

    @Override
    public double z()
    {
        return center.z();
    }
}
