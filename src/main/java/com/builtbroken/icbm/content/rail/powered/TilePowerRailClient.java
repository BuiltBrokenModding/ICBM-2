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
    public IIcon getIcon()
    {
        facingDirection = ForgeDirection.NORTH;

        if (getAttachedDirection() == ForgeDirection.UP)
        {
            if(facingDirection == ForgeDirection.NORTH)
            {
                return arrow1;
            }
        }
        else if (getAttachedDirection() == ForgeDirection.DOWN)
        {

        }
        return main;
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        facingDirection = ForgeDirection.getOrientation(buf.readInt());
    }
}
