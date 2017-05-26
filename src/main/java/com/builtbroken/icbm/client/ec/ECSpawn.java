package com.builtbroken.icbm.client.ec;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.blast.entity.ExSpawn;
import com.builtbroken.mc.api.explosive.ITexturedExplosiveHandler;
import com.builtbroken.mc.client.ExplosiveRegistryClient;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2016.
 */
public class ECSpawn extends ExSpawn implements ITexturedExplosiveHandler
{
    IIcon icon;
    IIcon icon_1;
    IIcon icon_2;

    @Override
    public IIcon getBottomLeftCornerIcon(ItemStack stack, int pass)
    {
        switch (pass)
        {
            case 0:
                return icon;
            case 1:
                return icon_1;
            case 2:
                return icon_2;
        }
        return null;
    }

    @Override
    public void registerExplosiveHandlerIcons(IIconRegister reg, boolean blocks)
    {
        if (!blocks)
        {
            icon = reg.registerIcon(ICBM.PREFIX + "ex.icon.egg.0");
            icon_1 = reg.registerIcon(ICBM.PREFIX + "ex.icon.egg.1");
            icon_2 = reg.registerIcon(ICBM.PREFIX + "ex.icon.egg.2");
        }
    }

    @Override
    public int getBottomLeftCornerIconColor(ItemStack item, int pass)
    {
        EntityList.EntityEggInfo eggInfo = ExplosiveRegistryClient.getEggInfo(getEntityID(item));
        if (eggInfo != null)
        {
            switch (pass)
            {
                case 1:
                    return eggInfo.primaryColor;
                case 2:
                    return eggInfo.secondaryColor;
            }
        }
        return 16777215;
    }
}
