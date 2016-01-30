package com.builtbroken.icbm.content.blast.util;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExRegen extends ExplosiveHandlerICBM<BlastRegen>
{
    public ExRegen()
    {
        super("Regen", 8);
    }

    @Override
    protected BlastRegen newBlast()
    {
        return new BlastRegen();
    }
}
