package com.builtbroken.icbm.content.debug;

import com.builtbroken.icbm.content.fragments.EntityFragment;
import com.builtbroken.icbm.content.fragments.FragmentType;
import com.builtbroken.icbm.content.fragments.RenderFragment;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.mc.lib.render.fx.FxBeam;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/4/2017.
 */
public class BlockEntityDisplay extends BlockContainer
{
    public BlockEntityDisplay()
    {
        super(Material.iron);
        setBlockUnbreakable();
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityDisplay();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float side_x, float side_y, float side_z)
    {
        if (player.getHeldItem() != null)
        {
            if (world.isRemote)
            {
                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile instanceof TileEntityDisplay)
                {
                    ((TileEntityDisplay) tile).stack = player.getHeldItem().copy();
                    ((TileEntityDisplay) tile).stack.stackSize = 1;
                    player.addChatComponentMessage(new ChatComponentText("Display set to " + ((TileEntityDisplay) tile).stack.getDisplayName()));
                }
            }
            return true;
        }
        return false;
    }

    public static class TileEntityDisplay extends TileEntity
    {
        ItemStack stack;
    }

    public static class TESRDisplay extends TileEntitySpecialRenderer
    {
        EntityFragment fragment;

        @Override
        public void renderTileEntityAt(TileEntity tile, double xx, double yy, double zz, float p_147500_8_)
        {
            try
            {
                if (tile instanceof TileEntityDisplay)
                {
                    if (fragment == null)
                    {
                        fragment = new EntityFragment(null);
                        fragment.setFragmentType(FragmentType.PROJECTILE);
                    }
                    fragment.worldObj = tile.getWorldObj();
                    fragment.posX = tile.xCoord + 0.5;
                    fragment.posY = tile.yCoord + 2;
                    fragment.posZ = tile.zCoord + 0.5;
                    fragment.setFragmentMaterial(((TileEntityDisplay) tile).stack.copy());
                    fragment.rotationYaw += 5;
                    if (fragment.rotationYaw >= 360)
                    {
                        fragment.rotationYaw = 0;
                        fragment.rotationPitch += 5;
                        if (fragment.rotationPitch >= 360)
                        {
                            fragment.rotationPitch = 0;
                        }
                    }

                    Pos pos = new Pos(tile);
                    FxBeam laser = new FxBeam(SharedAssets.LASER_TEXTURE, tile.getWorldObj(), pos.add(0.5, 1.1, 0.5), pos.add(0.5, 2, 0.5), Color.RED, 10);
                    FMLClientHandler.instance().getClient().effectRenderer.addEffect(laser);

                    RenderFragment render = (RenderFragment) RenderManager.instance.getEntityClassRenderObject(EntityFragment.class);
                    if (render != null)
                    {
                        GL11.glPushMatrix();
                        RenderUtility.disableLighting();
                        render.doRender(fragment, xx + 0.5, yy + 2, zz + 0.5, 0, 1);
                        RenderUtility.enableLighting();
                        GL11.glPopMatrix();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
