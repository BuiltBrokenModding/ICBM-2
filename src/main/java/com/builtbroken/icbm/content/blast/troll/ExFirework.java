package com.builtbroken.icbm.content.blast.troll;

import com.builtbroken.icbm.api.blast.IBlastHandler;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveData;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/31/2017.
 */
public class ExFirework extends ExplosiveHandler<BlastFirework> implements IBlastHandler
{
    public static final String NBT_KEY = "firework";

    public ExFirework(ExplosiveData data)
    {
        super(data);
    }

    @Override
    protected BlastFirework newBlast(NBTTagCompound tag)
    {
        if (tag != null && tag.hasKey(NBT_KEY))
        {
            return new BlastFirework(this, ItemStack.loadItemStackFromNBT(tag.getCompoundTag(NBT_KEY)));
        }
        return null;
    }
}