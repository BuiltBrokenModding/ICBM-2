package com.builtbroken.icbm.content.blast.effect;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
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
            List<Entity> players = world.getEntitiesWithinAABB(EntityPlayerMP.class, new Cube(-size, -size - 1, -size, size, size, size).add(x, y, z).cropToWorld().toAABB());
            //Botania devs are made of plants, lol inside joke
            for(Entity entity : players)
            {
                if(entity instanceof EntityPlayerMP)
                {
                    String username = ((EntityPlayerMP)entity).getGameProfile().getName();
                    if(username.equalsIgnoreCase("williewillus") || username.equalsIgnoreCase("Vazkii"))
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
        if (!world.isRemote)
        {
            //Generate random position near block
            double posX = (double) ((float) blocks.x() + world.rand.nextFloat());
            double posY = (double) ((float) blocks.y() + world.rand.nextFloat());
            double posZ = (double) ((float) blocks.z() + world.rand.nextFloat());

            Pos pos = randomMotion(posX, posY, posZ);
            //Spawn particles
            Engine.proxy.spawnParticle("explode", world, (posX + x * 1.0D) / 2.0D, (posY + y * 1.0D) / 2.0D, (posZ + z * 1.0D) / 2.0D, pos.x(), pos.y(), pos.z());
            Engine.proxy.spawnParticle("smoke", world, posX, posY, posZ, pos.x(), pos.y(), pos.z());
        }
    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {

    }
}
