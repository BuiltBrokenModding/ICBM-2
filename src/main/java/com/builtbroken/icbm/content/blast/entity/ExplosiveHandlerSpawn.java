package com.builtbroken.icbm.content.blast.entity;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;
import com.builtbroken.mc.api.items.explosives.IExplosiveItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by robert on 12/25/2014.
 */
public class ExplosiveHandlerSpawn extends ExplosiveHandlerICBM<BlastSpawn>
{
    public ExplosiveHandlerSpawn()
    {
        super("EntitySpawn", 1);
    }

    public static int getEntityID(ItemStack stack)
    {
        return stack.getItem() instanceof IExplosiveItem ? ((IExplosiveItem) stack.getItem()).getAdditionalExplosiveData(stack).getInteger("EntityID") : -1;
    }

    public static void setEntityID(ItemStack stack, int id)
    {
        if (stack.getItem() instanceof IExplosiveItem)
        {
            ((IExplosiveItem) stack.getItem()).getAdditionalExplosiveData(stack).setInteger("EntityID", id);
        }
    }

    @Override
    protected BlastSpawn newBlast(NBTTagCompound tag)
    {
        int entityID = tag.getInteger("EntityID");
        return new BlastEntitySpawn(this, entityID);
    }

    @Override
    public boolean doesDamageMissile(IMissileEntity entity, IMissile missile, IWarhead warhead, boolean warheadBlew, boolean engineBlew)
    {
        return engineBlew;
    }
}
