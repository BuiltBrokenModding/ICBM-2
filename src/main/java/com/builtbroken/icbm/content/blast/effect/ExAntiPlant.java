package com.builtbroken.icbm.content.blast.effect;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExAntiPlant extends ExplosiveHandlerICBM<BlastAntiPlant>
{
    public ExAntiPlant()
    {
        super("AntiPlant", 3);
    }

    @Override
    protected BlastAntiPlant newBlast()
    {
        return new BlastAntiPlant();
    }
}
