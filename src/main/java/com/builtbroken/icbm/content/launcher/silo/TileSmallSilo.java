package com.builtbroken.icbm.content.launcher.silo;

import com.builtbroken.icbm.content.Assets;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.icbm.content.launcher.launcher.GuiSmallLauncher;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by robert on 3/28/2015.
 */
public class TileSmallSilo extends TileAbstractLauncher implements ISimpleItemRenderer
{
    public TileSmallSilo()
    {
        super("smallsilo", Material.iron, 1);
        this.isOpaque = false;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
    }

    @Override
    public boolean canAcceptMissile(Missile missile)
    {
        return super.canAcceptMissile(missile) && missile.casing == MissileCasings.SMALL;
    }

    @Override
    public Tile newTile()
    {
        return new TileSmallSilo();
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.gravel.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        //We have no icons to register
    }

    @Override
    public String getInventoryName()
    {
        return "tile.icbm:smallLauncher.container";
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glScaled(.8f, .8f, .8f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        Assets.SMALL_SILO_MODEL.renderOnly("base", "botCorner1", "botCorner2", "botCorner3", "botCorner4");
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new Cube(0, 0, 0, 1, 3, 1).add(x(), y(), z()).toAABB();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        Assets.SMALL_SILO_MODEL.renderAll();
        GL11.glPopMatrix();

        //Render missile
        if (getMissile() != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
            GL11.glScaled(.0015625f, .0015625f, .0015625f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);
            Assets.SMALL_MISSILE_MODEL.renderAll();
            GL11.glPopMatrix();
        }
    }
}
