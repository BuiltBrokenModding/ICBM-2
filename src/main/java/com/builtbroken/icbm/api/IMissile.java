package com.builtbroken.icbm.api;

import com.builtbroken.jlib.data.vector.IPos3D;
import net.minecraft.entity.Entity;

/** Applied to entities that are missiles
 * Created by robert on 11/19/2014.
 */
public interface IMissile
{
    /** Tells the missile to start moving */
    void setIntoMotion();

    void setTarget(IPos3D location, boolean ark);

    void setTarget(Entity entity, boolean track_towards);
}
