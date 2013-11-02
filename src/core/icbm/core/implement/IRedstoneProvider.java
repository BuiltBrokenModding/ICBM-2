package icbm.core.implement;

import net.minecraftforge.common.ForgeDirection;

public interface IRedstoneProvider
{
	public boolean isPoweringTo(ForgeDirection direction);

	public boolean isIndirectlyPoweringTo(ForgeDirection direction);
}
