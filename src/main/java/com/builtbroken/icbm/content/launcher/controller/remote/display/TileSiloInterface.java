package com.builtbroken.icbm.content.launcher.controller.remote.display;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.launcher.controller.local.TileController;
import com.builtbroken.icbm.content.launcher.controller.remote.central.TileCommandController;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.tile.Tile;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

/**
 * Used to get info and send commands to silos
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileSiloInterface extends Tile implements ILinkable, ILinkFeedback, IGuiTile
{
    private Pos commandCenterPos;
    private TileCommandController commandCenter;

    public TileSiloInterface()
    {
        super("commandSiloDisplay", Material.iron);
        this.hardness = 10f;
        this.resistance = 10f;
    }
    @Override
    public Tile newTile()
    {
        return new TileSiloInterface();
    }

    @Override
    public void onLinked(Location location)
    {

    }

    @Override
    public String link(Location loc, short code)
    {
        //Validate location data
        if (loc.world != world())
        {
            return "link.error.world.match";
        }

        Pos pos = loc.toPos();
        if (!pos.isAboveBedrock())
        {
            return "link.error.pos.invalid";
        }
        if (distance(pos) > TileController.MAX_LINK_DISTANCE)
        {
            return "link.error.pos.distance.max";
        }

        //Compare tile pass code
        TileEntity tile = pos.getTileEntity(loc.world());
        if (!(tile instanceof TileCommandController))
        {
            return "link.error.tile.invalid";
        }
        if (tile instanceof IPassCode && ((IPassCode) tile).getCode() != code)
        {
            return "link.error.code.match";
        }

        //Add location
        if (commandCenter == null && !pos.equals(commandCenterPos) || tile != commandCenter)
        {
            commandCenterPos = pos;
            commandCenter = (TileCommandController) tile;
            if (tile instanceof ILinkFeedback)
            {
                ((ILinkFeedback) tile).onLinked(toLocation());
            }
            return "";
        }
        else
        {
            return "link.error.tile.already.added";
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerDummy(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiSiloInterface(player, this);
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (player.getHeldItem() != null)
        {
            if (player.getHeldItem().getItem() instanceof IWorldPosItem)
            {
                return false;
            }
            else if (player.getHeldItem().getItem() == Items.stick && Engine.runningAsDev)
            {
                if (isServer())
                {
                    player.addChatComponentMessage(new ChatComponentText("Command Center -> " + commandCenter));
                }
                return true;
            }
        }
        if(isServer())
        {
            openGui(player, ICBM.INSTANCE);
        }
        return true;
    }
}
