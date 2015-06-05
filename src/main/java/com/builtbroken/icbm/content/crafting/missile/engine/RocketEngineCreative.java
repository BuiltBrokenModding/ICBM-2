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
    public RocketEngineCreative(ItemStack item)
    {
        super(item, "engine.creative");
    }
}
