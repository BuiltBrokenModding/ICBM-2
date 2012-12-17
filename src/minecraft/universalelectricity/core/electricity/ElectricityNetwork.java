package universalelectricity.core.electricity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.implement.IConductor;
import cpw.mods.fml.common.FMLLog;

public class ElectricityNetwork
{
	private final HashMap<TileEntity, ElectricityPack> producers = new HashMap<TileEntity, ElectricityPack>();
	private final HashMap<TileEntity, ElectricityPack> consumers = new HashMap<TileEntity, ElectricityPack>();

	public final List<IConductor> conductors = new ArrayList<IConductor>();

	public ElectricityNetwork(IConductor conductor)
	{
		this.addConductor(conductor);
	}

	/**
	 * Sets this tile entity to start producing energy in this network.
	 */
	public void startProducing(TileEntity tileEntity, ElectricityPack electricityPack)
	{
		if (tileEntity != null && electricityPack.getWatts() > 0)
		{
			this.producers.put(tileEntity, electricityPack);
		}
	}

	public void startProducing(TileEntity tileEntity, double amperes, double voltage)
	{
		this.startProducing(tileEntity, new ElectricityPack(amperes, voltage));
	}

	public boolean isProducing(TileEntity tileEntity)
	{
		return this.producers.containsKey(tileEntity);
	}

	/**
	 * Sets this tile entity to stop producing energy in this network.
	 */
	public void stopProducing(TileEntity tileEntity)
	{
		this.producers.remove(tileEntity);
	}

	/**
	 * Sets this tile entity to start producing energy in this network.
	 */
	public void startRequesting(TileEntity tileEntity, ElectricityPack electricityPack)
	{
		if (tileEntity != null && electricityPack.getWatts() > 0)
		{
			this.consumers.put(tileEntity, electricityPack);
		}
	}

	public void startRequesting(TileEntity tileEntity, double amperes, double voltage)
	{
		this.startRequesting(tileEntity, new ElectricityPack(amperes, voltage));
	}

	public boolean isRequesting(TileEntity tileEntity)
	{
		return this.consumers.containsKey(tileEntity);
	}

	/**
	 * Sets this tile entity to stop producing energy in this network.
	 */
	public void stopRequesting(TileEntity tileEntity)
	{
		this.consumers.remove(tileEntity);
	}

	/**
	 * @return The electricity produced in this electricity network
	 */
	public ElectricityPack getProduced()
	{
		ElectricityPack totalElectricity = new ElectricityPack(0, 0);

		Iterator it = this.producers.entrySet().iterator();

		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry) it.next();

			if (pairs != null)
			{
				TileEntity tileEntity = (TileEntity) pairs.getKey();

				if (tileEntity == null)
				{
					it.remove();
					continue;
				}

				if (tileEntity.isInvalid())
				{
					it.remove();
					continue;
				}

				if (tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) != tileEntity)
				{
					it.remove();
					continue;
				}

				ElectricityPack pack = (ElectricityPack) pairs.getValue();

				if (pairs.getKey() != null && pairs.getValue() != null && pack != null)
				{
					totalElectricity.amperes += pack.amperes;
					totalElectricity.voltage = Math.max(totalElectricity.voltage, pack.voltage);
				}
			}
		}

		return totalElectricity;
	}

	/**
	 * @return How much electricity this network needs.
	 */
	public ElectricityPack getRequest()
	{
		ElectricityPack totalElectricity = this.getRequestWithoutReduction();
		totalElectricity.amperes = Math.max(totalElectricity.amperes - this.getProduced().amperes, 0);

		return totalElectricity;
	}

	public ElectricityPack getRequestWithoutReduction()
	{
		ElectricityPack totalElectricity = new ElectricityPack(0, 0);

		Iterator it = this.consumers.entrySet().iterator();

		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry) it.next();

			if (pairs != null)
			{
				TileEntity tileEntity = (TileEntity) pairs.getKey();

				if (tileEntity == null)
				{
					it.remove();
					continue;
				}

				if (tileEntity.isInvalid())
				{
					it.remove();
					continue;
				}

				if (tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) != tileEntity)
				{
					it.remove();
					continue;
				}

				ElectricityPack pack = (ElectricityPack) pairs.getValue();

				if (pack != null)
				{
					totalElectricity.amperes += pack.amperes;
					totalElectricity.voltage = Math.max(totalElectricity.voltage, pack.voltage);
				}
			}
		}

		return totalElectricity;
	}

	/**
	 * @param tileEntity
	 * @return The electricity being input into this tile entity.
	 */
	public ElectricityPack consumeElectricity(TileEntity tileEntity)
	{
		ElectricityPack totalElectricity = new ElectricityPack(0, 0);

		try
		{
			ElectricityPack tileRequest = this.consumers.get(tileEntity);

			if (this.consumers.containsKey(tileEntity) && tileRequest != null)
			{
				// Calculate the electricity this tile entity is receiving in percentage.
				totalElectricity = this.getProduced();

				if (totalElectricity.getWatts() > 0)
				{
					ElectricityPack totalRequest = this.getRequestWithoutReduction();
					totalElectricity.amperes *= (tileRequest.amperes / totalRequest.amperes);

					int distance = this.conductors.size();
					double ampsReceived = totalElectricity.amperes - (totalElectricity.amperes * totalElectricity.amperes * this.getResistance() * distance) / totalElectricity.voltage;
					double voltsReceived = totalElectricity.voltage - (totalElectricity.amperes * this.getResistance() * distance);

					totalElectricity.amperes = ampsReceived;
					totalElectricity.voltage = voltsReceived;

					return totalElectricity;
				}
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to consume electricity!");
			e.printStackTrace();
		}

		return totalElectricity;
	}

	/**
	 * @return Returns all producers in this electricity network.
	 */
	public HashMap<TileEntity, ElectricityPack> getProducers()
	{
		return this.producers;
	}

	/**
	 * @return Returns all consumers in this electricity network.
	 */
	public HashMap<TileEntity, ElectricityPack> getConsumers()
	{
		return this.consumers;
	}

	public void addConductor(IConductor newConductor)
	{
		this.cleanConductors();

		if (!conductors.contains(newConductor))
		{
			conductors.add(newConductor);
			newConductor.setNetwork(this);
		}
	}

	/**
	 * Get only the electric units that can receive electricity from the given side.
	 */
	public List<TileEntity> getReceivers()
	{
		List<TileEntity> receivers = new ArrayList<TileEntity>();

		Iterator it = this.consumers.entrySet().iterator();

		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry) it.next();

			if (pairs != null)
			{
				receivers.add((TileEntity) pairs.getKey());
			}
		}

		return receivers;
	}

	public void cleanConductors()
	{
		for (int i = 0; i < conductors.size(); i++)
		{
			if (conductors.get(i) == null)
			{
				conductors.remove(i);
			}
			else if (((TileEntity) conductors.get(i)).isInvalid())
			{
				conductors.remove(i);
			}
		}
	}

	public void resetConductors()
	{
		for (int i = 0; i < conductors.size(); i++)
		{
			conductors.get(i).reset();
		}
	}

	public void setNetwork()
	{
		this.cleanConductors();

		for (IConductor conductor : this.conductors)
		{
			conductor.setNetwork(this);
		}
	}

	public void onOverCharge()
	{
		this.cleanConductors();

		for (int i = 0; i < conductors.size(); i++)
		{
			conductors.get(i).onOverCharge();
		}
	}

	/**
	 * Gets the resistance of this electrical network.
	 */
	public double getResistance()
	{
		double resistance = 0;

		for (int i = 0; i < conductors.size(); i++)
		{
			resistance = Math.max(resistance, conductors.get(i).getResistance());
		}

		return resistance;
	}

	public double getLowestAmpTolerance()
	{
		double lowestAmp = 0;

		for (IConductor conductor : conductors)
		{
			if (lowestAmp == 0 || conductor.getMaxAmps() < lowestAmp)
			{
				lowestAmp = conductor.getMaxAmps();
			}
		}

		return lowestAmp;
	}

	/**
	 * This function is called to refresh all conductors in this network
	 */
	public void refreshConductors()
	{
		for (int j = 0; j < this.conductors.size(); j++)
		{
			IConductor conductor = this.conductors.get(j);
			conductor.refreshConnectedBlocks();
		}
	}

	/**
	 * Tries to find the electricity network based in a tile entity and checks to see if it is a
	 * conductor. All machines should use this function to search for a connecting conductor around
	 * it.
	 * 
	 * @param conductor - The TileEntity conductor
	 * @param approachDirection - The direction you are approaching this wire from.
	 * @return The ElectricityNetwork or null if not found.
	 */
	public static ElectricityNetwork getNetworkFromTileEntity(TileEntity tileEntity, ForgeDirection approachDirection)
	{
		if (tileEntity != null)
		{
			if (tileEntity instanceof IConductor)
			{
				if (ElectricityConnections.isConnector(tileEntity))
				{
					if (ElectricityConnections.canConnect(tileEntity, approachDirection.getOpposite())) { return ((IConductor) tileEntity).getNetwork(); }
				}
			}
		}

		return null;
	}
}
