package com.builtbroken.icbm.content.blast.builder;

import com.builtbroken.icbm.api.blast.IBlastHandler;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveData;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveHandler;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2018.
 */
public class ExProxyGlass extends ExplosiveHandler<BlastProxyGlass> implements IBlastHandler
{
    public ExProxyGlass(ExplosiveData data)
    {
        super(data);
    }

    @Override
    protected BlastProxyGlass newBlast(NBTTagCompound tag)
    {
        return new BlastProxyGlass(this);
    }
}