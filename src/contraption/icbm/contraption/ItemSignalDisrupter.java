package icbm.contraption;

import icbm.api.IItemFrequency;
import icbm.core.base.ItemICBMElectricBase;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemSignalDisrupter extends ItemICBMElectricBase implements IItemFrequency
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
	public void onUpdate(ItemStack itemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		if (!par2World.isRemote)
		{
			if (this.getElectricityStored(itemStack) > 20 && par2World.getWorldTime() % 20 == 0)
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
	public float getVoltage(ItemStack itemStack)
	{
		return 25;
	}

	@Override
	public float getMaxElectricityStored(ItemStack itemStack)
	{
		return 80000;
	}
}
