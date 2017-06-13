package com.builtbroken.icbm.api.modules;

import com.builtbroken.icbm.content.missile.entity.EntityMissile;
import com.builtbroken.mc.api.modules.IModuleEntityDriven;

/**
 * Prefab interface for all missile based module interfaces.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/13/2016.
 */
public interface IMissileModule extends IModuleEntityDriven<EntityMissile> //TODO move to interface instead of direct entity class
{
    /**
     * Size of the missile this part is
     * designed to fit into.
     * <p/>
     * Negative one will assume any missile size
     * will work. Warheads and guidance systems
     * are suggested to return -1. Engines are
     * suggested to be size based.
     * </p>
     * With the exception of engines small
     * modules will fit into larger rockets.
     * Though this will have an effect on
     * the performance of the rocket.
     *
     * @return int value matching enum {@link com.builtbroken.icbm.content.missile.parts.casing.MissileCasings}
     */
    int getMissileSize();


    /**
     * Called each tick the missile exists
     *
     * @param missile - missile entity
     */
    void update(EntityMissile missile);
}
