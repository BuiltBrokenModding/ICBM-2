package icbm.explosion.dianqi;

import icbm.api.IFrequency;
import icbm.explosion.CommonProxy;
import icbm.explosion.ZhuYao;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.prefab.ItemElectric;

public class ItHuoLuanQi extends ItemElectric implements IFrequency
{
	public ItHuoLuanQi(int par1, int par2)
	{
		super(par1);
		this.iconIndex = par2;
		this.setItemName("signalDisrupter");
		this.setCreativeTab(ZhuYao.TAB);
	}

	@Override
	public String getTextureFile()
	{
		return ZhuYao.ITEM_TEXTURE_FILE;
	}

	/**
	 * Allows items to add custom lines of information to the mouseover description
	 */
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		super.addInformation(itemStack, par2EntityPlayer, par3List, par4);
		par3List.add("Frequency: " + this.getFrequency(itemStack));
	}

	@Override
	public short getFrequency(Object... data)
	{
		ItemStack itemStack = (ItemStack) data[0];
		if (itemStack.stackTagCompound == null) { return 0; }
		return itemStack.stackTagCompound.getShort("frequency");
	}

	@Override
	public void setFrequency(short frequency, Object... data)
	{
		ItemStack itemStack = (ItemStack) data[0];

		if (itemStack.stackTagCompound == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		itemStack.stackTagCompound.setShort("frequency", frequency);
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		if (!par2World.isRemote)
		{
			super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);

			if (this.getJoules(par1ItemStack) > 1)
			{
				this.onUse(1, par1ItemStack);
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		par3EntityPlayer.openGui(ZhuYao.instance, CommonProxy.GUI_FREQUENCY, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
		return par1ItemStack;
	}

	@Override
	public double getVoltage(Object... data)
	{
		return 25;
	}

	@Override
	public double getMaxJoules(Object... data)
	{
		return 80000;
	}
}
