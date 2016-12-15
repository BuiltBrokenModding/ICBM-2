package com.builtbroken.icbm.client.ec;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.blast.biome.ExBiomeChange;
import com.builtbroken.mc.api.explosive.ITexturedExplosiveHandler;
import com.google.common.collect.SetMultimap;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/2/2016.
 */
public class ECBiomeChange extends ExBiomeChange implements ITexturedExplosiveHandler
{
    //TODO add cooking to merge layers down into single icons for faster rendering
    Map<String, IIcon> corner_icons = new HashMap();
    Map<String, IIcon> background_icons = new HashMap();
    Map<String, IIcon> overlay_icons = new HashMap();

    Map<String, String> backgroundForBiome = new HashMap();
    Map<String, String> textureNameOverrideForIcons = new HashMap();

    IIcon defaultCornerIcon;
    public static IIcon defaultIcon;

    IIcon blank;
    IIcon defaultBackground;
    IIcon emptyBackground;
    IIcon glassIcon;
    IIcon frameIcon;


    boolean hasInit = false;

    public ECBiomeChange()
    {
        textureNameOverrideForIcons.put("Cold Taiga Hills", ICBM.PREFIX + "ex.biome.overlay.snow");
        textureNameOverrideForIcons.put("Hell", ICBM.PREFIX + "ex.biome.overlay.netherrack");
    }

    @Override
    public IIcon getBottomLeftCornerIcon(ItemStack stack)
    {
        init();
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
        return defaultCornerIcon;
    }

    public IIcon getIcon(ItemStack stack, int pass)
    {
        init();
        int id = getBiomeID(stack);
        //Background
        if (pass == 0)
        {
            if (id >= 0 && id < BiomeGenBase.getBiomeGenArray().length)
            {
                BiomeGenBase biome = BiomeGenBase.getBiome(id);
                if (biome != null && backgroundForBiome.containsKey(biome.biomeName))
                {
                    IIcon icon = background_icons.get(backgroundForBiome.get(biome.biomeName));
                    if (icon != null)
                    {
                        return icon;
                    }
                }
                return defaultBackground;
            }
            return emptyBackground;
        }
        //Biome overlay
        else if (pass == 1)
        {
            if (id >= 0 && id < 255)
            {
                BiomeGenBase biome = BiomeGenBase.getBiome(id);
                if (biome != null)
                {
                    IIcon icon = overlay_icons.get(biome.biomeName);
                    if (icon != null)
                    {
                        return icon;
                    }
                    return blank;
                }
            }
        }
        else if (pass == 2)
        {
            return glassIcon;
        }
        else if (pass == 3)
        {
            return frameIcon;
        }
        return blank;
    }

    private final void init()
    {
        if (!hasInit)
        {
            //Bellow ensure that missing textures do not exist
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
                        name = location.getResourcePath().replace("textures/items/ex.biome.", "").replace(".png", "");
                        if (overlay_icons.containsKey(name))
                        {
                            overlay_icons.remove(name);
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
    }

    @Override
    public void registerExplosiveHandlerIcons(IIconRegister reg, boolean blocks)
    {
        if (!blocks)
        {
            defaultCornerIcon = reg.registerIcon(ICBM.PREFIX + "ex.icon.grass");
            defaultIcon = reg.registerIcon(ICBM.PREFIX + "ex.biome");

            defaultBackground = reg.registerIcon(ICBM.PREFIX + "ex.biome.back.grass");
            blank = reg.registerIcon(ICBM.PREFIX + "blank");

            glassIcon = reg.registerIcon(ICBM.PREFIX + "ex.biome.item.glass");
            frameIcon = reg.registerIcon(ICBM.PREFIX + "ex.biome.item.frame");
            emptyBackground = reg.registerIcon(ICBM.PREFIX + "ex.biome.back.blank");

            for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
            {
                if (biome != null && biome.biomeID >= 0)
                {
                    String icon = ICBM.PREFIX + "ex.icon." + biome.biomeName;
                    if (textureNameOverrideForIcons.containsKey(biome.biomeName) && textureNameOverrideForIcons.get(biome.biomeName) != null && !textureNameOverrideForIcons.get(biome.biomeName).isEmpty())
                    {
                        icon = textureNameOverrideForIcons.get(biome.biomeName);
                    }
                    corner_icons.put(biome.biomeName, reg.registerIcon(icon));
                    overlay_icons.put(biome.biomeName, reg.registerIcon(ICBM.PREFIX + "ex.biome." + biome.biomeName));
                }
            }
            for (String iconName : backgroundForBiome.values())
            {
                background_icons.put(iconName, reg.registerIcon(iconName));
            }
        }
    }
}
