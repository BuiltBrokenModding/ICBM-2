package icbm.explosion.items;

import icbm.Reference;
import icbm.Settings;
import icbm.core.ICBMCore;
import icbm.core.prefab.item.ItemICBMElectrical;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.entities.EntityLightBeam;
import icbm.explosion.machines.launcher.TileLauncherPrefab;
import icbm.explosion.machines.launcher.TileLauncherScreen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import resonant.api.blocks.IBlockFrequency;
import resonant.api.items.IItemFrequency;
import resonant.lib.network.IPacketReceiver;
import resonant.lib.utility.LanguageUtility;
import universalelectricity.api.item.ItemElectric;
import universalelectricity.api.vector.Vector3;
import calclavia.api.mffs.fortron.FrequencyGrid;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;

public class ItemLaserDesignator extends ItemICBMElectrical implements IItemFrequency, IPacketReceiver
{
    public static final int BAN_JING = Settings.DAO_DAN_ZUI_YUAN;
    public static final int energyCost = 1000000;

    public ItemLaserDesignator(int id)
    {
        super(id, "laserDesignator");
    }

    /** Allows items to add custom lines of information to the mouseover description */
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        super.addInformation(itemStack, par2EntityPlayer, par3List, par4);

        if (this.getFrequency(itemStack) > 0)
        {
            par3List.add(LanguageUtility.getLocal("info.misc.freq") + " " + getFrequency(itemStack));
        }
        else
        {
            par3List.add(LanguageUtility.getLocal("info.designator.noFreq"));
        }
    }

    @Override
    public int getFrequency(ItemStack itemStack)
    {
        if (itemStack.stackTagCompound == null)
        {
            return 0;
        }
        return itemStack.stackTagCompound.getInteger("frequency");
    }

    @Override
    public void setFrequency(int frequency, ItemStack itemStack)
    {
        if (itemStack.stackTagCompound == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        itemStack.stackTagCompound.setInteger("frequency", frequency);
    }

    public int getLauncherCountDown(ItemStack par1ItemStack)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            return -1;
        }

        return par1ItemStack.stackTagCompound.getInteger("countDown");
    }

    public void setLauncherCountDown(ItemStack par1ItemStack, int value)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            par1ItemStack.setTagCompound(new NBTTagCompound());
        }

        par1ItemStack.stackTagCompound.setInteger("countDown", value);
    }

    public int getLauncherCount(ItemStack par1ItemStack)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            return 0;
        }
        return par1ItemStack.stackTagCompound.getInteger("launcherCount");
    }

    public void setLauncherCount(ItemStack par1ItemStack, int value)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            par1ItemStack.setTagCompound(new NBTTagCompound());
        }

        par1ItemStack.stackTagCompound.setInteger("launcherCount", value);
    }

    public int getLauncherDelay(ItemStack par1ItemStack)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            return 0;
        }
        return par1ItemStack.stackTagCompound.getInteger("launcherDelay");
    }

    public void setLauncherDelay(ItemStack par1ItemStack, int value)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            par1ItemStack.setTagCompound(new NBTTagCompound());
        }

        par1ItemStack.stackTagCompound.setInteger("launcherDelay", value);
    }

    /** Called each tick as long the item is on a player inventory. Uses by maps to check if is on a
     * player hand and update it's contents. */
    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity par3Entity, int par4, boolean par5)
    {
        super.onUpdate(itemStack, world, par3Entity, par4, par5);

        if (!world.isRemote)
        {
            List<TileLauncherPrefab> connectedLaunchers = new ArrayList<TileLauncherPrefab>();

            if (this.getLauncherCountDown(itemStack) > 0 || this.getLauncherCount(itemStack) > 0)
            {
                Vector3 position = new Vector3(par3Entity.posX, par3Entity.posY, par3Entity.posZ);

                for (IBlockFrequency blockFrequency : FrequencyGrid.instance().get(world, position, ItemLaserDesignator.BAN_JING, this.getFrequency(itemStack)))
                {
                    if (blockFrequency instanceof TileLauncherPrefab)
                    {
                        // Do airstrike!
                        TileLauncherPrefab missileLauncher = (TileLauncherPrefab) blockFrequency;

                        if (missileLauncher.canLaunch())
                        {
                            connectedLaunchers.add(missileLauncher);
                        }
                    }
                }
            }

            if (this.getLauncherCountDown(itemStack) > 0 && connectedLaunchers.size() > 0)
            {
                if (this.getLauncherCountDown(itemStack) % 20 == 0)
                {
                    ((EntityPlayer) par3Entity).addChatMessage(LanguageUtility.getLocal("message.designator.callTime") + " " + (int) Math.floor(this.getLauncherCountDown(itemStack) / 20));
                }

                if (this.getLauncherCountDown(itemStack) == 1)
                {
                    this.setLauncherCount(itemStack, connectedLaunchers.size());
                    this.setLauncherDelay(itemStack, 0);
                    ((EntityPlayer) par3Entity).addChatMessage(LanguageUtility.getLocal("message.designator.blast"));
                }

                this.setLauncherCountDown(itemStack, this.getLauncherCountDown(itemStack) - 1);
            }

            if (this.getLauncherCount(itemStack) > 0 && this.getLauncherCount(itemStack) <= connectedLaunchers.size() && connectedLaunchers.size() > 0)
            {
                // Launch a missile every two seconds from different launchers
                if (this.getLauncherDelay(itemStack) % 40 == 0)
                {
                    connectedLaunchers.get(this.getLauncherCount(itemStack) - 1).launch();
                    this.setLauncherCount(itemStack, this.getLauncherCount(itemStack) - 1);
                }

                if (this.getLauncherCount(itemStack) == 0)
                {
                    this.setLauncherDelay(itemStack, 0);
                    connectedLaunchers.clear();
                }

                this.setLauncherDelay(itemStack, this.getLauncherDelay(itemStack) + 1);
            }
        }
    }

    /** Callback for item usage. If the item does something special on right clicking, he will have
     * one of those. Return True if something happen and false if it don't. This is for ITEMS, not
     * BLOCKS ! */
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        if (!par3World.isRemote)
        {
            // SET FREQUENCY OF REMOTE
            TileEntity tileEntity = par3World.getBlockTileEntity(x, y, z);

            if (tileEntity != null)
            {
                if (tileEntity instanceof TileLauncherPrefab)
                {
                    TileLauncherPrefab missileLauncher = (TileLauncherPrefab) tileEntity;

                    if (missileLauncher.getFrequency() > 0)
                    {
                        this.setFrequency(missileLauncher.getFrequency(), par1ItemStack);
                        par2EntityPlayer.addChatMessage(LanguageUtility.getLocal("message.designator.setFreq") + " " + this.getFrequency(par1ItemStack));
                    }
                    else
                    {
                        par2EntityPlayer.addChatMessage(LanguageUtility.getLocal("message.designator.failFreq"));
                    }
                }
            }
        }

        return false;
    }

    /** Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
     * world, entityPlayer */
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            MovingObjectPosition objectMouseOver = player.rayTrace(BAN_JING * 2, 1);

            if (objectMouseOver != null && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
            {
                int blockId = world.getBlockId(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
                int blockMetadata = world.getBlockMetadata(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

                if (this.getLauncherCountDown(par1ItemStack) > 0)
                {
                    return par1ItemStack;
                }

                /*
                 * Prevents calling air strike if the user is trying to set the frequency of the
                 * remote.
                 */
                if (blockId == ICBMExplosion.blockMachine.blockID)
                {
                    return par1ItemStack;
                }
                else
                {
                    int airStrikeFreq = this.getFrequency(par1ItemStack);

                    // Check if it is possible to do an air strike.
                    if (airStrikeFreq > 0)
                    {
                        if (this.getEnergy(par1ItemStack) >= energyCost)
                        {
                            Vector3 position = new Vector3(player.posX, player.posY, player.posZ);

                            boolean doAirStrike = false;
                            int errorCount = 0;

                            for (IBlockFrequency blockFrequency : FrequencyGrid.instance().get(world, position, ItemLaserDesignator.BAN_JING, airStrikeFreq))
                            {
                                if (blockFrequency instanceof TileLauncherPrefab)
                                {
                                    // Do airstrike!
                                    TileLauncherPrefab missileLauncher = (TileLauncherPrefab) blockFrequency;

                                    double yHit = objectMouseOver.blockY;

                                    if (missileLauncher instanceof TileLauncherScreen)
                                    {
                                        if (missileLauncher.getTarget() != null)
                                            yHit = missileLauncher.getTarget().y;
                                        else
                                            yHit = 0;
                                    }

                                    missileLauncher.setTarget(new Vector3(objectMouseOver.blockX, yHit, objectMouseOver.blockZ));
                                    PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(missileLauncher, 2, missileLauncher.getTarget().intX(), missileLauncher.getTarget().intY(), missileLauncher.getTarget().intZ()));

                                    if (missileLauncher.canLaunch())
                                    {
                                        doAirStrike = true;
                                    }
                                    else
                                    {
                                        errorCount++;
                                        player.addChatMessage("#" + errorCount + " Missile Launcher Error: " + missileLauncher.getStatus());
                                    }
                                }
                            }

                            if (doAirStrike && this.getLauncherCountDown(par1ItemStack) >= 0)
                            {
                                PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_ITEM.getPacket(player, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
                                player.addChatMessage(LanguageUtility.getLocal("message.designator.callBlast"));
                            }
                        }
                        else
                        {
                            player.addChatMessage(LanguageUtility.getLocal("message.designator.nopower"));
                        }
                    }
                    else
                    {
                        player.addChatMessage(LanguageUtility.getLocal("message.designator.noFreq"));
                    }
                }
            }
        }

        return par1ItemStack;
    }

    @Override
    public long getVoltage(ItemStack itemStack)
    {
        return 30;
    }

    @Override
    public long getEnergyCapacity(ItemStack itemStack)
    {
        return energyCost * 10;
    }

    @Override
    public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
    {
        ItemStack itemStack = (ItemStack) extra[0];
        Vector3 position = new Vector3(data.readInt(), data.readInt(), data.readInt());

        ((ItemLaserDesignator) ICBMExplosion.itemLaserDesignator).setLauncherCountDown(itemStack, 119);

        player.worldObj.playSoundEffect(position.intX(), player.worldObj.getHeightValue(position.intX(), position.intZ()), position.intZ(), Reference.PREFIX + "airstrike", 5.0F, (1.0F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

        player.worldObj.spawnEntityInWorld(new EntityLightBeam(player.worldObj, position, 5 * 20, 0F, 1F, 0F));
        if (ICBMExplosion.itemRadarGun instanceof ItemElectric)
            ((ItemElectric) ICBMExplosion.itemRadarGun).discharge(itemStack, ItemLaserDesignator.energyCost, true);
    }
}
