package icbm.common;

import icbm.common.zhapin.ZhaPin.ZhaPinType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector2;
import cpw.mods.fml.common.FMLLog;

public class BaoHu
{
	/**
	 * File Structure: Tag: DimData -Tag: DimID -boolean: globalBan -Tag: RegionName -int: X -int: Z
	 * -int: R -int: TYPE
	 */
	public static NBTTagCompound nbtData;

	public static final String FIELD_GLOBAL_BAN = "globalBan";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_X = "X";
	public static final String FIELD_Z = "Z";
	public static final String FIELD_R = "R";

	public static boolean SHE_DING_BAO_HU;

	public static boolean shiWeiZhiBaoHu(World worldObj, Vector2 position, ZhaPinType type)
	{
		try
		{
			if (!worldObj.isRemote)
			{
				NBTTagCompound dimData = nbtData.getCompoundTag("dim" + worldObj.provider.dimensionId);

				if (nengQuanQiuBaoHu(dimData)) { return true; }

				// Regions check
				Iterator i = dimData.getTags().iterator();
				while (i.hasNext())
				{
					try
					{
						NBTTagCompound region = (NBTTagCompound) i.next();

						if (Vector2.distance(position, new Vector2(region.getInteger(FIELD_X), region.getInteger(FIELD_Z))) <= region.getInteger(FIELD_R)) { return (ZhaPinType.get(region.getInteger(FIELD_TYPE)) == ZhaPinType.QUAN_BU || ZhaPinType.get(region.getInteger(FIELD_TYPE)) == type); }
					}
					catch (Exception e)
					{
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public static boolean nengDanBaoHu(World worldObj, Vector2 position)
	{
		return !shiWeiZhiBaoHu(worldObj, position, ZhaPinType.ZHA_DAN);
	}

	public static boolean nengShouLiuDanBaoHu(World worldObj, Vector2 position)
	{
		return !shiWeiZhiBaoHu(worldObj, position, ZhaPinType.SHOU_LIU_DAN);
	}

	public static boolean nengDaoDanBaoHu(World worldObj, Vector2 position)
	{
		return !shiWeiZhiBaoHu(worldObj, position, ZhaPinType.DAO_DAN);
	}

	public static boolean nengQuanQiuBaoHu(NBTTagCompound dimData)
	{
		return ((!dimData.hasKey(FIELD_GLOBAL_BAN) && SHE_DING_BAO_HU) || dimData.getBoolean(FIELD_GLOBAL_BAN));
	}

	public static boolean saveData(NBTTagCompound data, String filename)
	{
		String folder;

		if (MinecraftServer.getServer().isDedicatedServer())
		{
			folder = MinecraftServer.getServer().getFolderName();
		}
		else
		{
			folder = "saves" + File.separator + MinecraftServer.getServer().getFolderName();
		}

		try
		{
			File tempFile;
			File file;

			if (ZhuYao.proxy.getMinecraftDir() != "")
			{
				tempFile = new File(ZhuYao.proxy.getMinecraftDir(), folder + File.separator + filename + "_tmp.dat");
				file = new File(ZhuYao.proxy.getMinecraftDir(), folder + File.separator + filename + ".dat");
			}
			else
			{
				tempFile = new File(folder + File.separator + filename + "_tmp.dat");
				file = new File(folder + File.separator + filename + ".dat");
			}

			CompressedStreamTools.writeCompressed(data, new FileOutputStream(tempFile));

			if (file.exists())
			{
				file.delete();
			}

			tempFile.renameTo(file);

			FMLLog.fine("Saved ICBM data successfully.");
			return true;
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to save " + filename + ".dat!");
			e.printStackTrace();
			return false;
		}
	}

	public static NBTTagCompound loadData(String filename)
	{
		String folder;

		if (MinecraftServer.getServer().isDedicatedServer())
		{
			folder = MinecraftServer.getServer().getFolderName();
		}
		else
		{
			folder = "saves" + File.separator + MinecraftServer.getServer().getFolderName();
		}

		try
		{
			File file;

			if (ZhuYao.proxy.getMinecraftDir() != "")
			{
				file = new File(ZhuYao.proxy.getMinecraftDir(), folder + File.separator + filename + ".dat");
			}
			else
			{
				file = new File(folder + File.separator + filename + ".dat");
			}

			if (file.exists())
			{
				return CompressedStreamTools.readCompressed(new FileInputStream(file));
			}
			else
			{
				return new NBTTagCompound();
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to load " + filename + ".dat!");
			e.printStackTrace();
			return null;
		}
	}
}
