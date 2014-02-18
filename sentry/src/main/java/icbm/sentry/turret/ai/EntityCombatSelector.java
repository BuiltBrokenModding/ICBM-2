package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.ISentryContainer;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;

public class EntityCombatSelector implements IEntitySelector
{
    ISentryContainer drone;
    boolean monsters = true, animals = false, npcs = false, players = false, flying = false;

    public EntityCombatSelector(ISentryContainer drone)
    {
        this.drone = drone;
    }

    @Override
    public boolean isEntityApplicable(Entity entity)
    {
        if (entity instanceof EntityLivingBase)
        {
            if (entity.isEntityAlive() && !entity.isInvisible())
            {
                if (entity instanceof EntityFlying)
                {
                    return flying;
                }
                if (players && entity instanceof EntityPlayer && entity.worldObj.difficultySetting > 0)
                {
                    if (!((EntityPlayer) entity).capabilities.isCreativeMode)
                    {
                        return true;
                    }
                }
                if (monsters && entity instanceof IMob)
                {
                    return true;
                }
                if (animals && entity instanceof IAnimals)
                {
                    return true;
                }
                if (npcs && entity instanceof INpc)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
