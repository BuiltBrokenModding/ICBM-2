package icbm.common;

import icbm.common.zhapin.EZhaPin;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.common.FMLCommonHandler;

public class MingLing extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "ICBM";
	}

	@Override
	public List getCommandAliases()
	{
		return Arrays.asList(new String[] { "icbm" });
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender)
	{
		return "ICBM Commands \n" + "/" + getCommandName() + " protectOn" + "\n" + "/" + getCommandName() + " protectOff" + "\n" + "/" + getCommandName() + " list" + "\n" + "/" + getCommandName() + " lag" + "\n" + "/" + getCommandName() + " addRegion <name> <radius> <type:all,block,grenade,missile>" + "\n" + "/" + getCommandName() + " removeRegion <name>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		try
		{
			EntityPlayer entityPlayer = (EntityPlayer) sender;
			int dimension = entityPlayer.worldObj.getWorldInfo().getDimension();
			NBTTagCompound dimData = BaoHu.nbtData.getCompoundTag("dim" + dimension);

			if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("protectOn"))
				{
					dimData.setBoolean(BaoHu.FIELD_GLOBAL_BAN, true);
					sender.sendChatToPlayer("The dimension you are in is now protected from ICBM.");
				}
				else if (args[0].equalsIgnoreCase("protectOff"))
				{
					dimData.setBoolean(BaoHu.FIELD_GLOBAL_BAN, false);
					sender.sendChatToPlayer("The dimension you are in is now unprotected from ICBM.");
				}
				else if (args[0].equalsIgnoreCase("list"))
				{
					String msg = "List of regions in this dimension: ";
					Iterator i = dimData.getTags().iterator();
					while (i.hasNext())
					{
						try
						{
							NBTTagCompound tag = (NBTTagCompound) i.next();
							msg = msg + " " + tag.getName() + ",";
						}
						catch (Exception e)
						{
						}
					}

					sender.sendChatToPlayer(msg);
				}
				else
				{
					throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
				}
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
				else if (args[0].equalsIgnoreCase("lag"))
				{
					int radius = parseInt(sender, args[1]);

					if (radius > 0 && radius < 1000)
					{
						EntityPlayer player = (EntityPlayer) sender;

						AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(player.posX - radius, player.posY - radius, player.posZ - radius, player.posX + radius, player.posY + radius, player.posZ + radius);
						List<Entity> entitiesNearby = player.worldObj.getEntitiesWithinAABB(Entity.class, bounds);

						for (Entity entity : entitiesNearby)
						{
							if (entity instanceof EFeiBlock)
							{
								((EFeiBlock) entity).setBlock();
							}
							else if (entity instanceof EZhaPin)
							{
								entity.setDead();
							}
						}

						sender.sendChatToPlayer("Removed all ICBM lag sources from " + radius + " radius.");
					}
					else
					{
						throw new WrongUsageException("Radius not within range.");
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
							region.setInteger(BaoHu.FIELD_TYPE, 1);
						}
						else if (args[3].equalsIgnoreCase("grenade"))
						{
							region.setInteger(BaoHu.FIELD_TYPE, 2);
						}
						else if (args[3].equalsIgnoreCase("missile"))
						{
							region.setInteger(BaoHu.FIELD_TYPE, 3);
						}
						else
						{
							region.setInteger(BaoHu.FIELD_TYPE, 0);
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

			BaoHu.nbtData.setCompoundTag("dim" + dimension, dimData);
		}
		catch (Exception e)
		{
			throw new WrongUsageException(this.getCommandUsage(sender));
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer) { return FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().areCommandsAllowed(sender.getCommandSenderName()) || sender.getCommandSenderName().toLowerCase() == "calclavia"; }

		return false;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] { "protectOn", "protectOff", "list", "lag" }) : null;
	}

}
