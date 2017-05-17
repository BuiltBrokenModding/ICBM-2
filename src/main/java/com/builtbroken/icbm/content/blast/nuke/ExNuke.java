package com.builtbroken.icbm.content.blast.nuke;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExNuke extends ExplosiveHandlerICBM<BlastNuke>
{
    /**
     * Creates an explosive using a blast class, and name
     */
    public ExNuke()
    {
        super("Nuke", (int) BlastNuke.defaultSize);
    }

    @Override
    protected BlastNuke newBlast()
    {
        BlastNuke nuke = new BlastNuke(this);
        //nuke.explosivesToTriggerAfter.add(ExplosiveRegistry.get("Microwave"));
        return nuke;
    }

    @Override
    public boolean doesVaporizeParts(IMissileEntity entity, IMissile missile, IWarhead warhead, boolean warheadBlew, boolean engineBlew)
    {
        return true;
    }
}
