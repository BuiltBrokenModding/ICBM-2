package com.builtbroken.icbm.content.launcher.silo;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.Assets;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.multiblock.EnumMultiblock;
import com.builtbroken.mc.prefab.tile.multiblock.MultiBlockHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by robert on 3/28/2015.
 */
public class TileSmallSilo extends TileAbstractLauncher implements ISimpleItemRenderer, IMultiTileHost, IPostInit
{
    public static HashMap<IPos3D, String> tileMapCache = new HashMap();

    static
    {
        tileMapCache.put(new Pos(0, 1, 0), EnumMultiblock.TILE.getName());
        tileMapCache.put(new Pos(0, 2, 0), EnumMultiblock.TILE.getName());
    }

    private boolean _destroyingStructure = false;

    public TileSmallSilo()
    {
        super("smallsilo", Material.iron, 1);
        this.isOpaque = false;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockSmallSilo), "I I", "I I", "CBC", 'I', Blocks.iron_bars, 'B', Blocks.iron_block, 'C', UniversalRecipe.CIRCUIT_T1.get()));
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
        return "tile.icbm:smallSilo.container";
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

    @Override
    public void firstTick()
    {
        super.firstTick();
        MultiBlockHelper.buildMultiBlock(world(), this, true);
    }

    @Override
    public void onMultiTileAdded(IMultiTile tileMulti)
    {
        if (tileMulti instanceof TileEntity)
        {
            if (tileMapCache.containsKey(new Pos(this).sub(new Pos((TileEntity) tileMulti))))
            {
                tileMulti.setHost(this);
            }
        }
    }

    @Override
    public void onRemove(Block block, int par6)
    {
        super.onRemove(block, par6);
        breakDownStructure(true);
    }

    @Override
    public boolean onMultiTileBroken(IMultiTile tileMulti, Object source, boolean harvest)
    {
        if (!_destroyingStructure && tileMulti instanceof TileEntity)
        {
            Pos pos = new Pos((TileEntity) tileMulti).sub(new Pos(this));

            if (tileMapCache.containsKey(pos))
            {
                breakDownStructure(harvest);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canPlaceBlockAt()
    {
        return super.canPlaceBlockAt() && world().getBlock(xi(), yi() + 1, zi()).isReplaceable(world(), xi(), yi() + 1, zi()) && world().getBlock(xi(), yi() + 2, zi()).isReplaceable(world(), xi(), yi() + 2, zi());
    }

    @Override
    public boolean canPlaceBlockOnSide(ForgeDirection side)
    {
        return side == ForgeDirection.UP && canPlaceBlockAt();
    }

    @Override
    public boolean removeByPlayer(EntityPlayer player, boolean willHarvest)
    {
        breakDownStructure(willHarvest);
        return true;
    }

    private void breakDownStructure(boolean doDrops)
    {
        if (!_destroyingStructure)
        {
            _destroyingStructure = true;
            MultiBlockHelper.destroyMultiBlockStructure(this, doDrops);
            _destroyingStructure = false;
        }
    }

    @Override
    public void onTileInvalidate(IMultiTile tileMulti)
    {

    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, IPos3D hit)
    {
        return this.onPlayerRightClick(player, side, new Pos(hit));
    }

    @Override
    public void onMultiTileClicked(IMultiTile tile, EntityPlayer player)
    {

    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        HashMap<IPos3D, String> map = new HashMap();
        Pos center = new Pos(this);
        for (Map.Entry<IPos3D, String> entry : tileMapCache.entrySet())
        {
            map.put(center.add(entry.getKey()), entry.getValue());
        }
        return map;
    }
}
