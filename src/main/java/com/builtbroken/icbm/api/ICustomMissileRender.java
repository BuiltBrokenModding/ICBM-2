package com.builtbroken.icbm.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

/**
 * Created by robert on 12/12/2014.
 */
@SideOnly(Side.CLIENT)
public interface ICustomMissileRender
{
    /**
     * Location and rotation is already set based on default missile. So only make minor adjustments based
     * on your model as needed. Avoid extra effects as this will slow down the render time in the inventory.
     *
     * @param type  - render type
     * @param stack - missile itemstack
     * @param data  - unknown?
     * @return false to use the default missile render
     */
    public boolean renderMissileItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data);

    /**
     * Location and rotation is already set based on default missile. So only make minor adjustments based
     * on your model as needed.
     * @return false to use the default missile render
     */
    public boolean renderMissileInWorld();

}
