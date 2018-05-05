package com.builtbroken.icbm.content.launcher.door.listeners;

import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.launcher.door.TileSiloDoor;
import com.builtbroken.icbm.content.launcher.door.json.DoorData;
import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.client.json.IJsonRenderStateProvider;
import com.builtbroken.mc.framework.block.imp.BlockListenerKeys;
import com.builtbroken.mc.framework.block.imp.IBlockListener;
import com.builtbroken.mc.framework.block.imp.ITileEventListener;
import com.builtbroken.mc.framework.block.imp.ITileEventListenerBuilder;
import com.builtbroken.mc.seven.framework.block.listeners.TileListener;
import com.builtbroken.mc.seven.framework.block.listeners.client.ITileRenderListener;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.obj.WavefrontObject;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles rendering the door with rotation
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/1/2018.
 */
public class DoorRenderListener extends TileListener implements IBlockListener, ITileRenderListener, IJsonRenderStateProvider
{
    public static final WavefrontObject DOOR_MODEL = (WavefrontObject) Assets.model("door/siloDoorHatch3x3.obj"); //TODO move to JSON

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(TileEntity tile, double xx, double yy, double zz, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(xx + 0.5, yy + 1, zz + 0.5);
        if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof TileSiloDoor)
        {
            TileSiloDoor door = (TileSiloDoor) ((ITileNodeHost) tile).getTileNode();

            //Render upper moving part of the door
            renderDoor(door, f);

            //TODO render pistons and smaller parts
        }
        GL11.glPopMatrix();
    }

    protected void renderDoor(TileSiloDoor door, float f)
    {
        //Rotate to direction
        ForgeDirection rotation = door.getDirection();
        switch (rotation)
        {
            case NORTH:
                GL11.glRotatef(90, 0, 1, 0);
                break;
            case SOUTH:
                GL11.glRotatef(-90, 0, 1, 0);
                break;
            case EAST:
                //Default is good
                break;
            case WEST:
                GL11.glRotatef(-180, 0, 1, 0);
                break;
        }

        door._prevDoorRotation = MathHelper.lerp(door._prevDoorRotation, door.doorRotation, f);

        //Move to hinge
        GL11.glTranslated(1.05, 0, 0); //TODO pull from JSON render

        //Rotate open
        GL11.glRotatef(-door._prevDoorRotation, 0, 0, 1);

        //Render model
        DOOR_MODEL.renderAll(); //TODO pull from JSON render
    }

    @Override
    public List<String> getListenerKeys()
    {
        List<String> list = new ArrayList();
        list.add(BlockListenerKeys.TILE_RENDER);
        list.add(BlockListenerKeys.JSON_RENDER_STATE);
        return list;
    }

    @Override
    public String getRenderContentID(IItemRenderer.ItemRenderType renderType, Object objectBeingRendered)
    {
        if (objectBeingRendered instanceof ITileNodeHost && ((ITileNodeHost) objectBeingRendered).getTileNode() instanceof TileSiloDoor)
        {
            return ((TileSiloDoor) ((ITileNodeHost) objectBeingRendered).getTileNode()).getDoorData().doorRender;
        }
        else if (objectBeingRendered instanceof ItemStack)
        {
            ItemStack stack = (ItemStack) objectBeingRendered;
            String doorID = DoorBlockStackListener.getDoorID(stack);
            if (doorID != null)
            {
                DoorData data = DoorData.getDoorData(doorID);
                if (data != null)
                {
                    return data.doorRender;
                }
            }
        }
        return null;
    }

    @Override
    public List<String> getRenderContentIDs()
    {
        List<String> list = new ArrayList();
        for (DoorData doorData : DoorData.doorMap.values())
        {
            if (doorData.doorRender != null)
            {
                list.add(doorData.doorRender);
            }
        }
        return list;
    }

    public static class Builder implements ITileEventListenerBuilder
    {
        @Override
        public ITileEventListener createListener(Block block)
        {
            return new DoorRenderListener();
        }

        @Override
        public String getListenerKey()
        {
            return "icbm:SiloDoorRenderListener";
        }
    }
}
