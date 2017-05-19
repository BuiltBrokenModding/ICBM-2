package com.builtbroken.icbm.content.blast.potion;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExRadiation extends ExplosiveHandlerICBM<BlastRadiation>
{
    /**
     * Creates an explosive using a blast class, and name
     */
    public ExRadiation()
    {
        super("Radiation", 1);
    }

    @Override
    protected BlastRadiation newBlast()
    {
        return new BlastRadiation(this);
    }
}
