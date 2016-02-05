package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

import java.util.Iterator;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/1/2016.
 */
public class BlastTaintBottle extends BlastSimplePath
{
    @Override
    public BlockEdit changeBlock(Location location)
    {
        if (world.rand.nextBoolean() && location.getBiomeGen() != ThaumcraftWorldGenerator.biomeTaint)
        {
            if (world.isBlockNormalCubeDefault(location.xi(), location.yi() - 1, location.zi(), false) && location.isReplaceable())
            {
                return new BlockEdit(location, ConfigBlocks.blockTaintFibres);
            }
        }
        return null;
    }

    @Override
    public void handleBlockPlacement(final IWorldEdit vec)
    {
        if (vec != null && vec.hasChanged() && vec.getNewBlock() == ConfigBlocks.blockTaintFibres)
        {
            Utils.setBiomeAt(world, (int) vec.x(), (int) vec.z(), ThaumcraftWorldGenerator.biomeTaint);
            vec.place();
        }
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        List entities = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(size, size, size));
        if (entities.size() > 0)
        {
            Iterator<EntityLivingBase> entity = entities.iterator();

            while (entity.hasNext())
            {
                Object y = entity.next();
                EntityLivingBase z = (EntityLivingBase) y;
                if (!(z instanceof ITaintedMob) && !z.isEntityUndead())
                {
                    z.addPotionEffect(new PotionEffect(Config.potionTaintPoisonID, 100, 0, false));
                }
            }
        }
    }

    @Override
    public void doEndAudio()
    {
        Thaumcraft.proxy.bottleTaintBreak(world, x, y, z);
    }
}
