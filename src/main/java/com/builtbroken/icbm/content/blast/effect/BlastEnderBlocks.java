package com.builtbroken.icbm.content.blast.effect;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketSpawnStream;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Blast that teleport any non-TileEntity blocks to a random location on the map. Range of teleportation is based on
 * size of the blast.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/6/2015.
 */
public class BlastEnderBlocks extends BlastSimplePath<BlastEnderBlocks>
{
    public BlastEnderBlocks(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public void getEffectedBlocks(List<IWorldEdit> list)
    {
        super.getEffectedBlocks(list);

        //Sort threw list and find them new homes
        Iterator<IWorldEdit> it = list.iterator();
        List<IWorldEdit> newList = new ArrayList();

        while (it.hasNext())
        {
            IWorldEdit edit = it.next();
            if (Engine.runningAsDev && edit instanceof BlockEdit)
            {
                ((BlockEdit) edit).newBlock = ICBM.blockExplosiveMarker;
            }
            //TODO prevent loading chunks
            //TODO add gravity to blocks
            Location location = getRandomLocationChecked();
            if (location != null)
            {
                //Add place call
                newList.add(new BlockEdit(location).set(edit.getBlock(), edit.getBlockMetadata(), false, true));
            }
        }
        list.addAll(newList);
    }

    /**
     * Gets a random range value based on size of the explosive.
     *
     * @return rand 0 - (size * 5)
     */
    protected int getRandomRange()
    {
        return world.rand.nextInt((int) (size * 20)) - world.rand.nextInt((int) (size * 20));
    }

    /**
     * Gets a random range value based on size of the explosive.
     *
     * @return rand 0 - (size * 5)
     */
    protected Location getRandomLocation()
    {
        return new Location(world, getRandomRange() + center.x(), Math.max(10, Math.min(getRandomRange() + center.y(), 255)), getRandomRange() + center.z());
    }

    /**
     * Gets a random location but checks to avoid overriding non-air blocks
     *
     * @return
     */
    protected Location getRandomLocationChecked()
    {
        //TODO check for replaceable
        //TODO maybe pathfind?
        Location location;
        int i = 0;
        do
        {
            location = getRandomLocation();
        }
        while (i++ < 10 && !location.isAirBlock());
        if (!location.isAirBlock())
        {
            int y = location.yi();
            for (; y < 255; y++)
            {
                if (world.getBlock(location.xi(), y, location.zi()).isAir(world, location.xi(), y, location.zi()))
                {
                    break;
                }
            }
            location = new Location(location.world, location.xi(), y, location.zi());
        }
        return location.isAirBlock() ? location : null;
    }


    @Override
    public BlockEdit changeBlock(Location location)
    {
        if (location.getTileEntity() == null && !location.isAirBlock())
        {
            return new BlockEdit(location).set(Blocks.air, 0, false, true);
        }
        return null;
    }

    @Override
    public boolean shouldPath(Location location)
    {
        if (super.shouldPath(location))
        {
            if (location.getHardness() < 0)
            {
                return false;
            }
            else if (location.getTileEntity() != null)
            {
                return false;
            }
            else if (location.isAirBlock())
            {
                return false;
            }
            //TODO look at other blocks that shouldn't be moved
            //TODO make a teleportation blacklist
            //TODO tap into existing mod's blacklists
            //TODO fire teleportation event
            //TODO break or don't teleport crops
            Block block = location.getBlock();
            if (block == Blocks.portal)
            {
                return false;
            }
            else if (block == Blocks.end_portal)
            {
                return false;
            }
            else if (block == Blocks.end_portal_frame)
            {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void doStartDisplay()
    {
        //Mainly just to disable default effects
    }

    @Override
    public void doEndDisplay()
    {
        //Mainly just to disable default effects
    }

    @Override
    public void displayEffectForEdit(IWorldEdit blocks)
    {
        if (!world.isRemote)
        {
            Engine.instance.packetHandler.sendToAllAround(new PacketSpawnStream(world.provider.dimensionId, x, y, z, blocks.x(), blocks.y(), blocks.z(), 0), new NetworkRegistry.TargetPoint(world.provider.dimensionId, blocks.x(), blocks.y(), blocks.z(), 90));
        }
    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {
        if (!world.isRemote)
        {
            world.playSoundEffect(blocks.x(), blocks.y(), blocks.z(), "mob.endermen.portal", 2.0F, 1.0F);
        }
    }
}
