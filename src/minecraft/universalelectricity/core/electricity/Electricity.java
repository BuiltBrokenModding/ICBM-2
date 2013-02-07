package universalelectricity.core.electricity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.implement.IConductor;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.common.FMLLog;

/**
 * The Electricity Network Manager.
 * 
 * @author Calclavia
 * 
 */
public class Electricity
{
	public static Electricity instance = new Electricity();

	private List<ElectricityNetwork> electricityNetworks = new ArrayList<ElectricityNetwork>();

	/**
	 * Registers a conductor into the UE electricity net.
	 */
	public void registerConductor(IConductor newConductor)
	{
		this.cleanUpNetworks();
		ElectricityNetwork newNetwork = new ElectricityNetwork(newConductor);
		this.electricityNetworks.add(newNetwork);
	}

	public void unregister(TileEntity tileEntity)
	{
		for (ElectricityNetwork network : this.electricityNetworks)
		{
			network.stopProducing(tileEntity);
			network.stopRequesting(tileEntity);
		}
	}

	/**
	 * Merges two connection lines together into one.
	 * 
	 * @param networkA - The network to be merged into. This network will be kept.
	 * @param networkB - The network to be merged. This network will be deleted.
	 */
	public void mergeConnection(ElectricityNetwork networkA, ElectricityNetwork networkB)
	{
		if (networkA != networkB)
		{
			if (networkA != null && networkB != null)
			{
				networkA.conductors.addAll(networkB.conductors);
				networkA.setNetwork();
				this.electricityNetworks.remove(networkB);
				networkB = null;

				networkA.cleanConductors();
			}
			else
			{
				System.err.println("Failed to merge Universal Electricity wire connections!");
			}
		}
	}

	/**
	 * Separate one connection line into two different ones between two conductors. This function
	 * does this by resetting all wires in the connection line and making them each reconnect.
	 * 
	 * @param conductorA - existing conductor
	 * @param conductorB - broken/invalid conductor
	 */
	public void splitConnection(IConductor conductorA, IConductor conductorB)
	{
		try
		{
			ElectricityNetwork network = conductorA.getNetwork();

			if (network != null)
			{
				network.cleanConductors();
				network.resetConductors();

				Iterator it = network.conductors.iterator();

				while (it.hasNext())
				{
					IConductor conductor = (IConductor) it.next();

					for (byte i = 0; i < 6; i++)
					{
						conductor.updateConnectionWithoutSplit(Vector3.getConnectorFromSide(((TileEntity) conductor).worldObj, new Vector3((TileEntity) conductor), ForgeDirection.getOrientation(i)), ForgeDirection.getOrientation(i));
					}
				}
			}
			else
			{
				FMLLog.severe("Conductor invalid network while splitting connection!");
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to split wire connection!");
			e.printStackTrace();
		}
	}

	/**
	 * Clean up and remove all useless and invalid connections.
	 */
	public void cleanUpNetworks()
	{
		try
		{
			Iterator it = electricityNetworks.iterator();

			while (it.hasNext())
			{
				ElectricityNetwork network = (ElectricityNetwork) it.next();
				network.cleanConductors();

				if (network.conductors.size() == 0)
				{
					it.remove();
				}
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to clean up wire connections!");
			e.printStackTrace();
		}
	}

	public void resetConductors()
	{
		Iterator it = electricityNetworks.iterator();

		while (it.hasNext())
		{
			ElectricityNetwork network = ((ElectricityNetwork) it.next());
			network.resetConductors();
		}
	}
}
