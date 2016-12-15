package com.builtbroken.icbm.content.blast.entity;

import com.builtbroken.icbm.api.blast.IBlastTileMissile;
import com.builtbroken.icbm.api.missile.ITileMissile;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.prefab.explosive.blast.Blast;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public class BlastSlimeRain extends Blast<BlastSlimeRain> implements IBlastTileMissile
{
    //TODO spawn missile tile
    //TODO shoot green beam in air
    //TODO while beam spawn slime rain in an area
    //TODO make sky render colorful

    private int ticks = 0;
    private int duration = 100;

    public BlastSlimeRain(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public void tickBlast(ITileMissile tile, IMissile missile)
    {
        ticks++;
        if (ticks >= Integer.MAX_VALUE - 4)
        {
            ticks = 1;
        }
        if (ticks % 5 == 0)
        {
            //TODO pick random location
            EntitySlimeRain entity = new EntitySlimeRain(world);
        }
        if (ticks >= duration)
        {
            killAction(false);
        }
    }

    @Override
    public boolean isCompleted()
    {
        return super.isCompleted() || ticks >= duration;
    }
}
