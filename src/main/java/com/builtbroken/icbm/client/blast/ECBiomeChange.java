package com.builtbroken.icbm.client.blast;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.blast.biome.ExBiomeChange;
import com.builtbroken.mc.api.explosive.ITexturedExplosiveHandler;
import com.google.common.collect.SetMultimap;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/2/2016.
 */
public class ECBiomeChange extends ExBiomeChange implements ITexturedExplosiveHandler
{
    Map<String, IIcon> corner_icons = new HashMap();

    boolean hasInit = false;

    @Override
    public IIcon getBottomLeftCornerIcon(ItemStack stack)
    {
        if (!hasInit)
        {
            try
            {
                Field field = FMLClientHandler.class.getDeclaredField("missingTextures");
                field.setAccessible(true);
                SetMultimap<String, ResourceLocation> set = (SetMultimap<String, ResourceLocation>) field.get(FMLClientHandler.instance());
                if (set.containsKey("icbm"))
                {
                    Map<String, Collection<ResourceLocation>> map = set.asMap();
                    Collection<ResourceLocation> locations = map.get("icbm");
                    for (ResourceLocation location : locations)
                    {
                        String name = location.getResourcePath().replace("textures/items/ex.icon.", "").replace(".png", "");
                        if (corner_icons.containsKey(name))
                        {
                            corner_icons.remove(name);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            hasInit = true;
        }
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("biomeID"))
        {
            BiomeGenBase biome = BiomeGenBase.getBiome(stack.getTagCompound().getByte("biomeID"));
            if (biome != null)
            {
                IIcon icon = corner_icons.get(biome.biomeName);
                if (icon != null)
                {
                    return icon;
                }
            }
        }
        return Blocks.grass.getIcon(1, 0);
    }

    @Override
    public void registerExplosiveHandlerIcons(IIconRegister reg, boolean blocks)
    {
        if (!blocks)
        {
            for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
            {
                if (biome != null && biome.biomeID >= 0)
                {
                    IIcon icon = reg.registerIcon(ICBM.PREFIX + "ex.icon." + biome.biomeName);
                    if (icon != TextureUtil.missingTexture)
                    {
                        corner_icons.put(biome.biomeName, icon);
                    }
                }
            }
        }
    }
}
