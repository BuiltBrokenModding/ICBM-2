package icbm.sentry.terminal;

import icbm.sentry.ISpecialAccess;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

/** @author Calclavia, DarkGuardsman */
public abstract class TerminalCommand
{
    /** what the commands starts with /help /time /day
     * 
     * @return */
    public abstract String getCommandPrefix();

    /** Executes the command
     * 
     * @param var1
     * @param args */
    public abstract boolean processCommand(EntityPlayer player, ITerminal terminal, String[] args);

    /** Returns true if the given command sender is allowed to use this command. */
    public abstract boolean canPlayerUse(EntityPlayer player, ISpecialAccess specialAccess);

    /** should this command show on /help
     * 
     * @param player - used to find if it should show
     * @return true/false */
    public abstract boolean showOnHelp(EntityPlayer player, ISpecialAccess specialAccess);

    /** returns the list of commands that the player can view on /help keep it shorter than 22 chars
     * to fit on Cmd Gui
     * 
     * @param player
     * @return */
    public abstract List<String> getCmdUses(EntityPlayer player, ISpecialAccess specialAccess);

    /** some cmds can only be use on some machines but will be access by all machines. to prevent the
     * cmd from activating on the machine return false
     * 
     * @param mm
     * @return */
    public abstract boolean canMachineUse(ISpecialAccess specialAccess);
}
