package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;
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
        return getFragmentType(tag).newBlast(tag);
    }

    /**
     * Gets the string ID used for the fragment.
     *
     * @param stack - item
     * @return valid string, reference from an enum
     */
    protected Fragments getFragmentType(ItemStack stack)
    {
        return getFragmentType(stack.getTagCompound());
    }

    /**
     * Gets the string ID used for the fragment.
     *
     * @param nbt - save file
     * @return valid string, reference from an enum
     */
    public static Fragments getFragmentType(NBTTagCompound nbt)
    {
        if (nbt != null && nbt.hasKey("fragmentType"))
        {
            int i = nbt.getInteger("fragmentType");
            if (i > 0 && i < Fragments.values().length)
            {
                return Fragments.values()[i];
            }
        }
        return Fragments.ARROW;
    }

    public static NBTTagCompound setFragmentType(NBTTagCompound nbt, Fragments frag)
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
