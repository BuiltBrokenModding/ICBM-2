package com.builtbroken.icbm.content.blast;

import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.lib.world.edit.PlacementData;
import com.builtbroken.mc.lib.world.heat.BlockConversionData;
import com.builtbroken.mc.lib.world.heat.HeatedBlockRegistry;
import com.builtbroken.mc.prefab.entity.selector.EntitySelectors;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

import java.util.List;

/**
 * Created by robert on 2/24/2015.
 */
public class BlastExoThermic extends BlastSimplePath
{
    public static DamageSource thermalDamage = new DamageSource("thermal").setFireDamage();
    @Override
    public BlockEdit changeBlock(Location location)
    {
        Block block = location.getBlock();
        //TODO change temp to be based on init energy and heating data of the block
        //TODO change to dump heat into the heat map
        PlacementData data = HeatedBlockRegistry.getResultWarmUp(block, getTempForDistance(location.distance(x, y, z)));
        if(data != null && data.block() != null)
        {
            BlockEdit edit = new BlockEdit(location);
            edit.set(data.block(), data.meta() == -1 ? 0 : data.meta(), false, true);
            return edit;
        }
        return null;
    }

    private int getTempForDistance(double distance)
    {
        return 20000 - (int)((20000 / size) * distance);
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        List<Entity> list = EntitySelectors.LIVING_SELECTOR.selector().getEntities(this, size * 2);
        for(Entity entity : list)
        {
            double distance = entity.getDistance(x, y, z);
            int temp = getTempForDistance(distance);
            float damage = temp / 1000.0f;
            if(entity.attackEntityFrom(DamageSource.inFire, damage))
            {
                entity.setFire((int)damage);
            }
        }
    }
}
