package com.builtbroken.icbm.content.launcher.block;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import com.builtbroken.mc.core.content.tool.ItemSheetMetalTools;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.recipe.item.sheetmetal.RecipeSheetMetal;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileMachine;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/24/2016.
 */
public class TileLauncherFrame extends TileMachine implements ISimpleItemRenderer, IPostInit
{
    public TileLauncherFrame()
    {
        super("launcherFrame", Material.iron);
        this.resistance = 20;
        this.hardness = 20;
        this.isOpaque = false;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
    }

    @Override
    public Tile newTile()
    {
        return new TileLauncherFrame();
    }

    @Override
    public void onPostInit()
    {
        if (Engine.itemSheetMetal != null && Engine.itemSheetMetalTools != null)
        {
            GameRegistry.addRecipe(new RecipeSheetMetal(new ItemStack(ICBM.blockLauncherFrame, 1, 0), "RHR", "RCR", "RDR", 'C', UniversalRecipe.WIRE.get(), 'R', OreNames.ROD_IRON, 'P', ItemSheetMetal.SheetMetal.FULL.stack(), 'H', ItemSheetMetalTools.getHammer(), 'D', ItemSheetMetalTools.getShears()));
        }
        else
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockLauncherFrame, 1, 0), "RPR", "PRP", "RPR", 'R', UniversalRecipe.PRIMARY_METAL.get(), 'P', UniversalRecipe.PRIMARY_PLATE.get()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister p_149651_1_)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return Blocks.iron_bars.getIcon(p_149691_1_, p_149691_2_);
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(-0.0F, -0.5F, 0.0F);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.LAUNCHER_FRAME_TEXTURE);
        Assets.LAUNCHER_FRAME_BLOCK_MODEL.renderAll();
        GL11.glPopMatrix();
    }

    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf(), pos.zf() + 0.5f);
        if (getMetadata() == 0)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.LAUNCHER_FRAME_TEXTURE);
            Assets.LAUNCHER_FRAME_BLOCK_MODEL.renderAll();
        }
        else
        {
            int rotation = getMetadata();
            switch (rotation)
            {
                case 1:
                    GL11.glRotatef(180, 0, 1, 0);
                    break;
                case 2:
                    GL11.glRotatef(0, 0, 1, 0);
                    break;
                case 3:
                    GL11.glRotatef(90, 0, 1, 0);
                    break;
                case 4:
                    GL11.glRotatef(-90, 0, 1, 0);
                    break;
            }
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.LAUNCHER_FRAME_TEXTURE);
            Assets.LAUNCHER_FRAME_BLOCK_TOP_MODEL.renderAll();
        }
        GL11.glPopMatrix();
    }
}
