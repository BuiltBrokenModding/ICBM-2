package icbm.api.flag;

import java.util.Iterator;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Commands used for flags and regions. This can be used for protection for specific mod components
 * that might be dangerous.
 * 
 * @author Calclavia
 * 
 */
public class CommandFlag extends CommandBase
{
	public String commandName = "flag";
	public ModFlagData modFlagData;

	public CommandFlag(ModFlagData modFlagData)
	{
		this.modFlagData = modFlagData;
	}

	@Override
	public String getCommandName()
	{
		return commandName;
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender)
	{
		return "Flags are region based.  \n" + "/" + getCommandName() + " protectOn" + "\n" + "/" + getCommandName() + " protectOff" + "\n" + "/" + getCommandName() + " list" + "\n" + "/" + getCommandName() + " lag" + "\n" + "/" + getCommandName() + " addRegion <name> <radius> <type:all,block,grenade,missile>" + "\n" + "/" + getCommandName() + " removeRegion <name>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		try
		{
			EntityPlayer entityPlayer = (EntityPlayer) sender;

			// The world data the player is on.
			FlagWorld flagWorld = this.modFlagData.getWorldFlags(entityPlayer.worldObj);

			String commandName = args[0].toLowerCase();

			switch (commandName)
			{
			/**
			 * The list command lists out all regions in this world/region.
			 */
				case "list":
				{
					try
					{
						String regionName = args[1];

						String msg = "List of flags in this region: ";

						Iterator<Flag> i = flagWorld.getRegion(regionName).flags.iterator();
						while (i.hasNext())
						{
							Flag flag = i.next();
							msg = msg + " " + flag.name + "=>" + flag.value + ",";
						}

						sender.sendChatToPlayer(msg);

					}
					catch (Exception e)
					{
						String msg = "List of regions in this dimension: ";

						Iterator<FlagRegion> i = flagWorld.regions.iterator();
						while (i.hasNext())
						{
							FlagRegion flagRegion = i.next();
							msg = msg + " " + flagRegion.name + " (" + flagRegion.region.min.x + "," + flagRegion.region.min.z + ")" + ",";
						}

						sender.sendChatToPlayer(msg);
					}
					break;
				}
				case "addregion":
				{
					if (args.length > 2)
					{
						String regionName = args[0];
						int radius = Integer.parseInt(args[1]);

						if (radius > 0)
						{
							if (flagWorld.getRegion(regionName) == null)
							{
								flagWorld
							}
							else
							{
								throw new WrongUsageException("Region already exists.");
							}
						}
					}
					else
					{
						throw new WrongUsageException("/" + this.getCommandName() + " <name> <radius>");
					}

					break;
				}
				case "removeregion":
				{
					if (args.length > 1)
					{
						String regionName = args[1];

						if (flagWorld.removeRegion(regionName))
						{
							sender.sendChatToPlayer("Region with name " + regionName + " is removed.");
						}
						else
						{
							throw new WrongUsageException("The specified region does not exist.");
						}
					}
					else
					{
						throw new WrongUsageException("Please specify the region name.");
					}
					break;
				}
			}

			if (args[0].equalsIgnoreCase("list"))
			{

			}
			else
			{
				throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
			}

			if (args.length == 1)
			{

			}
			else if (args.length == 2)
			{
				if (args[0].equalsIgnoreCase("removeregion"))
				{
					String name = args[1];
					if (dimData.hasKey(name))
					{
						NBTTagCompound newdata = new NBTTagCompound();
						Iterator i = dimData.getTags().iterator();
						while (i.hasNext())
						{
							try
							{
								NBTTagCompound tag = (NBTTagCompound) i.next();
								if (!tag.getName().equalsIgnoreCase(name))
								{
									newdata.setCompoundTag(tag.getName(), tag);
								}
								else
								{
									sender.sendChatToPlayer("Region with name " + name + " is removed.");
								}
							}
							catch (Exception e)
							{
							}
						}
						dimData = newdata;
					}
					else
					{
						throw new WrongUsageException("The specified region does not exist.");
					}
				}

			}
			else if (args.length >= 3)
			{
				if (args[0].equalsIgnoreCase("addregion"))
				{
					String name = args[1];

					if (dimData.hasKey(name))
					{
						sender.sendChatToPlayer("That region already exists.");
					}
					else
					{
						NBTTagCompound region = new NBTTagCompound();
						region.setInteger("X", ((Double) entityPlayer.posX).intValue());
						region.setInteger("Z", ((Double) entityPlayer.posZ).intValue());
						region.setInteger("R", Integer.parseInt(args[2]));

						if (args[3].equalsIgnoreCase("block"))
						{
							region.setInteger(NBTFileLoader.FIELD_TYPE, 1);
						}
						else if (args[3].equalsIgnoreCase("grenade"))
						{
							region.setInteger(NBTFileLoader.FIELD_TYPE, 2);
						}
						else if (args[3].equalsIgnoreCase("missile"))
						{
							region.setInteger(NBTFileLoader.FIELD_TYPE, 3);
						}
						else
						{
							region.setInteger(NBTFileLoader.FIELD_TYPE, 0);
						}

						dimData.setCompoundTag(name, region);
						sender.sendChatToPlayer("Region " + name + " added with radius " + args[2] + " at X: " + ((Double) entityPlayer.posX).intValue() + " Y: " + ((Double) entityPlayer.posY).intValue() + ".");
					}
				}
			}
			else
			{
				throw new WrongUsageException(this.getCommandUsage(sender));
			}

			NBTFileLoader.nbtData.setCompoundTag("dim" + dimension, dimData);
		}
		catch (Exception e)
		{
			throw new WrongUsageException(this.getCommandUsage(sender));
		}
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] { "protectOn", "protectOff", "list", "lag" }) : null;
	}

}
