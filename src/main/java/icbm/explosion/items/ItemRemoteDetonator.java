package icbm.explosion.items;

import icbm.core.prefab.item.ItemICBMElectrical;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.TileExplosive;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import resonant.engine.ResonantEngine;
import resonant.lib.network.discriminator.PacketPlayerItem;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.utility.LanguageUtility;

public class ItemRemoteDetonator extends ItemICBMElectrical implements IPacketReceiver
{
    public static final int BAN_JING = 100;
    public static final int ENERGY = 1500;

    public ItemRemoteDetonator()
    {
        super("remoteDetonator");
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List par3List, boolean par4)
    {
        Vector3 coord = getSavedCoord(itemStack);

        if (this.nengZha(coord.getTileEntity(player.worldObj)))
        {
            par3List.add("\uaa74" + LanguageUtility.getLocal("info.detonator.linked"));
            par3List.add(LanguageUtility.getLocal("gui.misc.x") + " " + (int) coord.x() + ", " + LanguageUtility.getLocal("gui.misc.y") + " " + (int) coord.y() + ", " + LanguageUtility.getLocal("gui.misc.z") + " " + (int) coord.z());
        }
        else
        {
            par3List.add("\u00a74" + LanguageUtility.getLocal("info.detonator.noLink"));
        }
		super.addInformation(itemStack, player, par3List, par4);
    }

    /** Lock the remote to an explosive if it exists. */
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (entityPlayer.isSneaking() && tileEntity != null)
        {
            if (this.nengZha(tileEntity))
            {
                // Check for electricity
                if (this.getEnergy(itemStack) > ENERGY)
                {
                    this.setSavedCoords(itemStack, new Vector3(x, y, z));
                    this.discharge(itemStack, ENERGY, true);
                    if (world.isRemote)
                    {
                        entityPlayer.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("message.detonator.locked").replaceAll("%x", "" + x).replace("%y", "" + y).replace("%z", "" + z)));
                    }
                }
                else if (world.isRemote)
                {
                    entityPlayer.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("message.detonator.nopower")));
                }

                return true;
            }
        }

        return false;
    }

    /** Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
     * world, entityPlayer */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            if (!player.isSneaking())
            {
                MovingObjectPosition objectMouseOver = player.rayTrace(BAN_JING, 1);

                if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {

                    TileEntity tileEntity = world.getTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
                    Block blockID = world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
                    TileEntity tile = world.getTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

                    if (tile != null)
                    {
                        if (tile instanceof TileExplosive)
                        {
                            if (blockID == ICBMExplosion.blockMachine)
                            {
                                return itemStack;
                            }
                            else if (this.nengZha(tileEntity))
                            {
                                // Check for electricity
                                if (this.getEnergy(itemStack) > ENERGY)
                                {
                                    ResonantEngine.instance.packetHandler.sendToServer(new PacketTile(tileEntity, (byte) 2));
                                    return itemStack;
                                }
                                else
                                {
                                    player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("message.detonator.nopower")));
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                if (this.getEnergy(itemStack) > ENERGY)
                {
                    TileEntity tileEntity = this.getSavedCoord(itemStack).getTileEntity(world);

                    if (this.nengZha(tileEntity))
                    {
                        // Blow it up
                        ResonantEngine.instance.packetHandler.sendToServer(new PacketTile(tileEntity, (byte) 2));
                        // Use Energy
                        ResonantEngine.instance.packetHandler.sendToServer(new PacketPlayerItem(player));
                    }
                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("message.detonator.nopower")));
                }
            }
        }

        return itemStack;
    }

    public boolean nengZha(TileEntity tileEntity)
    {
        if (tileEntity != null)
        {
            if (tileEntity instanceof TileExplosive)
            {
                return ((TileExplosive) tileEntity).explosiveID == Explosive.condensed.getID() || ((TileExplosive) tileEntity).explosiveID == Explosive.breaching.getID() || ((TileExplosive) tileEntity).explosiveID == Explosive.sMine.getID();
            }
        }

        return false;
    }

    public void setSavedCoords(ItemStack itemStack, Vector3 position)
    {
        if (itemStack.stackTagCompound == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        itemStack.stackTagCompound.setInteger("x", position.xi());
        itemStack.stackTagCompound.setInteger("y", position.yi());
        itemStack.stackTagCompound.setInteger("z", position.zi());
    }

    public Vector3 getSavedCoord(ItemStack par1ItemStack)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            return new Vector3();
        }

        return new Vector3(par1ItemStack.stackTagCompound.getInteger("x"), par1ItemStack.stackTagCompound.getInteger("y"), par1ItemStack.stackTagCompound.getInteger("z"));
    }

    @Override
    public double getEnergyCapacity(ItemStack theItem)
    {
        return 50000000;
    }

    @Override
    public double getVoltage(ItemStack itemStack)
    {
        return 80;
    }

    @Override
    public void read(ByteBuf data, EntityPlayer player, PacketType type)
    {
        if(type instanceof PacketPlayerItem)
        {
            ItemStack itemStack = player.inventory.getStackInSlot(((PacketPlayerItem)type).slotId);
            this.discharge(itemStack, ENERGY, true);
        }
    }
}
