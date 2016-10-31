package com.builtbroken.icbm.content.rail;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/30/2016.
 */
public class TilePowerRailClient extends TilePowerRail
{
    @Override
    public Tile newTile()
    {
        return new TilePowerRailClient();
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        icons = new HashMap();
        icons.put("main", iconRegister.registerIcon(ICBM.PREFIX + "PowerRailBody"));

        icons.put("PowerRailArrow1", iconRegister.registerIcon(ICBM.PREFIX + "PowerRailArrow1"));
        icons.put("PowerRailArrow1", iconRegister.registerIcon(ICBM.PREFIX + "PowerRailArrow2"));
        icons.put("PowerRailArrow1", iconRegister.registerIcon(ICBM.PREFIX + "PowerRailArrow3"));
        icons.put("PowerRailArrow1", iconRegister.registerIcon(ICBM.PREFIX + "PowerRailArrow4"));
    }

    private void clearSideIcons()
    {
        HashMap<String, IIcon> icons = ((TilePowerRailClient) getTileBlock().staticTile).icons;
        for (int i = 0; i < 6; i++)
        {
            for (int z = 0; z < 4; z++)
            {
                icons.remove(i + "" + z);
            }
        }
    }

    private void setupSideIcons()
    {
        HashMap<String, IIcon> icons = ((TilePowerRailClient) getTileBlock().staticTile).icons;
        IIcon icon1 = icons.get("PowerRailArrow2");
        IIcon icon2 = icons.get("PowerRailArrow2");
        IIcon icon3 = icons.get("PowerRailArrow3");
        IIcon icon4 = icons.get("PowerRailArrow4");
        icons.put("00", icon1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected IIcon getSideIcon(int m, int s)
    {
        if (m == s)
        {
            return icons.get(m + "" + s);
        }
        return icons.get("main");
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        attachedSide = ForgeDirection.getOrientation(buf.readInt());
        facingDirection = ForgeDirection.getOrientation(buf.readInt());
    }
}
