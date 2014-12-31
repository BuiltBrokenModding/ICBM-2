package com.builtbroken.icbm.content.display;

import com.builtbroken.icbm.api.ICustomMissileRender;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.missile.ItemMissile;
import com.builtbroken.icbm.content.missile.RenderMissile;
import cpw.mods.fml.client.FMLClientHandler;
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
import org.lwjgl.opengl.GL11;
import resonant.engine.ResonantEngine;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.prefab.tile.TileAdvanced;
import resonant.lib.transform.region.Cuboid;
import resonant.lib.transform.vector.Vector3;

/**
 * Simple display table to test to make sure missiles are rendering correctly
 * Later will be changed to only render micro and small missiles
 * Created by robert on 12/31/2014.
 */
public class TileMissileDisplay extends TileAdvanced implements IPacketReceiver
{
    protected Missile missile = null;

    public TileMissileDisplay()
    {
        super(Material.circuits);
        this.setRenderStaticBlock(true);
        this.normalRender(false);
        this.setForceItemToRenderAsBlock(true);
        this.bounds(new Cuboid(0, 0, 0, 1, .4, 1));
        this.isOpaqueCube(false);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("missileItem"))
        {
            missile = MissileModuleBuilder.INSTANCE.buildMissile(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("missileItem")));
        }
        else
        {
            missile = null;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (missile != null)
        {
            nbt.setTag("missileItem", missile.toStack().writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public PacketTile getDescPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new PacketTile(this, tag);
    }

    public void updateClient()
    {
        ResonantEngine.instance.packetHandler.sendToAllAround(getDescPacket(), (TileEntity) this);
    }

    @Override
    public void read(ByteBuf buf, EntityPlayer player, PacketType packet)
    {
        System.out.println("Packet Received");
        readFromNBT(ByteBufUtils.readTag(buf));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Vector3 pos, float frame, int pass)
    {
        if (this.getWorldObj() != null)
        {
            if (missile != null)
            {
                GL11.glPushMatrix();
                GL11.glTranslated(pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5);
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                float yaw = 0;
                float pitch = 90;

                GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(pitch, 0.0F, 0.0F, 1.0F);

                if (!(missile instanceof ICustomMissileRender) || !((ICustomMissileRender) missile).renderMissileInWorld())
                {
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderMissile.SMALL_TEXTURE);
                    if (missile.getWarhead() != null)
                    {
                        RenderMissile.SMALL.renderOnly("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
                    }
                    RenderMissile.SMALL.renderAllExcept("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
                }
                GL11.glPopMatrix();
            }
        }
    }

    @Override
    public boolean use(EntityPlayer player, int side, Vector3 hit)
    {
        if (server())
        {
            ItemStack stack = player.getHeldItem();
            if (stack == null)
            {
                if (missile != null)
                {
                    player.addChatComponentMessage(new ChatComponentText("Removed Missile"));
                    player.inventory.mainInventory[player.inventory.currentItem] = missile.toStack();
                    missile = null;
                    player.inventoryContainer.detectAndSendChanges();
                    updateClient();
                    return true;
                }
            } //TODO support non-ICBM missiles
            else if (stack.getItem() instanceof ItemMissile)
            {
                player.addChatComponentMessage(new ChatComponentText("Added Missile"));
                missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
                if (!player.capabilities.isCreativeMode)
                {
                    stack.stackSize--;
                    if (stack.stackSize <= 0)
                    {
                        player.inventory.mainInventory[player.inventory.currentItem] = null;
                    }
                    player.inventoryContainer.detectAndSendChanges();
                    updateClient();
                }
                return true;
            }
        }
        return false;
    }
}
