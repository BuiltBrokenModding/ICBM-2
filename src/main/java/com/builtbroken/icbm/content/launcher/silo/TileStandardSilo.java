package com.builtbroken.icbm.content.launcher.silo;

import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.tile.listeners.IDestroyedListener;
import com.builtbroken.mc.api.tile.listeners.IPlacementListener;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * 7 block tall silo tube that requires a user to build a casing to contain the missile.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/23/2016.
 */
public class TileStandardSilo extends TileAbstractLauncher implements ISimpleItemRenderer, IPlacementListener, IDestroyedListener
{
    public TileStandardSilo()
    {
        //super("standardsilo", Material.iron);
        //this.itemBlock = ItemBlockStandardSilo.class;
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
    public boolean canAcceptMissile(IMissile missile)
    {
        return super.canAcceptMissile(missile) && missile.getMissileSize() == MissileCasings.STANDARD.ordinal();
    }


    //public String getInventoryName()
    //{
    //  return "tile.icbm:standardSilo.container";
    //}

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glScaled(.8f, .8f, .8f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_SILO_TEXTURE);
        GL11.glColor3f(1.0f, 0f, 0f);
        Assets.SMALL_SILO_MODEL.renderOnly("Group_001", "Component_1_001");
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new Cube(0, 0, 0, 1, 7, 1).add(x(), y(), z()).toAABB();
    }

    //TODO implement render in json
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf(), pos.yf(), pos.zf() + 1);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.STANDARD_SILO_TEXTURE);
        Assets.STANDARD_SILO_MODEL.renderAll();
        GL11.glPopMatrix();

        //Render missile
        if (getMissile() != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 2.75f, pos.zf() + 0.5f);
            //TODO rotate
            if (getMissile() instanceof ICustomMissileRender)
            {
                ((ICustomMissileRender) getMissile()).renderMissileInWorld(0, 0, frame);
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
