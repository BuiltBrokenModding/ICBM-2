package icbm.content;

import icbm.explosion.ExplosiveRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import resonant.api.explosion.IExplosive;

/**
 * Created by robert on 11/18/2014.
 */
public class ItemSaveUtil
{
    public static IExplosive getExplosive(ItemStack itemStack)
    {
        if(itemStack.getTagCompound() != null)
        {
            return ExplosiveRegistry.get(itemStack.getTagCompound().getString("explosiveString"));
        }
        return null;
    }

    public static void setExplosive(ItemStack itemStack, IExplosive ex)
    {
        if(itemStack.getTagCompound() == null)
            itemStack.setTagCompound(new NBTTagCompound());

        itemStack.getTagCompound().setString("explosiveString", ex.getUnlocalizedName());
    }
}
