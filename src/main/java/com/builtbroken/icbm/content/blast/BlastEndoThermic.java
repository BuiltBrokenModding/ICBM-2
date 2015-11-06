package com.builtbroken.icbm.content.blast;

import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.lib.world.edit.PlacementData;
import com.builtbroken.mc.lib.world.heat.HeatedBlockRegistry;
import com.builtbroken.mc.prefab.entity.damage.DamageSources;
import com.builtbroken.mc.prefab.entity.selector.EntitySelectors;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Blast that removes energy from the environment making it very cold.
 * Created by robert on 2/24/2015.
 */
public class BlastEndoThermic extends BlastSimplePath
{
    @Override
    public BlockEdit changeBlock(Location location)
    {
        Block block = location.getBlock();
        //TODO change temp to be based on init energy and heating data of the block
        //TODO change to dump heat into the heat map
        PlacementData data = HeatedBlockRegistry.getResultCoolDown(block, getTempForDistance(location.distance(x, y, z)));
        if (data != null && data.block() != null)
        {
            BlockEdit edit = new BlockEdit(location);
            edit.set(data.block(), data.meta() == -1 ? 0 : data.meta(), false, true);
            return edit;
        }
        else if (location.isAirBlock())
        {
            Location loc = location.add(0, -1, 0);
            if (!loc.isAirBlock() && loc.isSideSolid(ForgeDirection.UP))
            {
                BlockEdit edit = new BlockEdit(location);
                edit.set(Blocks.snow_layer, 0, false, true);
                return edit;
            }
        }
        return null;
    }

    @Override
    public boolean shouldPathTo(Location last, Location next)
    {
        if (super.shouldPathTo(last, next))
        {
            if (last.isAirBlock() && next.isAirBlock())
                return last.sub(next).toForgeDirection() != ForgeDirection.UP;
            PlacementData data = HeatedBlockRegistry.getResultWarmUp(next.getBlock(), getTempForDistance(next.distance(x, y, z)));
            PlacementData dataLast = HeatedBlockRegistry.getResultWarmUp(last.getBlock(), getTempForDistance(last.distance(x, y, z)));
            if ((data == null || data.block() == null) && (dataLast == null || dataLast.block() == null))
                return false;
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
                        entity.extinguish();
                }
            }
        }
    }
}
