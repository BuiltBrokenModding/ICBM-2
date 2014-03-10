package icbm.core;

import icbm.api.IMissile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

/** Utility that handles how damage is applied to entities
 * 
 * @author DarkGuardsman */
public class DamageUtility
{
    /** Can a damage source harm the entity in question.
     * 
     * @param entity - entity being attacked
     * @param source - damage source/type
     * @param damage - amount of damage
     * @return true if the damage can be applied */
    public static boolean canHarm(Entity entity, DamageSource source, float damage)
    {
        if (canDamage(entity))
        {
            if (isMachine(entity))
            {
                if (source.isFireDamage())
                {
                    return false;
                }
                if (source.isMagicDamage())
                {
                    return false;
                }
            }
            return source != null && damage > 0;
        }
        return false;
    }

    /** Can damage be applied at all to the entity
     * 
     * @param entity - entity being attacked
     * @return true if the entity can be damaged */
    public static boolean canDamage(Entity entity)
    {
        if (entity != null)
        {
            if (!entity.isEntityInvulnerable() && entity.isEntityAlive())
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

    /** Checks if the entity is an entity made of metal, or is a machine in nature. */
    public static boolean isMachine(Entity entity)
    {
        if (entity instanceof IMissile)
        {
            return true;
        }
        return false;
    }
}
