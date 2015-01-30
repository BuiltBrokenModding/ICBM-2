package com.builtbroken.icbm.content.display;

import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.missile.ItemMissile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketReceiver;
import com.builtbroken.mc.core.network.packet.AbstractPacket;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.lib.render.RenderItemOverlayUtility;
import com.builtbroken.mc.lib.transform.region.Cuboid;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Simple display table to test to make sure missiles are rendering correctly
 * Later will be changed to only render micro and small missiles
 * Created by robert on 12/31/2014.
 */
public class TileMissileDisplay extends TileMissileContainer
{
    private Missile missile = null;

    public TileMissileDisplay()
    {
        super("missileDisplay", Material.circuits);
        this.renderTileEntity = true;
        this.isOpaque = true;
        this.bounds = new Cuboid(0, 0, 0, 1, .4, 1);
    }

    @Override
    public Tile newTile()
    {
        return new TileMissileDisplay();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        if (this.getWorldObj() != null && getMissile() != null)
        {
            RenderItemOverlayUtility.renderItem(getWorldObj(), ForgeDirection.UNKNOWN, missile.toStack(), pos.add(0.5), 0, 0);
        }
    }

}
