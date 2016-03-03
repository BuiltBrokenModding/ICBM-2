package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.mc.api.explosive.ITexturedExplosiveHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/2/2016.
 */
public interface IFragmentExplosiveHandler extends ITexturedExplosiveHandler
{
    /**
     * Gets the icon used to render as the fragment item texture
     * or as an overlay
     *
     * @param stack - item being rendered
     * @return valid icon
     */
    IIcon getFragmentIcon(ItemStack stack, int layer);

    /**
     * Number of passes to use when rendering the item.
     *
     * @return greater than or equal to 1
     */
    int getFragmentNumberOfPasses();

    /**
     * Gets the localization to use in place of the
     * original item name.
     *
     * @param stack - item, use to extract data if needed
     * @return valid string localization
     */
    String getFragmentLocalization(ItemStack stack);
}
