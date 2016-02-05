package com.builtbroken.icbm.content.blast.fire;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExFireBomb extends ExplosiveHandlerICBM<BlastFireBomb>
{
    public ExFireBomb()
    {
        super("FireBomb",  1);
    }

    @Override
    protected BlastFireBomb newBlast()
    {
        return new BlastFireBomb();
    }
}
