package com.builtbroken.icbm.content.blast.temp;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.lib.data.heat.HeatedBlockRegistry;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.lib.world.edit.PlacementData;
import com.builtbroken.mc.prefab.entity.damage.DamageSources;
import com.builtbroken.mc.prefab.entity.selector.EntitySelectors;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Blast that removes energy from the environment making it very cold.
 * Created by robert on 2/24/2015.
 */
public class BlastEndoThermic extends BlastSimplePath<BlastEndoThermic>
{
    public BlastEndoThermic(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public BlockEdit changeBlock(BlockPos location)
    {
        Block block = location.getBlock(world);
        //TODO change temp to be based on init energy and heating data of the block
        //TODO change to dump heat into the heat map
        PlacementData data = HeatedBlockRegistry.getResultCoolDown(block, getTempForDistance(location.distance(x, y, z)));
        if (data != null && data.block() != null)
        {
            BlockEdit edit = new BlockEdit(world, location);
            edit.set(data.block(), data.meta() == -1 ? 0 : data.meta(), false, true);
            return edit;
        }
        else if (block == Blocks.water)
        {
            BlockEdit edit = new BlockEdit(world, location);
            edit.set(Blocks.ice, 0, false, true);
            edit.setNotificationLevel(2);
            //TODO turn flowing water into ice slabs matching meta value
            return edit;
        }
        else if (block == Blocks.snow)
        {
            BlockEdit edit = new BlockEdit(world, location);
            edit.set(Blocks.snow_layer, Math.min(location.getBlockMetadata(world) + world.rand.nextInt(3), 15), false, true);
            return edit;
        }
        else if (location.isReplaceable(world))
        {
            Location loc = new Location(world, location).add(0, -1, 0);
            if (!loc.isAirBlock() && loc.isSideSolid(ForgeDirection.UP) && Blocks.snow.canPlaceBlockAt(world, location.xi(), location.yi(), location.zi()))
            {
                BlockEdit edit = new BlockEdit(world, location);
                edit.set(Blocks.snow_layer, world.rand.nextInt(3), false, true);
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
                return EnumFacing.UP != dir;
            }
            //TODO check if the block has thermal properties
            //TODO if yes then check if it allows heat transfer
            //TODO if allows heat transfer we can path
            return true;
        }
        return false;
    }

    private int getTempForDistance(double distance)
    {
        return 0 + (int) (Math.max(10, (293 / size)) * distance);
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (!beforeBlocksPlaced)
        {
            DamageSource source = DamageSources.THERMAL_DECREASE.getSource(this);
            List<Entity> list = EntitySelectors.LIVING_SELECTOR.selector().getEntities(this, size * 2);
            for (Entity entity : list)
            {
                double distance = entity.getDistance(x, y, z);
                int temp = getTempForDistance(distance);
                if (temp <= 250)
                {
                    float damage = Math.max(1, 250 - temp) / 25;
                    entity.attackEntityFrom(source, damage);
                    if (entity.isBurning())
                    {
                        entity.extinguish();
                    }
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

    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {

    }
}
