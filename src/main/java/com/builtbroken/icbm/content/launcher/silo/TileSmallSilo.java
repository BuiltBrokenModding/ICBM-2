package com.builtbroken.icbm.content.launcher.silo;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.multiblock.EnumMultiblock;
import com.builtbroken.mc.prefab.tile.multiblock.MultiBlockHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
 * Small 2 block tall self contained launcher for small missiles.
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
        this.itemBlock = ItemBlockSmallSilo.class;
        this.isOpaque = false;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockSmallSilo), "I I", "I I", "CBC", 'I', Blocks.iron_bars, 'B', OreNames.BLOCK_IRON, 'C', UniversalRecipe.CIRCUIT_T1.get()));
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
        return Blocks.iron_block.getIcon(0, 0);
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
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_SILO_TEXTURE);
        Assets.SMALL_SILO_MODEL.renderOnly("Group_001", "Component_1_001");
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
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.0935f, pos.zf() + 0.5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_SILO_TEXTURE);
        Assets.SMALL_SILO_MODEL.renderAll();
        GL11.glPopMatrix();

        Missile missile = getMissile();
        //Render missile
        if (missile != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.4f, pos.zf() + 0.5f);
            if (missile instanceof ICustomMissileRender)
            {
                GL11.glTranslatef(0, (float) (missile.getHeight() / 2.0), 0);
                if(!((ICustomMissileRender) missile).renderMissileInWorld(0, 0, frame))
                {
                    //TODO Either error or render fake model
                }
            }
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
    public boolean onMultiTileBroken(IMultiTile tileMulti, Object source, boolean harvest)
    {
        if (!_destroyingStructure && tileMulti instanceof TileEntity)
        {
            Pos pos = new Pos((TileEntity) tileMulti).sub(new Pos(this));

            if (tileMapCache.containsKey(pos))
            {
                MultiBlockHelper.destroyMultiBlockStructure(this, harvest, false, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public boolean removeByPlayer(EntityPlayer player, boolean willHarvest)
    {
        MultiBlockHelper.destroyMultiBlockStructure(this, false, false, false);
        if (willHarvest && getMissileItem() != null)
        {
            InventoryUtility.dropItemStack(toLocation(), getMissileItem());
            setInventorySlotContents(0, null);
        }
        return super.removeByPlayer(player, willHarvest);
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
