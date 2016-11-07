package com.builtbroken.icbm.content.crafting.station.small;

import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
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
        if (isClient())
        {
            if (id == 1)
            {
                this.rotation = ForgeDirection.getOrientation(buf.readByte());
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
            else if (id == 5)
            {
                this.rotation = ForgeDirection.getOrientation(Math.min(0, Math.max(buf.readByte(), 5)));
                this.bounds = null;
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
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(SharedAssets.TOOL_TABLE_TEXTURE);
        SharedAssets.TOOL_TABLE.renderAll();
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
    public static void renderMissile(Pos pos, IMissile misssile, ForgeDirection connectedBlockSide, ForgeDirection direction)
    {
        ///Center render view to tile center
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
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
        //Bind texture
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);
        //Group_001 body
        //Component_1_001 - 4 Body Fins
        if (misssile.getWarhead() != null)
        {
            //Group_004 nose of warhead
            //Group_005 warhead
            Assets.SMALL_MISSILE_MODEL.renderOnly("Group_005");
            if (misssile.getWarhead().getExplosive() != null)
            {
                Assets.SMALL_MISSILE_MODEL.renderOnly("Group_004");
            }
        }
        if (misssile.getEngine() != null)
        {
            //Group_002 - Engine thruster
            //Group_003 - Engine case
            //Component_3_001 - 8 Engine lower segments
            //Component_2_001 - 4 Engine fins
            Assets.SMALL_MISSILE_MODEL.renderOnly("Group_002", "Group_003");
            for (int i = 1; i < 9; i++)
            {
                Assets.SMALL_MISSILE_MODEL.renderOnly("Component_3_00" + i);
            }
            for (int i = 1; i < 5; i++)
            {
                Assets.SMALL_MISSILE_MODEL.renderOnly("Component_2_00" + i);
            }
        }
        if (misssile.getGuidance() != null)
        {
            //TODO add model indication showing no guidance added
        }
        //Render body and fins
        Assets.SMALL_MISSILE_MODEL.renderOnly("Group_001", "Component_1_001", "Component_1_002", "Component_1_003", "Component_1_004");
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
                    if (rotation == ForgeDirection.EAST || rotation == ForgeDirection.WEST)
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
                    if (rotation == ForgeDirection.DOWN || rotation == ForgeDirection.UP)
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
                    if (rotation == ForgeDirection.DOWN || rotation == ForgeDirection.UP)
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
