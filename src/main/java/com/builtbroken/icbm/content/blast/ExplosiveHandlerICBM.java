package com.builtbroken.icbm.content.blast;

import com.builtbroken.icbm.api.blast.IBlastHandler;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveHandler;
import com.builtbroken.mc.framework.explosive.blast.Blast;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
@Deprecated
public abstract class ExplosiveHandlerICBM<B extends Blast> extends ExplosiveHandler<B> implements IBlastHandler
{
    /**
     * Creates an explosive using a blast class, and name
     *
     * @param name  - name to use for registry id
     * @param multi - size to adjust/multiply the original explosive size by
     */
    public ExplosiveHandlerICBM(String name, int multi)
    {
        super(name, multi);
    }
}
