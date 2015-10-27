package com.builtbroken.icbm.content.crafting.station;

import com.builtbroken.icbm.content.Assets;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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

    @Override
    public Tile newTile()
    {
        return new TileSmallMissileWorkstationClient();
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (isClient())
        {
            if (id == 1)
            {
                this.rotation = ForgeDirection.getOrientation(buf.readByte());
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
                worldObj.markBlockForUpdate(xi(), yi(), zi());
                return true;
            }
        }
        return false;
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glScaled(.7f, .7f, .7f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        Assets.SMALL_MISSILE_STATION_MODEL.renderAll();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.53f, pos.yf(), pos.zf() + 0.5f);
        GL11.glRotated(90, 0, 1, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);

        //Keep in mind the directions are of the facing block
        switch (connectedBlockSide)
        {
            case UP:
                if (getDirection() == ForgeDirection.WEST || getDirection() == ForgeDirection.EAST)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    //x y z
                    GL11.glTranslatef(-0.0255f, 0f, 0.033f);
                }
                break;
            case DOWN:
                GL11.glRotated(180, 1, 0, 0);
                // z y x
                GL11.glTranslatef(0.005f, -1f, 0.055f);
                if (getDirection() == ForgeDirection.WEST || getDirection() == ForgeDirection.EAST)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    //x y z
                    GL11.glTranslatef(-0.0255f, 0f, 0.033f);
                }
                break;
            case EAST:
                GL11.glRotated(90, 1, 0, 0);
                // z x y
                GL11.glTranslatef(0.007f, -0.536f, -0.475f);
                if (getDirection() == ForgeDirection.UP || getDirection() == ForgeDirection.DOWN)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    // y x z
                    GL11.glTranslatef(-0.02f, 0f, 0.038f);
                }
                break;
            case WEST:
                GL11.glRotated(-90, 1, 0, 0);
                // z x y
                GL11.glTranslatef(0.015f, -0.47f, 0.52f);
                if (getDirection() == ForgeDirection.UP || getDirection() == ForgeDirection.DOWN)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    // y x z
                    GL11.glTranslatef(-0.02f, -0.01f, 0.05f);
                }
                break;
            case NORTH:
                //GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() - 0.1f, pos.zf() + 0.5f);
                GL11.glRotated(90, 0, 1, 0);
                GL11.glRotated(90, 1, 0, 0);
                //GL11.glRotated(1.1, 1, 0, 0);
                // y x z
                GL11.glTranslatef(0.0355f, -0.5f, -0.47f);
                if (getDirection() == ForgeDirection.UP || getDirection() == ForgeDirection.DOWN)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    // y z x
                    GL11.glTranslatef(-0.02f, -0.01f, 0.04f);
                }
                break;
            case SOUTH:
                GL11.glRotated(90, 0, 1, 0);
                GL11.glRotated(-90, 1, 0, 0);
                // x z y
                GL11.glTranslatef(0.0355f, -0.5f, 0.53f);
                if (getDirection() == ForgeDirection.UP || getDirection() == ForgeDirection.DOWN)
                {
                    GL11.glRotated(-90, 0, 1, 0);
                    // y z x
                    GL11.glTranslatef(-0.02f, -0.01f, 0.04f);
                }
                break;

        }
        Assets.SMALL_MISSILE_STATION_MODEL.renderAll();
        GL11.glPopMatrix();

        //render missile
        if (getMissileItem() != null)
        {
            GL11.glPushMatrix();
            renderMissile(pos);
            GL11.glPopMatrix();
        }
    }

    /**
     * Handles rendering of the missile
     *
     * @param pos - offset for render
     */
    private void renderMissile(Pos pos)
    {
        float scale = 1f; //0.1f;
        GL11.glTranslatef(pos.xf() + .04f, pos.yf() - 0.4f, pos.zf() + 0.16f);

        //TODO optimize as it seems each rotation method looks similar to the next
        switch (connectedBlockSide)
        {
            case UP:
            case DOWN:
                handleMissileRotationUD();
                break;
            case EAST:
            case WEST:
                handleMissileRotationEW();
                break;
            case SOUTH:
            case NORTH:
                handleMissileRotationNS();
                break;
        }
        GL11.glScaled(scale, scale, scale);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        //Group_001 body
        //Component_1_001 - 4 Body Fins
        if (getMissile().getWarhead() != null)
        {
            //Group_004 nose of warhead
            //Group_005 warhead
            Assets.SMALL_MISSILE_MODEL_2.renderOnly("Group_005");
            if (getMissile().getWarhead().ex != null)
            {
                Assets.SMALL_MISSILE_MODEL_2.renderOnly("Group_004");
            }
        }
        if (getMissile().getEngine() != null)
        {
            //Group_002 - Engine thruster
            //Group_003 - Engine case
            //Component_3_001 - 8 Engine lower segments
            //Component_2_001 - 4 Engine fins
            Assets.SMALL_MISSILE_MODEL_2.renderOnly("Group_002", "Group_003");
            for (int i = 1; i < 9; i++)
            {
                Assets.SMALL_MISSILE_MODEL_2.renderOnly("Component_3_00" + i);
            }
            for (int i = 1; i < 5; i++)
            {
                Assets.SMALL_MISSILE_MODEL_2.renderOnly("Component_2_00" + i);
            }
        }
        if (getMissile().getGuidance() != null)
        {
            //TODO add model indication showing no guidance added
        }
        //Render body and fins
        Assets.SMALL_MISSILE_MODEL_2.renderOnly("Group_001", "Component_1_001", "Component_1_002", "Component_1_003", "Component_1_004");
    }

    private void handleMissileRotationUD()
    {
        switch (getDirection())
        {
            case EAST:
                GL11.glRotated(-90, 0, 0, 1);
                // y x z
                GL11.glTranslatef(-1.4f, -0.5f, 0.15f);
                break;
            case WEST:
                GL11.glRotated(90, 0, 0, 1);
                // y x z
                GL11.glTranslatef(0.5f, -1.4f, 0.15f);
                break;
            case NORTH:
                GL11.glRotated(90, 1, 0, 0);
                // x z y
                GL11.glTranslatef(0f, -0.6f, -1.2f);
                break;
            case SOUTH:
                GL11.glRotated(-90, 1, 0, 0);
                // x z y
                GL11.glTranslatef(0f, -1.3f, 0.75f);
                break;
        }
    }

    /**
     * Handles rotation for east and west
     */
    private void handleMissileRotationEW()
    {
        switch (getDirection())
        {
            //UP is already done by default
            //EAST and WEST are invalid rotations
            case DOWN:
                GL11.glRotated(180, 0, 0, 1);
                // ? -y ?
                GL11.glTranslatef(0f, -1.8f, 0f);
                break;
            case NORTH:
                GL11.glRotated(-90, 1, 0, 0);
                // x -z y
                GL11.glTranslatef(.15f, -0.9f, 0.7f);
                break;
            case SOUTH:
                GL11.glRotated(90, 1, 0, 0);
                // x z y
                GL11.glTranslatef(0.15f, -0.9f, -1.1f);
                break;
        }
    }

    /**
     * Handles rotation for north and south
     */
    private void handleMissileRotationNS()
    {
        switch (getDirection())
        {
            //UP is already done by default
            //NORTH and SOUTH are invalid rotations
            case DOWN:
                GL11.glRotated(180, 0, 0, 1);
                // ? -y ?
                GL11.glTranslatef( 0f, -1.8f, 0f);
                break;
            case EAST:
                GL11.glRotated(-90, 0, 0, 1);
                //-y x z
                GL11.glTranslatef(-1.35f, -0.9f, 0f);
                break;
            case WEST:
                GL11.glRotated(90, 0, 0, 1);
                //y -x z
                GL11.glTranslatef(0.45f, -0.9f, 0f);
                break;
        }
    }
}
