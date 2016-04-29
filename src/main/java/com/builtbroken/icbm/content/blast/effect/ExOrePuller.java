package com.builtbroken.icbm.content.blast.effect;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;
import com.builtbroken.icbm.content.blast.util.BlastOrePuller;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/29/2016.
 */
public class ExOrePuller extends ExplosiveHandlerICBM<BlastOrePuller>
{
    public ExOrePuller()
    {
        super("OrePuller", 1);
    }

    @Override
    protected BlastOrePuller newBlast()
    {
        return new BlastOrePuller();
    }
}
