package com.builtbroken.icbm.content.rail.powered;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/30/2016.
 */
public class TilePowerRailClient extends TilePowerRail
{
    private static IIcon main;
    //TODO replace with tree array
    private static IIcon[][] arrow;

    @Override
    public Tile newTile()
    {
        return new TilePowerRailClient();
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        markRender();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        //TODO implement animate textures for arrows
        //      One arrow blinks at a time sorta like a run way
        main = iconRegister.registerIcon(ICBM.PREFIX + "PowerRailBody");
        final String[] sideName = new String[]{"Up", "Down", "Left", "Right"};
        final String[] textureNames = new String[]{"", "Clockwise", "AClockwise", "Stop", "Go", "LoaderL", "LoaderR", "UnloaderL", "UnloaderR"};

        arrow = new IIcon[4][];
        for (int i = 0; i < sideName.length; i++)
        {
            arrow[i] = new IIcon[textureNames.length];
            for (int y = 0; y < textureNames.length; y++)
            {
                arrow[i][y] = iconRegister.registerIcon(ICBM.PREFIX + "PowerRailArrow" + sideName[i] + textureNames[y]);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        //As complex as this looks
        //IT only runs once per block update or block change
        if (this != block.staticTile)
        {
            if (side == getAttachedDirection().ordinal())
            {
                if (getAttachedDirection() == ForgeDirection.UP)
                {
                    if (getFacingDirection() == ForgeDirection.NORTH)
                    {
                        return isUnloadRail() ? arrow[0][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[0][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[0][3] : arrow[0][4] : isPoweredRail() ? arrow[0][0] : !rotateClockwise ? arrow[0][1] : arrow[0][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.EAST)
                    {
                        return isUnloadRail() ? arrow[3][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[3][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[3][3] : arrow[3][4] : isPoweredRail() ? arrow[3][0] : !rotateClockwise ? arrow[3][1] : arrow[3][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.SOUTH)
                    {
                        return isUnloadRail() ? arrow[1][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[1][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[1][3] : arrow[1][4] : isPoweredRail() ? arrow[1][0] : !rotateClockwise ? arrow[1][1] : arrow[1][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.WEST)
                    {
                        return isUnloadRail() ? arrow[2][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[2][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[2][3] : arrow[2][4] : isPoweredRail() ? arrow[2][0] : !rotateClockwise ? arrow[2][1] : arrow[2][2];
                    }
                }
                else if (getAttachedDirection() == ForgeDirection.DOWN)
                {
                    if (getFacingDirection() == ForgeDirection.NORTH)
                    {
                        return isUnloadRail() ? arrow[0][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[0][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[0][3] : arrow[0][4] : isPoweredRail() ? arrow[0][0] : !rotateClockwise ? arrow[0][1] : arrow[0][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.WEST)
                    {
                        return isUnloadRail() ? arrow[2][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[2][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[2][3] : arrow[2][4] : isPoweredRail() ? arrow[2][0] : !rotateClockwise ? arrow[2][1] : arrow[2][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.SOUTH)
                    {
                        return isUnloadRail() ? arrow[1][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[1][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[1][3] : arrow[1][4] : isPoweredRail() ? arrow[1][0] : !rotateClockwise ? arrow[1][1] : arrow[1][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.EAST)
                    {
                        return isUnloadRail() ? arrow[3][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[3][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[3][3] : arrow[3][4] : isPoweredRail() ? arrow[3][0] : !rotateClockwise ? arrow[3][1] : arrow[3][2];
                    }
                }
                else if (getAttachedDirection() == ForgeDirection.EAST)
                {
                    if (getFacingDirection() == ForgeDirection.NORTH)
                    {
                        return isUnloadRail() ? arrow[3][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[3][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[3][3] : arrow[3][4] : isPoweredRail() ? arrow[3][0] : !rotateClockwise ? arrow[3][1] : arrow[3][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.UP)
                    {
                        return isUnloadRail() ? arrow[0][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[0][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[0][3] : arrow[0][4] : isPoweredRail() ? arrow[0][0] : !rotateClockwise ? arrow[0][1] : arrow[0][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.SOUTH)
                    {
                        return isUnloadRail() ? arrow[2][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[2][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[2][3] : arrow[2][4] : isPoweredRail() ? arrow[2][0] : !rotateClockwise ? arrow[2][1] : arrow[2][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.DOWN)
                    {
                        return isUnloadRail() ? arrow[1][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[1][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[1][3] : arrow[1][4] : isPoweredRail() ? arrow[1][0] : !rotateClockwise ? arrow[1][1] : arrow[1][2];
                    }
                }
                else if (getAttachedDirection() == ForgeDirection.WEST)
                {
                    if (getFacingDirection() == ForgeDirection.NORTH)
                    {
                        return isUnloadRail() ? arrow[2][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[2][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[2][3] : arrow[2][4] : isPoweredRail() ? arrow[2][0] : !rotateClockwise ? arrow[2][1] : arrow[2][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.UP)
                    {
                        return isUnloadRail() ? arrow[0][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[0][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[0][3] : arrow[0][4] : isPoweredRail() ? arrow[0][0] : !rotateClockwise ? arrow[0][1] : arrow[0][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.SOUTH)
                    {
                        return isUnloadRail() ? arrow[3][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[3][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[3][3] : arrow[3][4] : isPoweredRail() ? arrow[3][0] : !rotateClockwise ? arrow[3][1] : arrow[3][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.DOWN)
                    {
                        return isUnloadRail() ? arrow[1][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[1][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[1][3] : arrow[1][4] : isPoweredRail() ? arrow[1][0] : !rotateClockwise ? arrow[1][1] : arrow[1][2];
                    }
                }
                else if (getAttachedDirection() == ForgeDirection.NORTH)
                {
                    if (getFacingDirection() == ForgeDirection.EAST)
                    {
                        return isUnloadRail() ? arrow[2][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[2][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[2][3] : arrow[2][4] : isPoweredRail() ? arrow[2][0] : !rotateClockwise ? arrow[2][1] : arrow[2][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.UP)
                    {
                        return isUnloadRail() ? arrow[0][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[0][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[0][3] : arrow[0][4] : isPoweredRail() ? arrow[0][0] : !rotateClockwise ? arrow[0][1] : arrow[0][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.WEST)
                    {
                        return isUnloadRail() ? arrow[3][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[3][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[3][3] : arrow[3][4] : isPoweredRail() ? arrow[3][0] : !rotateClockwise ? arrow[3][1] : arrow[3][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.DOWN)
                    {
                        return isUnloadRail() ? arrow[1][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[1][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[1][3] : arrow[1][4] : isPoweredRail() ? arrow[1][0] : !rotateClockwise ? arrow[1][1] : arrow[1][2];
                    }
                }
                else if (getAttachedDirection() == ForgeDirection.SOUTH)
                {
                    if (getFacingDirection() == ForgeDirection.WEST)
                    {
                        return isUnloadRail() ? arrow[2][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[2][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[2][3] : arrow[2][4] : isPoweredRail() ? arrow[2][0] : !rotateClockwise ? arrow[2][1] : arrow[2][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.UP)
                    {
                        return isUnloadRail() ? arrow[0][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[0][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[0][3] : arrow[0][4] : isPoweredRail() ? arrow[0][0] : !rotateClockwise ? arrow[0][1] : arrow[0][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.EAST)
                    {
                        return isUnloadRail() ? arrow[3][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[3][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[3][3] : arrow[3][4] : isPoweredRail() ? arrow[3][0] : !rotateClockwise ? arrow[3][1] : arrow[3][2];
                    }
                    else if (getFacingDirection() == ForgeDirection.DOWN)
                    {
                        return isUnloadRail() ? arrow[1][!rotateClockwise ? 5 : 6] : isLoaderRail() ? arrow[1][!rotateClockwise ? 7 : 8] : isStopRail() ? stopCarts ? arrow[1][3] : arrow[1][4] : isPoweredRail() ? arrow[1][0] : !rotateClockwise ? arrow[1][1] : arrow[1][2];
                    }
                }
            }
        }
        //Item renderer
        else if (side == 1)
        {
            final PoweredRails type = PoweredRails.get(meta);
            return type == PoweredRails.UNLOADER ? arrow[3][7] : type == PoweredRails.LOADER ? arrow[3][5] : type == PoweredRails.STOP ? arrow[3][3] : type == PoweredRails.ROTATION ? arrow[3][1] :  /**Powered Rail DEFAULT */arrow[3][0];
        }
        return main;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target)
    {
        return new ItemStack(ICBM.blockPowerRail, 1, railType.ordinal());
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        railType = PoweredRails.get(buf.readInt());
        setFacingDirection(ForgeDirection.getOrientation(buf.readInt()));
        if (isRotationRail())
        {
            rotateToAngle = buf.readBoolean();
            rotateClockwise = buf.readBoolean();
            rotateYaw = buf.readInt();
        }
        else if (isStopRail())
        {
            stopCarts = buf.readBoolean();
        }
        else if (isLoaderRail() || isLoaderRail())
        {
            rotateClockwise = buf.readBoolean();
        }
        world().markBlockRangeForRenderUpdate(xi(), yi(), zi(), xi(), yi(), zi());
    }
}
