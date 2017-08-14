package com.builtbroken.icbm.content.blast.potion;

import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.framework.explosive.blast.Blast;
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
public class BlastFlash extends Blast<BlastFlash>
{
    public BlastFlash(IExplosiveHandler handler)
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
            List<Entity> entityList = oldWorld.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x - size, 0, z - size, x + size, 255, z + size));
            for (Entity entity : entityList)
            {
                if (entity instanceof EntityLivingBase)
                {
                    final Pos pos = new Pos(entity);
                    EulerAngle angleToCenter = pos.toEulerAngle(center);
                    float yaw = ((EntityLivingBase) entity).rotationYawHead;
                    if (angleToCenter.isYawWithin(yaw, 50))
                    {
                        double distance = pos.distance(center);
                        if (distance < size)
                        {
                            Pos head = pos.add(0, entity.getEyeHeight(), 0);
                            MovingObjectPosition hit = head.rayTrace(oldWorld, center);
                            if (hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
                            {
                                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.blindness.getId(), 200, 2));
                            }
                        }
                    }
                }
            }
        }
    }
}
