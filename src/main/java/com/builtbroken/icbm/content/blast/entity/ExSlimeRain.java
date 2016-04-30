package com.builtbroken.icbm.content.blast.entity;

import com.builtbroken.icbm.api.blast.IExHandlerTileMissile;
import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public class ExSlimeRain extends ExplosiveHandlerICBM<BlastSlimeRain> implements IExHandlerTileMissile
{
    public ExSlimeRain()
    {
        super("SlimeRain", 2);
    }

    @Override
    protected BlastSlimeRain newBlast()
    {
        return new BlastSlimeRain();
    }

    @Override
    public boolean doesSpawnMissileTile(IMissile missile, IMissileEntity entity)
    {
        //TODO implement small pod spawn(10, etc entities only)
        //TODO implement mob spawner version
        return true;
    }
}
