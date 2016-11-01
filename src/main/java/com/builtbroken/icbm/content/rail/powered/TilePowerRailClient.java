package com.builtbroken.icbm.content.rail.powered;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/30/2016.
 */
public class TilePowerRailClient extends TilePowerRail
{
    private static IIcon main;
    private static IIcon arrow1;
    private static IIcon arrow2;
    private static IIcon arrow3;
    private static IIcon arrow4;

    @Override
    public Tile newTile()
    {
        return new TilePowerRailClient();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        main = iconRegister.registerIcon(ICBM.PREFIX + "PowerRailBody");

        arrow1 = iconRegister.registerIcon(ICBM.PREFIX + "PowerRailArrowUp");
        arrow2 = iconRegister.registerIcon(ICBM.PREFIX + "PowerRailArrowDown");
        arrow3 = iconRegister.registerIcon(ICBM.PREFIX + "PowerRailArrowLeft");
        arrow4 = iconRegister.registerIcon(ICBM.PREFIX + "PowerRailArrowRight");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (this != block.staticTile)
        {
            if (side == getAttachedDirection().ordinal())
            {
                if (getAttachedDirection() == ForgeDirection.UP)
                {
                    if (getFacingDirection() == ForgeDirection.NORTH)
                    {
                        return arrow1;
                    }
                    else if (getFacingDirection() == ForgeDirection.EAST)
                    {
                        return arrow4;
                    }
                    else if (getFacingDirection() == ForgeDirection.SOUTH)
                    {
                        return arrow2;
                    }
                    else if (getFacingDirection() == ForgeDirection.WEST)
                    {
                        return arrow3;
                    }
                }
                else if (getAttachedDirection() == ForgeDirection.DOWN)
                {
                    if (getFacingDirection() == ForgeDirection.NORTH)
                    {
                        return arrow1;
                    }
                    else if (getFacingDirection() == ForgeDirection.EAST)
                    {
                        return arrow3;
                    }
                    else if (getFacingDirection() == ForgeDirection.SOUTH)
                    {
                        return arrow2;
                    }
                    else if (getFacingDirection() == ForgeDirection.WEST)
                    {
                        return arrow4;
                    }
                }
                else if (getAttachedDirection() == ForgeDirection.EAST)
                {
                    if (getFacingDirection() == ForgeDirection.NORTH)
                    {
                        return arrow4;
                    }
                    else if (getFacingDirection() == ForgeDirection.UP)
                    {
                        return arrow1;
                    }
                    else if (getFacingDirection() == ForgeDirection.SOUTH)
                    {
                        return arrow3;
                    }
                    else if (getFacingDirection() == ForgeDirection.DOWN)
                    {
                        return arrow2;
                    }
                }
                else if (getAttachedDirection() == ForgeDirection.WEST)
                {
                    if (getFacingDirection() == ForgeDirection.NORTH)
                    {
                        return arrow3;
                    }
                    else if (getFacingDirection() == ForgeDirection.UP)
                    {
                        return arrow1;
                    }
                    else if (getFacingDirection() == ForgeDirection.SOUTH)
                    {
                        return arrow4;
                    }
                    else if (getFacingDirection() == ForgeDirection.DOWN)
                    {
                        return arrow2;
                    }
                }
                else if (getAttachedDirection() == ForgeDirection.NORTH)
                {
                    if (getFacingDirection() == ForgeDirection.EAST)
                    {
                        return arrow3;
                    }
                    else if (getFacingDirection() == ForgeDirection.UP)
                    {
                        return arrow1;
                    }
                    else if (getFacingDirection() == ForgeDirection.WEST)
                    {
                        return arrow4;
                    }
                    else if (getFacingDirection() == ForgeDirection.DOWN)
                    {
                        return arrow2;
                    }
                }
                else if (getAttachedDirection() == ForgeDirection.SOUTH)
                {
                    if (getFacingDirection() == ForgeDirection.WEST)
                    {
                        return arrow3;
                    }
                    else if (getFacingDirection() == ForgeDirection.UP)
                    {
                        return arrow1;
                    }
                    else if (getFacingDirection() == ForgeDirection.EAST)
                    {
                        return arrow4;
                    }
                    else if (getFacingDirection() == ForgeDirection.DOWN)
                    {
                        return arrow2;
                    }
                }
            }
        }
        return main;
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        setFacingDirection(ForgeDirection.getOrientation(buf.readInt()));
    }
}
