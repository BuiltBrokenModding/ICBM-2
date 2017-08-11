package com.builtbroken.icbm.content.missile.entity;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.mc.prefab.entity.type.EntityTypeCheck;
import net.minecraft.entity.Entity;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/23/2017.
 */
public class EntityTypeCheckMissile extends EntityTypeCheck
{
    public EntityTypeCheckMissile()
    {
        super("missile");
    }

    @Override
    public boolean isEntityApplicable(Entity entity)
    {
        return entity instanceof IMissileEntity || entity instanceof resonant.api.explosion.IMissile;
    }
}
