package icbm.sentry.terminal.command;

import icbm.sentry.platform.TileEntityTurretPlatform;
import icbm.sentry.terminal.AccessLevel;
import icbm.sentry.terminal.ISpecialAccess;
import icbm.sentry.terminal.ITerminal;
import icbm.sentry.terminal.TerminalCommand;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class CommandDestroy extends TerminalCommand
{

	@Override
	public String getCommandPrefix()
	{
		return "enable";
	}

	@Override
	public boolean processCommand(EntityPlayer player, ITerminal TE, String[] args)
	{
		if (args[0].equalsIgnoreCase("destroy") && args.length > 1 && args[1] != null && TE instanceof TileEntityTurretPlatform)
		{
			TileEntityTurretPlatform turret = (TileEntityTurretPlatform) TE;
			if (args.length > 1)
			{

			}
			else
			{

			}

		}
		return false;
	}

	@Override
	public boolean canPlayerUse(EntityPlayer var1, ISpecialAccess mm)
	{
		return mm.getPlayerAccess(var1.username).ordinal() >= AccessLevel.OPERATOR.ordinal();
	}

	@Override
	public boolean showOnHelp(EntityPlayer player, ISpecialAccess mm)
	{
		return this.canPlayerUse(player, mm);
	}

	@Override
	public List<String> getCmdUses(EntityPlayer player, ISpecialAccess mm)
	{
		List<String> cmds = new ArrayList<String>();
		cmds.add("destroy");
		cmds.add("destroy code");
		return cmds;
	}

	@Override
	public boolean canMachineUse(ISpecialAccess mm)
	{
		return mm instanceof TileEntityTurretPlatform;
	}

}
