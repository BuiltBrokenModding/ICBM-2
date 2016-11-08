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
    public static IIcon main;
    //TODO replace with tree array
    public static IIcon[][] arrow;

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
            if(side == getAttachedDirection().ordinal())
            {
                final int arrowIndex = getArrowIndex();
                if (isUnloadRail())
                {
                    return arrow[arrowIndex][!rotateClockwise ? 5 : 6];
                }
                else if (isLoaderRail())
                {
                    return arrow[arrowIndex][!rotateClockwise ? 7 : 8];
                }
                else if (isStopRail())
                {
                    return stopCarts ? arrow[arrowIndex][3] : arrow[arrowIndex][4];
                }
                else if (isRotationRail())
                {
                    return rotateClockwise ? arrow[arrowIndex][1] : arrow[arrowIndex][2];
                }
                return arrow[arrowIndex][0];
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

    /** Gets the arrow direction index */
    public final int getArrowIndex()
    {
        if (getAttachedDirection() == ForgeDirection.UP || getAttachedDirection() == ForgeDirection.DOWN)
        {
            if (getFacingDirection() == ForgeDirection.NORTH)
            {
                return 0;
            }
            else if (getFacingDirection() == ForgeDirection.EAST)
            {
                return 3;
            }
            else if (getFacingDirection() == ForgeDirection.SOUTH)
            {
                return 1;
            }
            else if (getFacingDirection() == ForgeDirection.WEST)
            {
                return 2;
            }
        }
        else if (getAttachedDirection() == ForgeDirection.EAST)
        {
            if (getFacingDirection() == ForgeDirection.NORTH)
            {
                return 3;
            }
            else if (getFacingDirection() == ForgeDirection.UP)
            {
                return 0;
            }
            else if (getFacingDirection() == ForgeDirection.SOUTH)
            {
                return 2;
            }
            else if (getFacingDirection() == ForgeDirection.DOWN)
            {
                return 1;
            }
        }
        else if (getAttachedDirection() == ForgeDirection.WEST)
        {
            if (getFacingDirection() == ForgeDirection.NORTH)
            {
                return 2;
            }
            else if (getFacingDirection() == ForgeDirection.UP)
            {
                return 0;
            }
            else if (getFacingDirection() == ForgeDirection.SOUTH)
            {
                return 3;
            }
            else if (getFacingDirection() == ForgeDirection.DOWN)
            {
                return 1;
            }
        }
        else if (getAttachedDirection() == ForgeDirection.NORTH)
        {
            if (getFacingDirection() == ForgeDirection.EAST)
            {
                return 2;
            }
            else if (getFacingDirection() == ForgeDirection.UP)
            {
                return 0;
            }
            else if (getFacingDirection() == ForgeDirection.WEST)
            {
                return 3;
            }
            else if (getFacingDirection() == ForgeDirection.DOWN)
            {
                return 1;
            }
        }
        else if (getAttachedDirection() == ForgeDirection.SOUTH)
        {
            if (getFacingDirection() == ForgeDirection.EAST)
            {
                return 3;
            }
            else if (getFacingDirection() == ForgeDirection.UP)
            {
                return 0;
            }
            else if (getFacingDirection() == ForgeDirection.WEST)
            {
                return 2;
            }
            else if (getFacingDirection() == ForgeDirection.DOWN)
            {
                return 1;
            }
        }
        return 0;
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
