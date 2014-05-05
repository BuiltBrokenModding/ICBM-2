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
		if(pairedGate == null) {
			Vector3 thisVector = new Vector3(this);
			Vector3 foundVector = null;
			
			for(int i = 0; i < 4; i += 90) {
				MovingObjectPosition mop = thisVector.rayTraceBlocks(worldObj, i, 0, true, 10);
				switch(i) {
					case 90:
						foundVector = new Vector3(mop).translate(1, 0, 0); break;
					case 180:
						foundVector = new Vector3(mop).translate(0, 0, 1); break;
					case 270:
						foundVector = new Vector3(mop).translate(-1, 0, 0); break;
					case 360:
						foundVector = new Vector3(mop).translate(0, 0, -1); break;
				}
				TileEntity tile = worldObj.getBlockTileEntity(foundVector.intX(), foundVector.intY(), foundVector.intZ());
				if(tile != null) {
					this.pairedGate = (TileLaserGate) tile;
					this.pairedGate.pairedGate = this;
				}
			}
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
		System.out.println("Paired " + pairedGate + "(X:" + pairedGate.xCoord + ", Y:" + pairedGate.yCoord + ", Z:" + pairedGate.zCoord + ")");
		// Draw Guff
	}	
}
