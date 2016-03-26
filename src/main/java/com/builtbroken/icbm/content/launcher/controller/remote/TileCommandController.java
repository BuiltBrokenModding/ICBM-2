package com.builtbroken.icbm.content.launcher.controller.remote;

import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Version of the controller that works from much greater distances. As well does not require linking
 * chip to link up silos. Instead a list of silos will appear and can be imported into the controller.
 * So long as they share the same network id and pass code with the controller.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileCommandController extends TileModuleMachine implements ILinkable, IPacketIDReceiver, IGuiTile, IPostInit
{
    /** List of locations containing known launchers */
    protected List<Pos> launcherLocations = new ArrayList();

    public TileCommandController()
    {
        super("remoteController", Material.iron);
    }

    @Override
    public String link(Location pos, short pass)
    {
        return null;
    }

    @Override
    public void onPostInit()
    {

    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerCommandController(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return null;
    }
}
