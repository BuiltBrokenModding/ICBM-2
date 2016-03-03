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
    Map<String, IIcon> icons = new HashMap();
    Map<String, IIcon> corner_icons = new HashMap();

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
        return "item.icbm:explosiveItem.fragment";
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
    public void registerExplosiveHandlerIcons(IIconRegister reg)
    {
        corner_icon = reg.registerIcon(ICBM.PREFIX + "fragment.corner");
        back_icon = reg.registerIcon(ICBM.PREFIX + "fragment.background");

        for (Fragments frag : Fragments.values())
        {
            String name = frag.name().toLowerCase();
            corner_icons.put(name, reg.registerIcon(ICBM.PREFIX + "fragment.corner." + name));
            icons.put(name, reg.registerIcon(ICBM.PREFIX + "fragment.background." + name));
        }
    }
}
