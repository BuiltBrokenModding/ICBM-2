package com.builtbroken.icbm.content.blast.potion;

import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/18/2017.
 */
public class BlastRadiation extends Blast<BlastRadiation>
{
    public BlastRadiation(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public int shouldThreadAction()
    {
        return -1;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        super.doEffectOther(beforeBlocksPlaced);
        if (beforeBlocksPlaced)
        {
            final Pos center = toPos();
            List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x - size, 0, z - size, x + size, 255, z + size));
            for (Entity entity : entityList)
            {
                if (entity instanceof EntityLivingBase)
                {
                    final Pos pos = new Pos(entity);
                    double distance = pos.distance(center);
                    if (distance < size)
                    {
                        Pos head = pos.add(0, entity.getEyeHeight(), 0);
                        MovingObjectPosition hit = head.rayTrace(world, center);
                        if (hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
                        {
                            if (distance < size * 0.1)
                            {
                                //TODO lethal damage
                                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.weakness.getId(), 2000, 2));
                                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.wither.getId(), 2000, 2));
                            }
                            else if (distance < size * 0.5)
                            {
                                //TODO heavy damage
                                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.weakness.getId(), 800, 2));
                                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.wither.getId(), 800, 1));
                            }
                            else
                            {
                                //light damage
                                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.weakness.getId(), 200, 1));
                            }
                        }
                    }
                }
            }
        }
    }
}
