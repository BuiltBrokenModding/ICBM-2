package icbm.explosion.items;

import icbm.core.ICBMCore;
import icbm.core.prefab.item.ItemICBMElectrical;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.machines.TileCruiseLauncher;
import icbm.explosion.machines.TileLauncherPrefab;
import icbm.explosion.machines.TileLauncherScreen;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import universalelectricity.api.item.ItemElectric;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.network.IPacketReceiver;
import calclavia.lib.utility.LanguageUtility;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;

public class ItemRadarGun extends ItemICBMElectrical implements IPacketReceiver
{
    public static final int YONG_DIAN_LIANG = 1000;
    public static final int JU_LI = 1000;

    public ItemRadarGun(int id)
    {
        super(id, "radarGun");
    }

    /** Allows items to add custom lines of information to the mouseover description */
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List par3List, boolean par4)
    {
        super.addInformation(itemStack, entityPlayer, par3List, par4);
        Vector3 coord = getLink(itemStack);
        par3List.add("\uaa74" + LanguageUtility.getLocal("info.radarGun.savedCoords"));
        par3List.add(LanguageUtility.getLocal("gui.misc.x") + " " + (int) coord.x + ", " + LanguageUtility.getLocal("gui.misc.y") + " " + (int) coord.y + ", " + LanguageUtility.getLocal("gui.misc.z") + " " + (int) coord.z);
        par3List.add((int) new Vector3(entityPlayer).distance(coord) + " " + LanguageUtility.getLocal("info.radarGun.meters") + " (" + (int) (new Vector3(entityPlayer).x - coord.x) + ", " + (int) (new Vector3(entityPlayer).y - coord.y) + ", " + (int) (new Vector3(entityPlayer).z - coord.z) + ")");
    }

    /** Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
     * world, entityPlayer */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World par2World, EntityPlayer entityPlayer)
    {
        if (par2World.isRemote)
        {
            MovingObjectPosition objectMouseOver = entityPlayer.rayTrace(JU_LI, 1);

            if (objectMouseOver != null)
            {
                TileEntity tileEntity = par2World.getBlockTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

                // Do not scan if the target is a
                // missile launcher
                if (!(tileEntity instanceof TileLauncherPrefab))
                {
                    // Check for electricity
                    if (this.getEnergy(itemStack) > YONG_DIAN_LIANG)
                    {
                        PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_ITEM.getPacket(entityPlayer, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
                        this.discharge(itemStack, YONG_DIAN_LIANG, true);
                        entityPlayer.addChatMessage(LanguageUtility.getLocal("message.radarGun.scanned").replaceAll("%x", "" + objectMouseOver.blockX).replace("%y", "" + objectMouseOver.blockY).replaceAll("%z", "" + objectMouseOver.blockZ).replaceAll("%d", "" + Math.round(new Vector3(entityPlayer).distance(new Vector3(objectMouseOver)))));
                    }
                    else
                    {
                        entityPlayer.addChatMessage(LanguageUtility.getLocal("message.radarGun.nopower"));
                    }
                }
            }
        }

        return itemStack;
    }

    /** Callback for item usage. If the item does something special on right clicking, he will have
     * one of those. Return True if something happen and false if it don't. This is for ITEMS, not
     * BLOCKS ! */
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        int blockId = par3World.getBlockId(x, y, z);
        int blockMetadata = par3World.getBlockMetadata(x, y, z);

        if (blockId == ICBMExplosion.blockMachine.blockID)
        {
            TileEntity tileEntity = par3World.getBlockTileEntity(x, y, z);

            if (tileEntity != null)
            {
                if (tileEntity instanceof TileLauncherScreen)
                {
                    TileLauncherScreen missileLauncher = (TileLauncherScreen) tileEntity;

                    Vector3 savedCords = this.getLink(par1ItemStack);

                    // If the vector is NOT 0
                    if (!savedCords.equals(new Vector3()))
                    {
                        if (missileLauncher.getTarget() == null)
                        {
                            missileLauncher.setTarget(new Vector3());
                        }

                        missileLauncher.getTarget().x = (int) savedCords.x;
                        missileLauncher.getTarget().z = (int) savedCords.z;

                        if (par3World.isRemote)
                        {
                            PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(missileLauncher, 2, savedCords.intX(), missileLauncher.getTarget().intY(), savedCords.intZ()));
                            par2EntityPlayer.addChatMessage(LanguageUtility.getLocal("message.radarGun.transfer"));
                        }
                    }
                    else
                    {
                        if (par3World.isRemote)
                            par2EntityPlayer.addChatMessage(LanguageUtility.getLocal("message.radarGun.noCoords"));
                    }
                }
                else if (tileEntity instanceof TileCruiseLauncher)
                {
                    TileCruiseLauncher missileLauncher = (TileCruiseLauncher) tileEntity;

                    Vector3 savedCords = this.getLink(par1ItemStack);

                    if (!savedCords.equals(new Vector3()))
                    {
                        if (missileLauncher.getTarget() == null)
                        {
                            missileLauncher.setTarget(new Vector3());
                        }

                        missileLauncher.setTarget(savedCords.clone());

                        if (par3World.isRemote)
                        {
                            PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(missileLauncher, 2, savedCords.intX(), missileLauncher.getTarget().intY(), savedCords.intZ()));
                            par2EntityPlayer.addChatMessage(LanguageUtility.getLocal("message.radarGun.transfer"));
                        }
                    }
                    else
                    {
                        if (par3World.isRemote)
                        {
                            par2EntityPlayer.addChatMessage(LanguageUtility.getLocal("message.radarGun.noCoords"));
                        }
                    }
                }
            }
        }

        return false;
    }

    public void setLink(ItemStack itemStack, Vector3 position)
    {
        // Saves the frequency in the ItemStack
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        position.writeToNBT(itemStack.getTagCompound());
    }

    public Vector3 getLink(ItemStack itemStack)
    {
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        return new Vector3(itemStack.getTagCompound());
    }

    @Override
    public long getVoltage(ItemStack itemStack)
    {
        return 50;
    }

    @Override
    public long getEnergyCapacity(ItemStack theItem)
    {
        return 80000;
    }

    @Override
    public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
    {
        ItemStack itemStack = (ItemStack) extra[0];
        this.setLink(itemStack, new Vector3(data.readInt(), data.readInt(), data.readInt()));
        if (ICBMExplosion.itemRadarGun instanceof ItemElectric)
            ((ItemElectric) ICBMExplosion.itemRadarGun).discharge(itemStack, ItemRadarGun.YONG_DIAN_LIANG, true);
    }

}
