package com.builtbroken.icbm.content.blast.util;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExRegenLocal extends ExplosiveHandlerICBM<BlastRegenLocal>
{
    public ExRegenLocal()
    {
        super("RegenLocal", 8);
    }

    @Override
    protected BlastRegenLocal newBlast()
    {
        return new BlastRegenLocal();
    }
}
