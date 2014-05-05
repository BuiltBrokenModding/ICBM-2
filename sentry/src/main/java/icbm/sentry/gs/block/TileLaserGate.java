package icbm.sentry.gs.block;

import icbm.core.prefab.TileICBM;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.prefab.tile.IRedstoneReceptor;
import calclavia.lib.prefab.tile.IRotatable;

public class TileLaserGate extends TileICBM implements IRotatable, IRedstoneReceptor {

	private TileLaserGate pairedGate;
	private final int LASER_REACH_LENGTH = 10;
	
	public TileLaserGate() {
		super();
		setEnergyHandler(new EnergyStorageHandler(10000, 100));
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!worldObj.isRemote) {
			if(ticks%10==0) {
				if(pairedGate == null) findLaserPair();
			}
		}
	}
	
	@Override
	public void invalidate() {
		if(this.pairedGate != null) this.pairedGate.setLaserPair(null);
		setLaserPair(null);
		
		super.invalidate();
	}
	
	public void setLaserPair(TileLaserGate lsg) {
		this.pairedGate = lsg;
	}
	
	public TileLaserGate getLaserPair() {
		return pairedGate;
	}
	
	public void findLaserPair() {
		for(int i = LASER_REACH_LENGTH; i > 0; i--) {
			Vector3 loc = new Vector3(this).translate(getFacingDirection(), i);
			if(loc.getTileEntity(worldObj) instanceof TileLaserGate && loc.getTileEntity(worldObj) != this) {
				TileLaserGate fence = (TileLaserGate) loc.getTileEntity(worldObj);
	             if (fence.getFacingDirection() == this.getFacingDirection()) {
	            	 setLaserPair(fence);
	            	 fence.setLaserPair(this);
	            	 return;
	             }
			}
		}
	}
	
	public ForgeDirection getFacingDirection() {
		return this.getDirection();
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
