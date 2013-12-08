package icbm.api.sentry;

import net.minecraft.util.DamageSource;

/** Apply this to an entity if it is meant to be targeted by the AA Turret.
 * 
 * @author Calclavia */
public interface IAATarget
{
    /** destroys the target with a boom. This is a forced way for the sentry too kill the target if
     * it doesn't take damage */
    public void destroyCraft();

    /** Applies damage to the the target
     * 
     * @param damage - damage in half HP
     * @return the amount of HP left. Return -1 if this target can't take damage, and will be chance
     * killed. Return 0 if this target is dead and destroyCraft() will be called. */
    @Deprecated
    public int doDamage(int damage);

    /** Attacks the aa target in the same way as Entity.attackEntityFrom
     * 
     * @param source - Damage source
     * @param damage - actual damage to apply
     * @return true if the damage was applied */
    public boolean attackEntityFrom(DamageSource source, float damage);

    /** Can this be targeted by automated targeting systems or AIs. Used to implement radar jammers,
     * cloaking devices, and other addons for the Entity being targeted
     * 
     * @param entity - entity that is targeting this, can be an Entity, EntityLiving, or TileEntity
     * @return true if it can */
    public boolean canBeTargeted(Object entity);
}
