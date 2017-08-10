package com.builtbroken.icbm.content.blast.entity;

import com.builtbroken.icbm.api.blast.IBlastHandler;
import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.mc.api.items.explosives.IExplosiveItem;
import com.builtbroken.mc.client.ExplosiveRegistryClient;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveData;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveHandler;
import com.builtbroken.mc.framework.json.imp.IJsonKeyDataProvider;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Created by robert on 12/25/2014.
 */
public class ExSpawn extends ExplosiveHandler<BlastSpawn> implements IBlastHandler, IJsonKeyDataProvider
{
    public ExSpawn(ExplosiveData data)
    {
        super(data);
    }

    @Override
    public void addInfoToItem(EntityPlayer player, ItemStack stack, List<String> lines)
    {
        lines.add("Entity: " + EntityList.getStringFromID(getEntityID(stack)));
        lines.add("Count: " + stack.stackSize);
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

    @Override
    public Object getJsonKeyData(String key, ItemStack item)
    {
        if (key.equalsIgnoreCase("entityPrimary"))
        {
            EntityList.EntityEggInfo eggInfo = ExplosiveRegistryClient.getEggInfo(getEntityID(item));
            if (eggInfo != null)
            {
                return eggInfo.primaryColor;
            }
        }
        else if (key.equalsIgnoreCase("entitySecondary"))
        {
            EntityList.EntityEggInfo eggInfo = ExplosiveRegistryClient.getEggInfo(getEntityID(item));
            if (eggInfo != null)
            {
                return eggInfo.secondaryColor;
            }
        }
        return null;
    }
}
