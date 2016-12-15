package com.builtbroken.icbm.client.ec;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.client.blast.BlastAntimatterClient;
import com.builtbroken.icbm.content.blast.explosive.BlastAntimatter;
import com.builtbroken.icbm.content.blast.explosive.ExAntimatter;
import com.builtbroken.mc.api.explosive.ITexturedExplosiveHandler;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2016.
 */
public class ECAntimatter extends ExAntimatter implements ITexturedExplosiveHandler
{
    IIcon icon;

    @Override
    public IIcon getBottomLeftCornerIcon(ItemStack stack)
    {
        return icon;
    }

    @Override
    protected BlastAntimatter newBlast()
    {
        return new BlastAntimatterClient(this);
    }

    @Override
    public void registerExplosiveHandlerIcons(IIconRegister reg, boolean blocks)
    {
        if (!blocks)
        {
            icon = reg.registerIcon(ICBM.PREFIX + "ex.icon.antimatter");
        }
    }
}
