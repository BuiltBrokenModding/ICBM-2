package com.builtbroken.icbm.client.blast;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.blast.fragment.ExFragment;
import com.builtbroken.icbm.content.blast.fragment.Fragments;
import com.builtbroken.icbm.content.blast.fragment.IFragmentExplosiveHandler;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.HashMap;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/2/2016.
 */
public class ECFragment extends ExFragment implements IFragmentExplosiveHandler
{
    IIcon corner_icon;
    IIcon back_icon;
    Map<Fragments, IIcon> icons = new HashMap();
    Map<Fragments, IIcon> icons_back = new HashMap();
    Map<Fragments, IIcon> corner_icons = new HashMap();


    @Override
    public IIcon getFragmentIcon(ItemStack stack, int layer)
    {
        if (layer == 1)
        {
            IIcon icon = icons.get(getFragmentType(stack));
            if (icon != null)
            {
                return icon;
            }
        }
        else if (layer == 0)
        {
            IIcon icon = icons_back.get(getFragmentType(stack));
            if (icon != null)
            {
                return icon;
            }
        }
        return back_icon;
    }


    @Override
    public int getFragmentNumberOfPasses()
    {
        return 2;
    }

    @Override
    public String getFragmentLocalization(ItemStack stack)
    {
        return "item.icbm:explosiveItem.fragment." + getFragmentType(stack).name().toLowerCase();
    }

    @Override
    public IIcon getBottomLeftCornerIcon(ItemStack stack)
    {
        IIcon icon = corner_icons.get(getFragmentType(stack));
        if (icon != null)
        {
            return icon;
        }
        return corner_icon;
    }

    @Override
    public void registerExplosiveHandlerIcons(IIconRegister reg, boolean blocks)
    {
        if (!blocks)
        {
            back_icon = reg.registerIcon(ICBM.PREFIX + "tnt-icon-2");
            for (Fragments frag : Fragments.values())
            {
                //TODO check to make sure duplicates only register once, as MC should return original if a duplicate registry is called
                corner_icons.put(frag, reg.registerIcon(ICBM.PREFIX + frag.corner_icon_name));
                icons.put(frag, reg.registerIcon(ICBM.PREFIX + frag.icon_name));
                icons_back.put(frag, reg.registerIcon(ICBM.PREFIX + frag.back_ground_icon_name));
            }
            corner_icon = corner_icons.get(Fragments.COBBLESTONE);
        }
    }
}
