package com.builtbroken.icbm.content.blast.power;

import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.lib.energy.UniversalEnergySystem;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.lib.world.edit.BlockEditResult;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

/**
 * Energy pulse wave that disables and damages machines nearby. Can path threw most walls but loses power each object is passes threw.
 * Can damage entities near metal objects, destroy machines, remove power, and disable generators. In order to disable machines it
 * creates a wrapper object that sucks power out of the machine each tick. This way it is unable to continue to output power to other
 * machines around it.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/14/2015.
 */
public class BlastEMP extends BlastSimplePath<BlastEMP>
{
    @Override
    public IWorldEdit changeBlock(Location location)
    {
        //TODO path threw wires
        //TODO disable machines
        //TODO break settings on machines
        //TODO destroy some machines
        TileEntity tile = location.getTileEntity();
        if (tile != null)
        {
            if (UniversalEnergySystem.isHandler(tile, null))
            {
                return new EmpDrainEdit(location);
            }
        }
        return null;
    }

    /** Version of block edit that doesn't remove a block but instead removes it's power */
    public class EmpDrainEdit extends BlockEdit
    {
        public EmpDrainEdit(IWorldPosition vec)
        {
            super(vec);
        }

        @Override
        public boolean hasChanged()
        {
            return true;
        }

        @Override
        public BlockEditResult place()
        {
            //We can not place a block without a world
            if (world != null)
            {
                //Check if the chunk exists and is loaded to prevent loading/creating new chunks
                Chunk chunk = world.getChunkFromBlockCoords(xi(), zi());
                if (chunk != null && chunk.isChunkLoaded)
                {
                    return doPlace();
                }
                return BlockEditResult.CHUNK_UNLOADED;
            }
            return BlockEditResult.NULL_WORLD;
        }

        @Override
        protected BlockEditResult doPlace()
        {
            TileEntity tile = getTileEntity();
            if (tile != null && UniversalEnergySystem.isHandler(tile, null))
            {
                //TODO get accessible sides
                //TODO do not drain all energy, remove pre-determined amount based on EMP size and distance from blast
                UniversalEnergySystem.extractEnergy(tile, Double.MAX_VALUE, true);
                return BlockEditResult.PLACED;
            }
            return BlockEditResult.PREV_BLOCK_CHANGED;
        }
    }
}
