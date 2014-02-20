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

/** Basic entity selector used by sentry guns to find valid targets
 * 
 * @author DarkGuardsman */
public class EntitySelectorSentry implements IEntitySelector
{
    ISentryContainer drone;
    boolean monsters = true, animals = false, npcs = false, players = false, flying = false;

    public EntitySelectorSentry(ISentryContainer drone)
    {
        this.drone = drone;
    }

    @Override
    public boolean isEntityApplicable(Entity entity)
    {
        if (!isFriendly(entity) && isValid(entity))
        {
            if (entity instanceof EntityFlying)
            {
                return flying;
            }
            else if (entity instanceof EntityPlayer)
            {
                return players;
            }
            else if (entity instanceof IMob)
            {
                return monsters;
            }
            else if (entity instanceof IAnimals)
            {
                return animals;
            }
            else if (entity instanceof INpc)
            {
                return npcs;
            }
        }
        return false;
    }

    /** Checks if the sentry finds the entity friendly */
    public boolean isFriendly(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            //TODO call back to access system to check if player is friendly
            //TODO check if the entity is owned by a player
        }
        return false;
    }

    /** Checks if the target is valid for being attacked */
    public boolean isValid(Entity entity)
    {
        if (entity instanceof EntityLivingBase)
        {
            if (entity.isEntityAlive() && !entity.isInvisible())
            {
                if (entity instanceof EntityPlayer)
                {
                    if (((EntityPlayer) entity).capabilities.isCreativeMode)
                    {
                        return false;
                    }
                }
                return true;
            }

        }
        return false;
    }
}
