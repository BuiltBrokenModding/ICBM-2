package com.builtbroken.icbm.content.blast.fire;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.CommonProxy;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Blast that creates a very small area of fire at the target.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/6/2015.
 */
public class BlastFireBomb extends BlastSimplePath<BlastFireBomb>
{
    public BlastFireBomb(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public BlockEdit changeBlock(BlockPos location)
    {
        //TODO spawn random fire particle that can set fire to blocks up to 20 away
        if (location.isAirBlock(world))
        {
            Location loc = new Location(world, location).sub(0, 1, 0);
            if (!loc.isAirBlock() && loc.isSideSolid(ForgeDirection.UP))
            {
                BlockEdit edit = new BlockEdit(world, location);
                edit.set(Blocks.fire, 0, false, true);
                return edit;
            }
        }
        return null;
    }

    @Override
    public boolean shouldPathTo(BlockPos last, BlockPos next, EnumFacing dir)
    {
        if (super.shouldPathTo(last, next, dir))
        {
            if (last.isAirBlock(world) && next.isAirBlock(world))
            {
                return dir != EnumFacing.DOWN.UP;
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
            CommonProxy.proxy.spawnParticle("lava", world, blocks.x(), blocks.y(), blocks.z(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {
        if (!world.isRemote)
        {
            world.playSoundEffect(blocks.x(), blocks.y(), blocks.z(), "liquid.lavapop", 0.2F + world.rand.nextFloat() * 0.2F, 0.9F + world.rand.nextFloat() * 0.15F);
        }
    }
}
