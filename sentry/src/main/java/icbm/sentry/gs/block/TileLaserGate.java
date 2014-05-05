package icbm.sentry.gs.block;

import icbm.core.prefab.TileICBM;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.prefab.tile.IRedstoneReceptor;

public class TileLaserGate extends TileICBM implements IBlockActivate, IRedstoneReceptor {

	private TileLaserGate pairedGate;
	
	public TileLaserGate() {
		super();
		setEnergyHandler(new EnergyStorageHandler(10000, 100));
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!worldObj.isRemote) {
			
		}
	}
	
	@Override
	public boolean onActivated(EntityPlayer player) {
		return false;
	}

	@Override
	public void onPowerOff() {
		// Stop Drawing Guff
	}

	@Override
	public void onPowerOn() {
		if(!worldObj.isRemote) {
			
		}
	}	
}
