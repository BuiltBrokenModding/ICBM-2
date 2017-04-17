package com.builtbroken.icbm.content.launcher.silo;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.tile.listeners.IDestroyedListener;
import com.builtbroken.mc.api.tile.listeners.IPlacementListener;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.framework.multiblock.EnumMultiblock;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

/**
 * Small 2 block tall self contained launcher for small missiles.
 * Created by robert on 3/28/2015.
 */
public class TileSmallSilo extends TileAbstractLauncher implements ISimpleItemRenderer, IPostInit, IPlacementListener, IDestroyedListener
{
    public static HashMap<IPos3D, String> tileMapCache = new HashMap();

    static
    {
        tileMapCache.put(new Pos(0, 1, 0), EnumMultiblock.TILE.getTileName());
        tileMapCache.put(new Pos(0, 2, 0), EnumMultiblock.TILE.getTileName());
    }

    public TileSmallSilo()
    {
        //super("smallsilo", Material.iron);
        //this.itemBlock = ItemBlockSmallSilo.class;
        //this.isOpaque = false;
        //this.renderNormalBlock = false;
        //this.renderTileEntity = true;
    }

    @Override
    protected IInventory createInventory()
    {
        return new TileModuleInventory(this, 1);
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockSmallSilo), "I I", "I I", "CBC", 'I', Blocks.iron_bars, 'B', OreNames.BLOCK_IRON, 'C', UniversalRecipe.CIRCUIT_T1.get()));
    }

    @Override
    public boolean canAcceptMissile(IMissile missile)
    {
        return super.canAcceptMissile(missile) && missile.getMissileSize() == MissileCasings.SMALL.ordinal();
    }

    //public String getInventoryName()
    //{
    //    return "tile.icbm:smallSilo.container";
    //}

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

    //TODO re-implement
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.0935f, pos.zf() + 0.5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_SILO_TEXTURE);
        Assets.SMALL_SILO_MODEL.renderAll();
        GL11.glPopMatrix();

        IMissile missile = getMissile();
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
    public boolean canPlaceOnSide(int side)
    {
        return side == ForgeDirection.UP.ordinal();
    }

    @Override
    public boolean removedByPlayer(EntityPlayer player, boolean willHarvest)
    {
        if (willHarvest && getMissileItem() != null)
        {
            InventoryUtility.dropItemStack(toLocation(), getMissileItem());
            getInventory().setInventorySlotContents(0, null);
        }
        return false;
    }
}
