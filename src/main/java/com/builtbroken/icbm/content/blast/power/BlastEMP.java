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
        float distance = (float) location.distance(center);
        if (tile != null)
        {
            if (UniversalEnergySystem.isHandler(tile, null))
            {
                return new EmpDrainEdit(location, (distance < size * .80 ? 1f : distance / (float) size));
            }
        }
        return null;
    }

    @Override
    public void doStartDisplay()
    {
        //TODO emp wave, light and hard to see as second wave will be the main wave
    }

    @Override
    public void doEndDisplay()
    {
        //TODO second emp wave, but faster
    }

    @Override
    public void doStartAudio()
    {
        if (!world.isRemote)
        {
            world.playSoundEffect(x, y, z, "icbm:icbm.taser", 0.2F + world.rand.nextFloat() * 0.2F, 0.9F + world.rand.nextFloat() * 0.15F);
        }
    }

    @Override
    public void doEndAudio()
    {
        if (!world.isRemote)
        {
            world.playSoundEffect(x, y, z, "icbm:icbm.emp", 0.2F + world.rand.nextFloat() * 0.2F, 0.9F + world.rand.nextFloat() * 0.15F);
        }
    }

    @Override
    public void displayEffectForEdit(IWorldEdit blocks)
    {
        if (!world.isRemote)
        {
            //TODO render sparks around machine that was drained
            //TODO render parts flying around machine that was destroyed
        }
    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {
        if (!world.isRemote)
        {
            world.playSoundEffect(blocks.x(), blocks.y(), blocks.z(), "icbm:icbm.spark", 0.2F + world.rand.nextFloat() * 0.2F, 0.9F + world.rand.nextFloat() * 0.15F);
        }
    }

    /** Version of block edit that doesn't remove a block but instead removes it's power */
    public class EmpDrainEdit extends BlockEdit
    {
        final float percentToDrain;

        public EmpDrainEdit(IWorldPosition vec, float percentToDrain)
        {
            super(vec);
            this.percentToDrain = percentToDrain;
        }

        @Override
        public boolean hasChanged()
        {
            //return true as we have no way of really checking
            // if power levels changed between first and final, effectively...
            return true;
        }

        @Override
        public BlockEditResult place()
        {
            //TODO destroy machines, replace machine with burnt up machine containing 60% of crafting parts
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
                //TODO get accessible sides to improve compatibility and performance
                //TODO make a simple drain method
                //TODO fire events
                //TODO implement EMP api to allow machines to control, and config overrides to bypass control
                final double power = UniversalEnergySystem.extractEnergy(tile, UniversalEnergySystem.getPotentialEnergy(tile), false);
                final double powerScaled = power * percentToDrain;
                UniversalEnergySystem.extractEnergy(tile, powerScaled, true);
                return BlockEditResult.PLACED;
            }
            return BlockEditResult.PREV_BLOCK_CHANGED;
        }
    }
}
