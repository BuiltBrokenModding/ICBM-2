package com.builtbroken.icbm.content.fof;

import net.minecraft.entity.Entity;

/**
 * Applied to tiles that supply a FoF tag to be shared
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2016.
 */
public interface IFoFStation
{
    /**
     * FoF tag that is shared
     *
     * @return valid string
     */
    String getProvidedFoFTag();

    /**
     * Called to check if the entity is a friendly
     *
     * @param entity - entity most likely being targeted
     * @return true if the entity is friendly
     */
    boolean isFriendly(Entity entity);
}
