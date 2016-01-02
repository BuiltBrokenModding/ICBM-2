/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.mc.lib.helper.LanguageUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;

/**
 * Handles research pages for thaumcraft
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/2/2016.
 */
public class ResearchItemICBM extends ResearchItem
{
    public ResearchItemICBM(String key, String catagory)
    {
        super(key, catagory);
    }

    public ResearchItemICBM(String key, String category, AspectList aspects, int displayColumn, int displayRow, int complexity, ResourceLocation icon)
    {
        super(key, category, aspects, displayColumn, displayRow, complexity, icon);
    }

    public ResearchItemICBM(String key, String category, AspectList aspects, int displayColumn, int displayRow, int complexity, ItemStack icon)
    {
        super(key, category, aspects, displayColumn, displayRow, complexity, icon);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getName()
    {
        return LanguageUtility.getLocal(String.format("thaumcraft.research.%s.name", key));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getText()
    {
        return LanguageUtility.getLocal(String.format("thaumcraft.research.%s.text", key));
    }
}