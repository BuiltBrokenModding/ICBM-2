package icbm.sentry.greatersecurity.block;

import icbm.core.prefab.TileICBM;
import net.minecraft.entity.player.EntityPlayer;
import universalelectricity.api.energy.EnergyStorageHandler;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.prefab.tile.IRedstoneReceptor;

public class TileLaserGate extends TileICBM implements IBlockActivate, IRedstoneReceptor {

	public TileLaserGate() {
		super();
		setEnergyHandler(new EnergyStorageHandler(10000, 100));
	}

	@Override
	public boolean onActivated(EntityPlayer arg0) {
		return false;
	}

	@Override
	public void onPowerOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPowerOn() {
		// TODO Auto-generated method stub
		
	}	
}
