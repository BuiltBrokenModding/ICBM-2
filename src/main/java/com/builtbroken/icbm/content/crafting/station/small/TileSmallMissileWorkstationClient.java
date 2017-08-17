package com.builtbroken.icbm.content.crafting.station.small;

import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.missile.client.RenderMissile;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.mc.client.json.imp.IModelState;
import com.builtbroken.mc.client.json.imp.IRenderState;
import com.builtbroken.mc.client.json.render.RenderData;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.json.imp.IJsonGenObject;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Client side implementation of the small missile workstation, designed to reduce code in a single class. As well this may
 * reduce how much memory is used server side.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/20/2015.
 */
public class TileSmallMissileWorkstationClient extends TileSmallMissileWorkstation implements ISimpleItemRenderer
{
    private AxisAlignedBB bounds;

    @Override
    public Tile newTile()
    {
        return new TileSmallMissileWorkstationClient();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.iron_block.getIcon(0, 0);
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (isClient() && worldObj != null)
        {
            if (id == 1)
            {
                this.facing = ForgeDirection.getOrientation(buf.readByte());
                this.bounds = null;
                ItemStack stack = ByteBufUtils.readItemStack(buf);
                if (InventoryUtility.stacksMatch(stack, new ItemStack(Items.apple)))
                {
                    this.setInventorySlotContents(INPUT_SLOT, null);
                }
                else
                {
                    this.setInventorySlotContents(INPUT_SLOT, stack);
                }
                worldObj.markBlockForUpdate(xi(), yi(), zi());
                return true;
            }
        }
        return false;
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glTranslatef(-1f, 0f, 0.1f);
        GL11.glRotatef(-20f, 0, 1, 0);
        GL11.glScaled(.7f, .7f, .7f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_WORKSTATION_TEXTURE2);
        Assets.SMALL_MISSILE_STATION_MODEL2.renderAll();
        //TODO add work table
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        renderDynamic(pos, new Pos(1, 0, 2), connectedBlockSide, getDirection(), getMissile(), frame, pass);
    }

    @SideOnly(Side.CLIENT)
    public static void renderDynamic(Pos pos, Pos offset, ForgeDirection connectedBlockSide, ForgeDirection direction, IMissile missile, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + offset.xf(), pos.yf() + offset.yf(), pos.zf() + offset.zf());
        GL11.glRotated(90, 0, 1, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_WORKSTATION_TEXTURE2);

        int tableX = 2;
        //Keep in mind the directions are of the facing block
        switch (connectedBlockSide)
        {
            case UP:
                if (direction == ForgeDirection.EAST)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    //x y z
                    GL11.glTranslatef(-2, 0f, -1);
                }
                else if (direction == ForgeDirection.WEST)
                {
                    GL11.glRotated(90, 0, 1, 0);
                    //x y z
                    GL11.glTranslatef(-1, 0f, 2);
                }
                else if (direction == ForgeDirection.SOUTH)
                {
                    GL11.glRotated(180, 0, 1, 0);
                    //x y z
                    GL11.glTranslatef(-3, 0f, 1);
                }
                break;
            case DOWN:
                GL11.glRotated(180, 1, 0, 0);
                // z y x
                GL11.glTranslatef(0f, -1f, 1f);
                if (direction == ForgeDirection.WEST)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    //x y z
                    GL11.glTranslatef(-2f, 0f, -1f);
                }
                else if (direction == ForgeDirection.EAST)
                {
                    GL11.glRotated(90, 0, 1, 0);
                    //x y z
                    GL11.glTranslatef(-1f, 0f, 2f);
                }
                else if (direction == ForgeDirection.SOUTH)
                {
                    GL11.glRotated(180, 0, 1, 0);
                    //x y z
                    GL11.glTranslatef(-3, 0f, 1);
                }
                break;
            case EAST:
                GL11.glRotated(90, 1, 0, 0);
                // z x y
                GL11.glTranslatef(0f, -1f, 0f);
                if (direction == ForgeDirection.DOWN)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    // y x z
                    GL11.glTranslatef(-2f, 0f, -1f);
                }
                else if (direction == ForgeDirection.UP)
                {
                    GL11.glRotated(90, 0, 1, 0);
                    // y x z
                    GL11.glTranslatef(-1f, 0f, 2f);
                }
                if (direction == ForgeDirection.SOUTH)
                {
                    GL11.glRotated(180, 0, 1, 0);
                    // y x z
                    GL11.glTranslatef(-3f, 0f, 1f);
                }
                break;
            case WEST:
                GL11.glRotated(-90, 1, 0, 0);
                // z x y
                if (direction == ForgeDirection.UP)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    // y x z
                    GL11.glTranslatef(-1f, 0f, -1f);
                }
                else if (direction == ForgeDirection.DOWN)
                {
                    GL11.glRotated(90, 0, 1, 0);
                    // y x z
                    GL11.glTranslatef(-2f, 0f, 2f);
                }
                else
                {
                    GL11.glTranslatef(0f, 0f, 1f);
                    if (direction == ForgeDirection.SOUTH)
                    {
                        GL11.glRotated(180, 0, 1, 0);
                        // y x z
                        GL11.glTranslatef(-3f, 0f, 1f);
                    }
                }
                break;
            case NORTH:
                GL11.glRotated(90, 0, 1, 0);
                GL11.glRotated(90, 1, 0, 0);
                // y x z
                GL11.glTranslatef(-1f, 1f, 0f);
                if (direction == ForgeDirection.DOWN)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    // y z x
                    GL11.glTranslatef(-2f, 0f, -1f);
                }
                else if (direction == ForgeDirection.EAST)
                {
                    GL11.glRotated(180, 0, 1, 0);
                    GL11.glTranslatef(-3f, 0f, 1f);
                }
                else if (direction == ForgeDirection.UP)
                {
                    GL11.glRotated(90, 0, 1, 0);
                    // y z x
                    GL11.glTranslatef(-1f, 0f, 2f);
                }
                break;
            case SOUTH:
                GL11.glRotated(90, 0, 1, 0);
                GL11.glRotated(-90, 1, 0, 0);
                // x z y
                GL11.glTranslatef(-1f, -2f, 1f);
                if (direction == ForgeDirection.UP)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    // y z x
                    GL11.glTranslatef(-2f, 0f, -1f);
                }
                else if (direction == ForgeDirection.EAST)
                {
                    GL11.glRotated(180, 0, 1, 0);
                    // x z y
                    GL11.glTranslatef(-3f, 0, 1f);
                }
                else if (direction == ForgeDirection.DOWN)
                {
                    GL11.glRotated(90, 0, 1, 0);
                    // y z x
                    GL11.glTranslatef(-1f, 0f, 2f);
                }
                break;

        }
        Assets.SMALL_MISSILE_STATION_MODEL2.renderAll();
        GL11.glTranslatef(tableX, 0, 0);
        //FMLClientHandler.instance().getClient().renderEngine.bindTexture(SharedAssets.TOOL_TABLE_TEXTURE);
        //SharedAssets.TOOL_TABLE.renderAll();
        GL11.glPopMatrix();

        //render missile
        if (missile != null)
        {
            GL11.glPushMatrix();
            renderMissile(pos, missile, connectedBlockSide, direction);
            GL11.glPopMatrix();
        }
    }

    /**
     * Handles rendering of the missile
     *
     * @param pos - offset for render
     */
    public static void renderMissile(Pos pos, IMissile missmissileile, ForgeDirection connectedBlockSide, ForgeDirection direction)
    {
        renderMissile(pos.xf(), pos.yf(), pos.zf(), missmissileile, connectedBlockSide, direction);
    }

    /**
     * Handles rendering of the missile
     */
    public static void renderMissile(float xx, float yy, float zz, IMissile missile, ForgeDirection connectedBlockSide, ForgeDirection direction)
    {
        ///Center render view to tile center
        GL11.glTranslatef(xx + 0.5f, yy + 0.5f, zz + 0.5f);
        //Slight scale of the lenght to fit on the table
        GL11.glScaled(1, .9, 1);

        //Handles setting the rotation based on the side
        switch (connectedBlockSide)
        {
            case UP:
            case DOWN:
                handleMissileRotationUD(direction);
                break;
            case EAST:
            case WEST:
                handleMissileRotationEW(direction);
                break;
            case SOUTH:
            case NORTH:
                handleMissileRotationNS(direction);
                break;
        }
        //Moves the missile slightly over to sit on its rack
        GL11.glTranslatef(0, -0.4f, 0);
        if (missile instanceof IJsonGenObject)
        {
            RenderData data = ClientDataHandler.INSTANCE.getRenderData(((IJsonGenObject) missile).getContentID());
            if (data != null)
            {
                List<String> parts = new ArrayList();
                parts.add("missile.body");
                if (missile.getWarhead() != null)
                {
                    parts.add("missile.warhead");
                    if (missile.getWarhead().getExplosive() != null)
                    {
                        parts.add("missile.warhead.ex");
                    }
                }
                if (missile.getGuidance() != null)
                {
                    parts.add("missile.guidance");
                }
                if (missile.getEngine() != null)
                {
                    parts.add("missile.engine");
                }
                for (String stateID : parts)
                {
                    IRenderState state = data.getState(stateID);
                    if (state instanceof IModelState)
                    {
                        ((IModelState) state).render(false, 0, 0, 0);
                        //TODO implement part based rendering so each module has a chance to render custom
                    }
                }
            }
        }
        else
        {
            RenderMissile.renderMissile(missile, 0, 0, null);
        }
    }

    public static void handleMissileRotationUD(ForgeDirection direction)
    {
        switch (direction)
        {
            case EAST:
                GL11.glRotated(-90, 0, 0, 1);
                // y x z
                //GL11.glTranslatef(-1.4f, -0.5f, 0.15f);
                break;
            case WEST:
                GL11.glRotated(90, 0, 0, 1);
                // y x z
                //GL11.glTranslatef(0.5f, -1.4f, 0.15f);
                break;
            case SOUTH:
                GL11.glRotated(90, 1, 0, 0);
                // x z y
                //GL11.glTranslatef(0f, -0.6f, -1.2f);
                break;
            case NORTH:
                GL11.glRotated(-90, 1, 0, 0);
                // x z y
                //GL11.glTranslatef(0f, -1.3f, 0.75f);
                break;
        }
    }

    /**
     * Handles rotation for east and west
     */
    public static void handleMissileRotationEW(ForgeDirection direction)
    {
        switch (direction)
        {
            //UP is already done by default
            //EAST and WEST are invalid rotations
            case UP:
                //GL11.glTranslatef(0f, -0.1f, .08f);
                break;
            case DOWN:
                GL11.glRotated(180, 0, 0, 1);
                // ? -y ?
                //GL11.glTranslatef(-1f, -1.8f, .15f);
                break;
            case NORTH:
                GL11.glRotated(-90, 1, 0, 0);
                // x -z y
                //GL11.glTranslatef(.15f, -1.3f, 0.7f);
                break;
            case SOUTH:
                GL11.glRotated(90, 1, 0, 0);
                // x z y
                //GL11.glTranslatef(0.1f, -0.55f, -1.1f);
                break;
        }
    }

    /**
     * Handles rotation for north and south
     */
    public static void handleMissileRotationNS(ForgeDirection direction)
    {
        switch (direction)
        {
            //UP is already done by default
            //NORTH and SOUTH are invalid rotations
            case DOWN:
                GL11.glRotated(180, 0, 0, 1);
                break;
            case EAST:
                GL11.glRotated(-90, 0, 0, 1);
                break;
            case WEST:
                GL11.glRotated(90, 0, 0, 1);
                break;
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        if (bounds == null)
        {
            switch (connectedBlockSide)
            {
                case UP:
                case DOWN:
                    if (facing == ForgeDirection.EAST || facing == ForgeDirection.WEST)
                    {
                        bounds = new Cube(-1, 0, 0, 1, 0, 0).add(toPos()).toAABB();
                    }
                    else
                    {
                        bounds = new Cube(0, 0, -1, 0, 0, 1).add(toPos()).toAABB();
                    }
                    break;
                case EAST:
                case WEST:
                    if (facing == ForgeDirection.DOWN || facing == ForgeDirection.UP)
                    {
                        bounds = new Cube(0, -1, 0, 0, 1, 0).add(toPos()).toAABB();
                    }
                    else
                    {
                        bounds = new Cube(0, 0, -1, 0, 0, 1).add(toPos()).toAABB();
                    }
                    break;
                case NORTH:
                case SOUTH:
                    if (facing == ForgeDirection.DOWN || facing == ForgeDirection.UP)
                    {
                        bounds = new Cube(0, -1, 0, 0, 1, 0).add(toPos()).toAABB();
                    }
                    else
                    {
                        bounds = new Cube(0, 0, -1, 0, 0, 1).add(toPos()).toAABB();
                    }
                    break;
            }
            bounds = super.getRenderBoundingBox();
        }
        return bounds;
    }
}
