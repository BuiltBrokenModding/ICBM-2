package com.builtbroken.icbm.content.blast.fire;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.data.Direction;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.entity.damage.DamageSources;
import com.builtbroken.mc.prefab.entity.selector.EntitySelectors;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Blast the set everything on fire in a large area.
 * Created by robert on 2/3/2015.
 */
public class BlastFlashFire extends BlastSimplePath<BlastFlashFire>
{
    public BlastFlashFire(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public BlockEdit changeBlock(BlockPos location)
    {
        if (location.isReplaceable(oldWorld))
        {
            Location loc = new Location(oldWorld, location).add(0, -1, 0);
            if (!loc.isAirBlock() && loc.isSideSolid(ForgeDirection.UP))
            {
                BlockEdit edit = new BlockEdit(oldWorld, location);
                edit.set(Blocks.fire, 0, false, true);
                return edit;
            }
        }
        return null;
    }

    @Override
    public boolean shouldPathTo(BlockPos last, BlockPos next, Direction dir)
    {
        if (super.shouldPathTo(last, next, dir))
        {
            if (last.isReplaceable(oldWorld) && next.isReplaceable(oldWorld))
                return dir != Direction.UP;
            return true;
        }
        return false;
    }

    private int getTempForDistance(double distance)
    {
        return 2000 - (int) ((2000 / size) * distance);
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (!beforeBlocksPlaced)
        {
            DamageSource source = DamageSources.THERMAL_INCREASE.getSource(this);
            List<Entity> list = EntitySelectors.LIVING_SELECTOR.selector().getEntities(this, size * 2);
            for (Entity entity : list)
            {
                double distance = entity.getDistance(x, y, z);
                int temp = getTempForDistance(distance);
                float damage = temp / 1000.0f;
                if (entity.attackEntityFrom(source, damage))
                {
                    entity.setFire((int) damage);
                }
            }
        }
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
        if (!oldWorld.isRemote)
        {
            world.spawnParticle("lava", blocks.x(), blocks.y(), blocks.z(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {
        if (!oldWorld.isRemote)
        {
            oldWorld.playSoundEffect(blocks.x(), blocks.y(), blocks.z(), "liquid.lavapop", 0.2F + oldWorld.rand.nextFloat() * 0.2F, 0.9F + oldWorld.rand.nextFloat() * 0.15F);
        }
    }
}