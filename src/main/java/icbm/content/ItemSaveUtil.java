package icbm.content;

import icbm.explosion.ExplosiveRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import icbm.api.explosion.IExplosive;

/**
 * Created by robert on 11/18/2014.
 */
public class ItemSaveUtil
{
    public static IExplosive getExplosive(ItemStack itemStack)
    {
        if(itemStack.getTagCompound() != null)
        {
            return getExplosive(itemStack.getTagCompound());
        }
        return null;
    }

    public static IExplosive getExplosive(NBTTagCompound tag)
    {
        if(tag != null)
        {
            return ExplosiveRegistry.get(tag.getString("explosiveString"));
        }
        return null;
    }

    public static void setExplosive(ItemStack itemStack, String ex)
    {
        setExplosive(itemStack, ExplosiveRegistry.get(ex));
    }

    public static void setExplosive(ItemStack itemStack, IExplosive ex)
    {
        if(ex != null)
        {
            if (itemStack.getTagCompound() == null)
                itemStack.setTagCompound(new NBTTagCompound());

            setExplosive(itemStack.getTagCompound(), ex);
        }
    }

    public static void setExplosive(NBTTagCompound tag, IExplosive ex)
    {
        if(ex != null)
        {
            tag.setString("explosiveString", ex.getUnlocalizedName());
        }
    }

    public static int getSize(ItemStack itemStack)
    {
        if (itemStack.getTagCompound() == null)
            itemStack.setTagCompound(new NBTTagCompound());

        return getSize(itemStack.getTagCompound());
    }

    public static int getSize(NBTTagCompound tag)
    {
        return tag.getInteger("exSize");
    }

    public static NBTTagCompound setSize(ItemStack itemStack, int size)
    {
        if (itemStack.getTagCompound() == null)
            itemStack.setTagCompound(new NBTTagCompound());

        return setSize(itemStack.getTagCompound(), size);
    }

    public static NBTTagCompound setSize(NBTTagCompound tag, int size)
    {
        tag.setInteger("exSize", size);
        return tag;
    }
}
