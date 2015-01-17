package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICustomMissileRender;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

/**
 * Created by robert on 12/29/2014.
 */
public class MissileSmallClassic extends MissileSmall
{

    public MissileSmallClassic(ItemStack stack)
    {
        super(stack);
    }

    @Override
    public boolean renderMissileItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data)
    {
       return false;
    }

    @Override
    public boolean renderMissileInWorld()
    {
        return false;
    }
}
