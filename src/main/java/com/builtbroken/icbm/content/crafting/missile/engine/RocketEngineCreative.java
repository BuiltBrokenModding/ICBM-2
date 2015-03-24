package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.IModuleContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/** Creative mode only version of the engine that
 * has no fuel requirement and is used as a
 * place holder when creating missiles without
 * crafting. When removed the engine will break
 * preventing players from cheating.
 *
 * Created by robert on 12/28/2014.
 */
public class RocketEngineCreative extends RocketEngine
{
    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public RocketEngineCreative(ItemStack item)
    {
        super(item, "engine.creative");
    }

    @Override
    public ItemStack getRemovedStack(IModuleContainer container)
    {
        return null;
    }

    @Override @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass)
    {
        return icon;
    }

    @Override @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        register.registerIcon(ICBM.PREFIX + "rocket.motor.creative");
    }
}
