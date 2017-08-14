package com.builtbroken.icbm.content.blast.effect;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.IPlantable;

import java.util.List;

/**
 * Blast that kills all plant life
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/11/2015.
 */
public class BlastAntiPlant extends BlastSimplePath<BlastAntiPlant>
{
    public BlastAntiPlant(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public BlockEdit changeBlock(BlockPos location)
    {
        Block block = location.getBlock(oldWorld);
        //int meta = location.getBlockMetadata();
        Material material = block.getMaterial();
        if (!location.isAirBlock(oldWorld) && location.getHardness(oldWorld) >= 0)
        {
            if (block == Blocks.grass)
            {
                return new BlockEdit(oldWorld, location).set(Blocks.dirt, 1, false, true);
            }
            else if (block == Blocks.dirt)
            {
                return new BlockEdit(oldWorld, location).set(Blocks.dirt, 1, false, true);
            }
            else if (block == Blocks.tallgrass)
            {
                return new BlockEdit(oldWorld, location).set(Blocks.air, 0, false, true);
            }
            else if (block == Blocks.mossy_cobblestone)
            {
                return new BlockEdit(oldWorld, location).set(Blocks.cobblestone, 0, false, true);
            }
            else if (material == Material.leaves
                    || material == Material.plants
                    || material == Material.vine
                    || material == Material.cactus
                    || material == Material.gourd)
            {
                return new BlockEdit(oldWorld, location).set(Blocks.air, 0, false, true);
            }
            else if (block instanceof BlockHugeMushroom || block instanceof BlockMycelium || block instanceof BlockHay || block instanceof BlockLog)
            {
                return new BlockEdit(oldWorld, location).set(Blocks.dirt, 1, false, true);
            }
            else if (block instanceof IPlantable || block instanceof IGrowable)
            {
                return new BlockEdit(oldWorld, location).set(Blocks.air, 1, false, true);
            }
        }
        return null;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (beforeBlocksPlaced)
        {
            Pos center = new Pos(x, y, z);
            //TODO add more plant based mob entries
            List<Entity> ents = oldWorld.getEntitiesWithinAABB(EntityCreeper.class, new Cube(-size, -size - 1, -size, size, size, size).add(x, y, z).cropToWorld().toAABB());
            for (Entity entity : ents)
            {
                if (center.distance(entity) <= size)
                {
                    entity.setDead();
                }
            }
            List<Entity> players = oldWorld.getEntitiesWithinAABB(EntityPlayerMP.class, new Cube(-size, -size - 1, -size, size, size, size).add(x, y, z).cropToWorld().toAABB());
            //Botania devs are made of plants, lol inside joke
            for (Entity entity : players)
            {
                if (entity instanceof EntityPlayerMP)
                {
                    String username = ((EntityPlayerMP) entity).getGameProfile().getName();
                    if (username.equalsIgnoreCase("williewillus") || username.equalsIgnoreCase("Vazkii"))
                    {
                        entity.attackEntityFrom(DamageSource.magic, 15);
                    }
                }
            }
        }
    }

    @Override
    public void displayEffectForEdit(IWorldEdit blocks)
    {
        if (!oldWorld.isRemote)
        {
            //Generate random position near block
            double posX = (double) ((float) blocks.x() + oldWorld.rand.nextFloat());
            double posY = (double) ((float) blocks.y() + oldWorld.rand.nextFloat());
            double posZ = (double) ((float) blocks.z() + oldWorld.rand.nextFloat());

            Pos pos = randomMotion(posX, posY, posZ);
            //Spawn particles
            world.spawnParticle("explode", (posX + x * 1.0D) / 2.0D, (posY + y * 1.0D) / 2.0D, (posZ + z * 1.0D) / 2.0D, pos.x(), pos.y(), pos.z());
            world.spawnParticle("smoke", posX, posY, posZ, pos.x(), pos.y(), pos.z());
        }
    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {

    }
}
