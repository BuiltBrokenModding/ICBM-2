package icbm.dianqi;

import icbm.ICBMCommon;
import icbm.ZhuYao;
import icbm.api.ICBM;
import icbm.api.IFrequency;

import java.util.List;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import universalelectricity.prefab.ItemElectric;

public class ItHuoLuanQi extends ItemElectric implements IFrequency
{
	public ItHuoLuanQi(String name, int par1, int par2)
	{
		super(par1, ZhuYao.TAB);
		this.iconIndex = par2;
		this.setItemName(name);
	}

	@Override
	public String getTextureFile()
	{
		return ICBM.ITEM_TEXTURE_FILE;
	}

	/**
	 * Allows items to add custom lines of
	 * information to the mouseover description
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
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);

		if (this.getJoules(par1ItemStack) > 1)
		{
			this.onUse(10, par1ItemStack);
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		par3EntityPlayer.openGui(ZhuYao.instance, ICBMCommon.GUI_FREQUENCY, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
		return par1ItemStack;
	}

	@Override
	public double getVoltage()
	{
		return 25;
	}

	@Override
	public double getMaxJoules(Object... data)
	{
		return 100000;
	}
}
