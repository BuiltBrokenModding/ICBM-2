package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;
import com.builtbroken.mc.api.items.explosives.IExplosiveItem;
import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExFragment extends ExplosiveHandlerICBM<Blast>
{
    /**
     * Creates an explosive using a blast class, and name
     */
    public ExFragment()
    {
        super("Fragment", 3);
    }

    @Override
    protected Blast newBlast(NBTTagCompound tag)
    {
        FragBlastType type = getFragmentType(tag);
        if (type == FragBlastType.ARROW)
        {
            return new BlastArrows(this);
        }
        else
        {
            return new BlastFragments(this, type);
        }
    }

    /**
     * Gets the string ID used for the fragment.
     *
     * @param stack - item
     * @return valid string, reference from an enum
     */
    protected FragBlastType getFragmentType(ItemStack stack)
    {
        return stack.getItem() instanceof IExplosiveItem ? getFragmentType(((IExplosiveItem) stack.getItem()).getAdditionalExplosiveData(stack)) : FragBlastType.ARROW;
    }

    /**
     * Gets the string ID used for the fragment.
     *
     * @param nbt - save file
     * @return valid string, reference from an enum
     */
    public static FragBlastType getFragmentType(NBTTagCompound nbt)
    {
        if (nbt != null && nbt.hasKey("fragmentType"))
        {
            int i = nbt.getInteger("fragmentType");
            if (i > 0 && i < FragBlastType.values().length)
            {
                return FragBlastType.values()[i];
            }
        }
        return FragBlastType.ARROW;
    }

    /**
     * Sets the fragment type of the item, the item needs to be
     * an instance of {@link IExplosiveItem}
     *
     * @param stack - stack
     * @param frag  - type
     */
    public static void setFragmentType(ItemStack stack, FragBlastType frag)
    {
        if (stack.getItem() instanceof IExplosiveItem)
        {
            setFragmentType(((IExplosiveItem) stack.getItem()).getAdditionalExplosiveData(stack), frag);
        }
    }

    /**
     * Sets the fragment type into the NBT
     *
     * @param nbt
     * @param frag
     * @return
     */
    public static NBTTagCompound setFragmentType(NBTTagCompound nbt, FragBlastType frag)
    {
        if (frag != null)
        {
            nbt.setInteger("fragmentType", frag.ordinal());
        }
        else if (nbt.hasKey("fragmentType"))
        {
            nbt.removeTag("fragmentType");
        }
        return nbt;
    }
}
