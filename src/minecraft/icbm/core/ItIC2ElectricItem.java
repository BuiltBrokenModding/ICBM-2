package icbm.core;

import ic2.api.ICustomElectricItem;
import net.minecraft.item.ItemStack;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.ItemElectric;

public abstract class ItIC2ElectricItem extends ItemElectric implements ICustomElectricItem
{
	public static final float CHARGE_RATE = 0.005f;

	public ItIC2ElectricItem(int id)
	{
		super(id);
	}

	@Override
	public boolean canProvideEnergy()
	{
		return this.canProduceElectricity();
	}

	@Override
	public int getChargedItemId()
	{
		return 0;
	}

	@Override
	public int getEmptyItemId()
	{
		return 0;
	}

	@Override
	public int getMaxCharge()
	{
		return (int) (this.getMaxJoules() * UniversalElectricity.TO_IC2_RATIO);
	}

	@Override
	public int getTier()
	{
		return 0;
	}

	@Override
	public int getTransferLimit()
	{
		return (int) ((this.getMaxJoules() * CHARGE_RATE) * UniversalElectricity.TO_IC2_RATIO);
	}

	@Override
	public int charge(ItemStack itemStack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate)
	{
		double inputElectricity = amount * UniversalElectricity.IC2_RATIO;

		inputElectricity = Math.min(inputElectricity, this.getMaxJoules(itemStack) - this.getJoules(itemStack));

		if (!ignoreTransferLimit)
		{
			inputElectricity = Math.min(inputElectricity, this.getMaxJoules(itemStack) * CHARGE_RATE);
		}

		if (!simulate)
		{
			this.setJoules(this.getJoules(itemStack) + inputElectricity, itemStack);
		}

		return (int) (inputElectricity * UniversalElectricity.TO_IC2_RATIO);
	}

	@Override
	public int discharge(ItemStack itemStack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate)
	{
		double outputElectricity = amount * UniversalElectricity.IC2_RATIO;

		outputElectricity = Math.min(outputElectricity, this.getJoules(itemStack));

		if (!ignoreTransferLimit)
		{
			outputElectricity = Math.min(this.getJoules(itemStack), this.getMaxJoules(itemStack) * CHARGE_RATE);
		}

		if (!simulate)
		{
			this.setJoules(this.getJoules(itemStack) - outputElectricity, itemStack);
		}

		return (int) (outputElectricity * UniversalElectricity.TO_IC2_RATIO);
	}

	@Override
	public boolean canUse(ItemStack itemStack, int amount)
	{
		return false;
	}

	@Override
	public boolean canShowChargeToolTip(ItemStack itemStack)
	{
		return false;
	}

}
