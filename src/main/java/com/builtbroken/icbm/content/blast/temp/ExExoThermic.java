package com.builtbroken.icbm.content.blast.temp;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExExoThermic extends ExplosiveHandlerICBM<BlastExoThermic>
{
    public ExExoThermic()
    {
        super("ExoThermic", 2);
    }

    @Override
    protected BlastExoThermic newBlast()
    {
        return new BlastExoThermic();
    }
}
