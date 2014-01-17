package icbm.sentry.turret;

import universalelectricity.api.CompatibilityModule;
import icbm.core.ICBMCore;
import net.minecraft.network.packet.Packet;
import net.minecraftforge.common.ForgeDirection;
import calclavia.lib.access.AccessProfile;
import calclavia.lib.access.IProfileContainer;
import calclavia.lib.terminal.TileTerminal;

public class TileSentry extends TileTerminal implements IProfileContainer
{
    /** Profile that control access properties for users */
    protected AccessProfile accessProfile;
    /** The start index of the upgrade slots for the turret. */
    public static final int UPGRADE_START_INDEX = 12;
    private static final int TURRET_UPGADE_SLOTS = 3;

  

    @Override
    public AccessProfile getAccessProfile()
    {
        if (this.accessProfile == null)
        {
            this.setAccessProfile(new AccessProfile().generateNew("default", this));
        }
        return accessProfile;
    }

    @Override
    public void setAccessProfile(AccessProfile profile)
    {
        this.accessProfile = profile;
    }

    @Override
    public boolean canAccess(String username)
    {
        return accessProfile.getUserAccess(username) != null;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return ICBMCore.PACKET_TILE.getPacket(this, this.getPacketData(0).toArray());
    }

    @Override
    public Packet getTerminalPacket()
    {
        return ICBMCore.PACKET_TILE.getPacket(this, this.getPacketData(1).toArray());
    }

    @Override
    public Packet getCommandPacket(String username, String cmdInput)
    {
        return ICBMCore.PACKET_TILE.getPacket(this, this.getPacketData(2).toArray());
    }
}
