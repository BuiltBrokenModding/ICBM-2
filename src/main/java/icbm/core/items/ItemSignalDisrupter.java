package icbm.core.items;

import icbm.core.ICBMCore;
import icbm.core.prefab.item.ItemICBMElectrical;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import resonant.api.items.IItemFrequency;
import resonant.lib.network.IPacketReceiver;
import resonant.lib.utility.LanguageUtility;

import com.google.common.io.ByteArrayDataInput;

public class ItemSignalDisrupter extends ItemICBMElectrical implements IItemFrequency, IPacketReceiver
{
    public ItemSignalDisrupter(int id)
    {
        super(id, "signalDisrupter");
    }

    /** Allows items to add custom lines of information to the mouseover description */
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        super.addInformation(itemStack, par2EntityPlayer, par3List, par4);
        par3List.add(LanguageUtility.getLocal("info.misc.freq") + " " + this.getFrequency(itemStack));
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

    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5)
    {
        if (!world.isRemote)
        {
            if (this.getEnergy(itemStack) > 20 && world.getWorldTime() % 20 == 0)
            {
                this.discharge(itemStack, 1 * 20, true);
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {        
        par3EntityPlayer.openGui(ICBMCore.INSTANCE, 0, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
        return par1ItemStack;
    }

    @Override
    public long getVoltage(ItemStack itemStack)
    {
        return 25;
    }

    @Override
    public long getEnergyCapacity(ItemStack itemStack)
    {
        return 80000;
    }

    @Override
    public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
    {
        if (extra == null)
        {
            return;
        }
        
        if (data == null)
        {
            return;
        }
        
        ItemStack itemStack = (ItemStack) extra[0];
        int frequency = data.readInt();
        
        if (itemStack != null)
        {
            Item clientItem = itemStack.getItem();
            
            if (clientItem instanceof ItemSignalDisrupter)
            {
                ((ItemSignalDisrupter) clientItem).setFrequency(frequency, itemStack);
            }
        }
    }
}
