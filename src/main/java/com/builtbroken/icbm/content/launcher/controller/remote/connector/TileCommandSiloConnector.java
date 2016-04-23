package com.builtbroken.icbm.content.launcher.controller.remote.connector;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.api.controller.ISiloConnectionPoint;
import com.builtbroken.icbm.api.launcher.ILauncher;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Works like the TileController linking silos together into a control group. This group is then broadcasted back to the uplink tile.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileCommandSiloConnector extends TileModuleMachine implements ILinkable, IPostInit, ISiloConnectionPoint
{
    /** Main texture */
    public static IIcon texture;
    /** Top icon */
    public static IIcon top_texture;

    public static double MAX_LINK_DISTANCE = 20;

    /** List of all connected data */
    protected List<ISiloConnectionData> siloData = new ArrayList();
    /** Map of positions to connected data, mainly used by link code */
    protected HashMap<Pos, ISiloConnectionData> posToData = new HashMap();

    public TileCommandSiloConnector()
    {
        super("siloWirelessDataPoint", Material.iron);
        this.hardness = 10f;
        this.resistance = 10f;
        this.addInventoryModule(2);
    }

    @Override
    public IIcon getIcon(int side)
    {
        if (side == 1)
        {
            return top_texture;
        }
        return texture;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        texture = iconRegister.registerIcon(ICBM.PREFIX + "commandSiloConnector");
        top_texture = iconRegister.registerIcon(ICBM.PREFIX + "commandSiloConnector_top");
    }

    @Override
    public Tile newTile()
    {
        return new TileCommandSiloConnector();
    }

    @Override
    public void onPostInit()
    {
        // GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockSiloController), "IGI", "CGC", "ICI", 'I', OreNames.INGOT_IRON, 'G', Blocks.chest, 'C', UniversalRecipe.CIRCUIT_T1.get()));
    }

    @Override
    public String getInventoryName()
    {
        return "tile.icbm:smallSiloController.container.name";
    }

    /**
     * Called to fire the launcher in the list of linked launchers
     *
     * @param index - # in the launcher list
     */
    protected void fireLauncher(int index)
    {
        if (isServer())
        {
            if (index >= 0 && index < siloData.size())
            {
                ISiloConnectionData data = siloData.get(index);
                ILauncher launcher = data.getSilo();
                if (launcher != null)
                {
                    launcher.fireMissile();
                }
            }
        }
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
        if (distance(pos) > MAX_LINK_DISTANCE)
        {
            return "link.error.pos.distance.max";
        }

        //Compare tile pass code
        TileEntity tile = pos.getTileEntity(loc.world());
        if (!(tile instanceof ILauncher))
        {
            return "link.error.tile.invalid";
        }
        if (tile instanceof IPassCode && ((IPassCode) tile).getCode() != code)
        {
            return "link.error.code.match";
        }

        //Add location
        if (!posToData.containsKey(pos))
        {
            ISiloConnectionData data = new SiloConnectionData((ILauncher) tile);
            if (!siloData.contains(data))
            {
                siloData.add(data);
                posToData.put(pos, data);
                ((ILinkFeedback) tile).onLinked(toLocation());
                return "";
            }
            else
            {
                data = posToData.get(pos);
                siloData.add(data);
                posToData.put(pos, data);
                ((ILinkFeedback) tile).onLinked(toLocation());
                return "link.updated";
            }
        }
        else
        {
            ISiloConnectionData data = posToData.get(pos);
            siloData.remove(data);
            posToData.remove(pos);
            return "link.removed";
        }
    }

    @Override
    public List<ISiloConnectionData> getSiloConnectionData()
    {
        return siloData;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("locations"))
        {
            siloData.clear();
            posToData.clear();
            NBTTagList list = nbt.getTagList("locations", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                ISiloConnectionData data = new SiloConnectionData(list.getCompoundTagAt(i));
                siloData.add(data);
                posToData.put(new Pos(data.x(), data.y(), data.z()), data);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (siloData.size() > 0)
        {
            NBTTagList list = new NBTTagList();
            for (ISiloConnectionData data : siloData)
            {
                list.appendTag(data.save(new NBTTagCompound()));
            }
            nbt.setTag("siloData", list);
        }
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
                    player.addChatComponentMessage(new ChatComponentText("Silos -> " + siloData.size()));
                }
                return true;
            }
        }
        return false;
    }

}
