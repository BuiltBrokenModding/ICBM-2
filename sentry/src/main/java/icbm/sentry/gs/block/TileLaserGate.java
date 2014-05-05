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
			if(pairedGate == null) {
				Vector3 thisVector = new Vector3(this);
				Vector3 foundVector = null;
				for(int i = 0; i < 4; i = i + 90) {
					MovingObjectPosition mop = thisVector.rayTraceBlocks(worldObj, i, 0, false, 10);
					
					foundVector = new Vector3(mop);
					TileEntity tile = worldObj.getBlockTileEntity(foundVector.intX(), foundVector.intY(), foundVector.intZ());
					if(tile != null) {
						this.pairedGate = (TileLaserGate) tile;
						this.pairedGate.pairedGate = this;
					}
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
		if(!worldObj.isRemote) {
			if(pairedGate == null) {
				System.out.println("Paired Null" + "(X:null, Y:null, Z:null)");
			} else {
				System.out.println("Paired " + pairedGate + "(X:" + pairedGate.xCoord + ", Y:" + pairedGate.yCoord + ", Z:" + pairedGate.zCoord + ")");
			}
		}
		// Draw Guff
	}	
}
