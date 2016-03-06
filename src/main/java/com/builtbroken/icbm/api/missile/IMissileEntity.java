package com.builtbroken.icbm.api.missile;

import com.builtbroken.jlib.data.vector.IPos3D;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

/**
 * Applied to entities that are missiles
 * Created by robert on 11/19/2014.
 */
public interface IMissileEntity
{
    /** Tells the missile to start moving */
    void setIntoMotion();

    /**
     * Sets the target of the missile to an exact location.
     *
     * @param location - target location
     * @param ark      - should the missile ark towards the target. Not
     *                 all missiles support this option.
     */
    void setTarget(IPos3D location, boolean ark);

    /**
     * Sets the target of the missile to an entity.
     *
     * @param entity        - target, should be valid(not dead, in world)
     * @param track_towards - if true the missile will track the entity as it's potion updates.
     *                      not all missiles support this option.
     */
    void setTarget(Entity entity, boolean track_towards);

    /**
     * Called to find where this missile is heading towards.
     *
     * @return location data, if null the missile has no target
     */
    IPos3D getCurrentTarget();

    /**
     * Called when the missile is destroyed by something before it can impact it's target. Use this in place
     * of setting the missile dead. This way the missile can perform it's own detonation code. As well do
     * other post death processed. Which includes notifying the launcher of it's death, dropping items, etc.
     *
     * @param source                   - optional, TileEntity, Entity is preferred but anything can be inserted
     * @param damage                   - optional but preferred, type of damage that was used to destroyed the missile
     * @param scaleExplosion           - value between 0 - 1, used to scale the size of the warhead explosion if it goes off
     * @param allowDetonationOfEngine  - true will allow the engine to detonate it's fuel
     * @param allowDetonationOfWarhead - true will allow the warhead to go off
     * @param allowDetonationOfOther   - true will allow other parts of the missile to explode
     */
    void destroyMissile(Object source, DamageSource damage, float scaleExplosion, boolean allowDetonationOfWarhead, boolean allowDetonationOfEngine, boolean allowDetonationOfOther);
}
