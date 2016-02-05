package com.builtbroken.icbm.content.blast.effect;

import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.IPlantable;

import java.util.List;

/**
 * Blast that kills all plant life
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/11/2015.
 */
public class BlastAntiPlant extends BlastSimplePath
{
    @Override
    public BlockEdit changeBlock(Location location)
    {
        Block block = location.getBlock();
        //int meta = location.getBlockMetadata();
        Material material = block.getMaterial();
        if (!location.isAirBlock() && location.getHardness() >= 0)
        {
            if (block == Blocks.grass)
            {
                return new BlockEdit(location).set(Blocks.dirt, 1, false, true);
            }
            else if (block == Blocks.dirt)
            {
                return new BlockEdit(location).set(Blocks.dirt, 1, false, true);
            }
            else if (block == Blocks.tallgrass)
            {
                return new BlockEdit(location).set(Blocks.air, 0, false, true);
            }
            else if (block == Blocks.mossy_cobblestone)
            {
                return new BlockEdit(location).set(Blocks.cobblestone, 0, false, true);
            }
            else if (material == Material.leaves
                    || material == Material.plants
                    || material == Material.vine
                    || material == Material.cactus
                    || material == Material.gourd)
            {
                return new BlockEdit(location).set(Blocks.air, 0, false, true);
            }
            else if (block instanceof BlockHugeMushroom || block instanceof BlockMycelium || block instanceof BlockHay || block instanceof BlockLog)
            {
                return new BlockEdit(location).set(Blocks.dirt, 1, false, true);
            }
            else if (block instanceof IPlantable || block instanceof IGrowable)
            {
                return new BlockEdit(location).set(Blocks.air, 1, false, true);
            }
        }
        return null;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (beforeBlocksPlaced)
        {
            //TODO add more plant based mob entries
            List<Entity> ents = world.getEntitiesWithinAABB(EntityCreeper.class, new Cube(-size, -size - 1, -size, size, size, size).add(x, y, z).cropToWorld().toAABB());
            for (Entity entity : ents)
            {
                if (center.distance(entity) <= size)
                {
                    entity.setDead();
                }
            }
        }
    }
}
