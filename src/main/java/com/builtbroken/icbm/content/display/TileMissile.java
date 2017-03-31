package com.builtbroken.icbm.content.display;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.item.ItemBlockMetadata;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Version of the Missile Display that is static
 * Created by robert on 1/16/2015.
 */
public class TileMissile extends Tile implements IPostInit, ISimpleItemRenderer
{
    public TileMissile()
    {
        super("TileMissile", Material.anvil);
        this.itemBlock = ItemBlockMetadata.class;
        this.bounds = new Cube(0, 0, 0, 1, .1, 1);
        this.isOpaque = false;
        this.renderNormalBlock = true;
        this.renderTileEntity = true;
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addShapedRecipe(new ItemStack(ICBM.blockDisplayMissile, 1, 1), "glg", "sls", "ppp", 'p', Blocks.planks, 'l', Blocks.log, 'g', new ItemStack(Items.dye, 1, 2), 's', new ItemStack(Items.dye, 1, 7));
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list)
    {
        //TODO list.add(new ItemStack(item, 1, 0)); add Micro missile renderer
        list.add(new ItemStack(item, 1, 1));
    }


    @Override
    public Tile newTile()
    {
        return new TileMissile();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.planks.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        //We have no icons to register
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        renderDynamic(new Pos(), 0, 0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new Cube(0, 0, 0, 1, 3, 1).add(x(), y(), z()).toAABB();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() - 0f, pos.zf() + 0.5f);
        float scale = .0013f;
        GL11.glScaled(scale, scale, scale);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);
        Assets.SMALL_MISSILE_MODEL.renderAll();
        GL11.glPopMatrix();
    }
}
