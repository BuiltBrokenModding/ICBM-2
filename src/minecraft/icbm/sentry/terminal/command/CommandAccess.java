package icbm.sentry.terminal.command;

import icbm.sentry.gui.GuiTerminal;
import icbm.sentry.platform.TileEntityTurretPlatform;
import icbm.sentry.terminal.AccessLevel;
import icbm.sentry.terminal.ISpecialAccess;
import icbm.sentry.terminal.TerminalCommand;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class CommandAccess extends TerminalCommand
{

	@Override
	public String getCommandPrefix()
	{
		return "access";
	}

	@Override
	public boolean processCommand(EntityPlayer player, ISpecialAccess TE, GuiTerminal gui, String[] args)
	{
		if (args[0].equalsIgnoreCase("access") && args.length > 1 && args[1] != null && TE instanceof TileEntityTurretPlatform)
		{
			TileEntityTurretPlatform turret = (TileEntityTurretPlatform) TE;
			if (args[1].equalsIgnoreCase("?"))
			{
				gui.addToConsole("Access = " + turret.getPlayerAccess(player).displayName);

				return true;
			}
			else if (args[1].equalsIgnoreCase("set") && args.length > 3)
			{
				// TODO readd this feature later
			}
		}
		return false;
	}

	@Override
	public boolean canPlayerUse(EntityPlayer var1, ISpecialAccess mm)
	{
		return mm.getPlayerAccess(var1).ordinal() >= AccessLevel.STANARD.ordinal();
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
		cmds.add("access set root [pass]");
		cmds.add("access ?");
		return cmds;
	}

	@Override
	public boolean canMachineUse(ISpecialAccess mm)
	{
		return mm instanceof TileEntityTurretPlatform;
	}

}
