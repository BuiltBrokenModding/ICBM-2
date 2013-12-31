package icbm.contraption;

import icbm.api.IItemFrequency;
import icbm.core.base.ItemICBMElectricBase;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.builtbroken.minecraft.network.ISimpleItemPacketReceiver;
import com.builtbroken.minecraft.network.ISimplePacketReceiver;
import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

public class ItemSignalDisrupter extends ItemICBMElectricBase implements IItemFrequency, ISimpleItemPacketReceiver
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
        par3List.add("Frequency: " + this.getFrequency(itemStack));
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
        par3EntityPlayer.openGui(ICBMContraption.instance, 0, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
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
    public boolean simplePacket(EntityPlayer player, ItemStack stack, String id, ByteArrayDataInput data)
    {
        if (id.equalsIgnoreCase("freq"))
        {
            if (stack.getItem() instanceof ItemSignalDisrupter)
            {
                ((ItemSignalDisrupter) stack.getItem()).setFrequency(data.readShort(), stack);
            }
            return true;
        }
        return false;
    }
}
