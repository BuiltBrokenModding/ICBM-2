package com.builtbroken.icbm.content.launcher.controller.local;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.icbm.content.launcher.controller.LauncherData;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to link several launchers together to be controlled from a single terminal
 * Created by robert on 4/3/2015.
 */
public class TileController extends TileModuleMachine implements ILinkable, IPacketIDReceiver, IGuiTile, ISimpleItemRenderer, IPostInit
{
    public static double MAX_LINK_DISTANCE = 100;
    public static int MAX_LAUNCHER_LINK = 6; //changed to 6 due to current GUI size and no GUI paging

    //TODO auto connect to any launcher next to the controller
    //TODO default to rear connection first
    //TODO add OC support
    protected List<Pos> launcherLocations = new ArrayList<Pos>();

    //Only used client side at the moment
    protected List<LauncherData> launcherData;

    public TileController()
    {
        super("missileController", Material.iron);
        this.hardness = 10f;
        this.resistance = 10f;
        this.addInventoryModule(2);
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
        this.isOpaque = false;
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockSiloController), "IGI", "CGC", "ICI", 'I', Items.iron_ingot, 'G', Blocks.chest, 'C', UniversalRecipe.CIRCUIT_T1.get()));
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
            if (index >= 0 && index < launcherLocations.size())
            {
                Pos pos = launcherLocations.get(index);
                TileEntity tile = pos.getTileEntity(world());
                if (tile instanceof TileAbstractLauncher)
                {
                    ((TileAbstractLauncher) tile).fireMissile();
                }
            }
        }
        else
        {
            sendPacketToServer(new PacketTile(this, 2, index));
        }
    }

    /**
     * Loops threw the list of launchers and fires each one
     */
    protected void fireAllLaunchers()
    {
        if (isServer())
        {
            for (int i = 0; i < launcherLocations.size(); i++)
            {
                fireLauncher(i);
            }
        }
        else
        {
            sendPacketToServer(new PacketTile(this, 2, -1));
        }
    }

    @Override
    public String link(Location loc, short code)
    {
        //Validate location data
        if (loc.world != world())
            return "link.error.world.match";

        Pos pos = loc.toPos();
        if (!pos.isAboveBedrock())
            return "link.error.pos.invalid";
        if (distance(pos) > MAX_LINK_DISTANCE)
            return "link.error.pos.distance.max";

        //Compare tile pass code
        TileEntity tile = pos.getTileEntity(loc.world());
        if (!(tile instanceof TileAbstractLauncher))
            return "link.error.tile.invalid";
        if (((IPassCode) tile).getCode() != code)
            return "link.error.code.match";

        //Add location
        if (!launcherLocations.contains(pos))
        {
            if (launcherLocations.size() < MAX_LAUNCHER_LINK)
            {
                launcherLocations.add(pos);
                ((ILinkFeedback) tile).onLinked(toLocation());
                return "";
            }
            else
            {
                return "link.error.tile.limit.max";
            }

        }
        else
        {
            return "link.error.tile.already.added";
        }
    }

    @Override
    public void onNeighborChanged(Pos pos)
    {
        super.onNeighborChanged(pos);
        if (!launcherLocations.contains(pos))
        {
            TileEntity tile = pos.getTileEntity(world());
            if (tile instanceof TileAbstractLauncher)
            {
                launcherLocations.add(pos);
                ((ILinkFeedback) tile).onLinked(toLocation());
            }
        }
    }

    /**
     * Called to remove the launcher from the list
     *
     * @param pos - location of the launcher
     */
    protected void removeLauncher(Pos pos)
    {
        if (isServer())
            launcherLocations.remove(pos);
        else
            sendPacketToServer(new PacketTile(this, 1, pos));
    }

    /**
     * Grabs a list of all linked launchers
     *
     * @return ArrayList<TileAbstractLauncher>
     */
    protected List<TileAbstractLauncher> getLaunchers()
    {
        List<TileAbstractLauncher> list = new ArrayList();
        for (Pos pos : launcherLocations)
        {
            TileEntity tile = pos.getTileEntity(world());
            if (tile instanceof TileAbstractLauncher)
            {
                list.add((TileAbstractLauncher) tile);
            }
        }
        return list;
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (isClient())
            {
                //Basic GUI data
                if (id == 0)
                {
                    NBTTagCompound tag = ByteBufUtils.readTag(buf);
                    launcherData = new ArrayList();

                    if (tag.hasKey("launcherData"))
                    {
                        NBTTagList list = tag.getTagList("launcherData", 10);
                        for (int i = 0; i < list.tagCount(); i++)
                        {
                            launcherData.add(new LauncherData(list.getCompoundTagAt(i)));
                        }
                    }

                    if (Minecraft.getMinecraft().currentScreen instanceof GuiController)
                    {
                        ((GuiController) Minecraft.getMinecraft().currentScreen).reloadData();
                    }

                    return true;
                }
            }
            else
            {
                //Remove launcher, input from GUI
                if (id == 1)
                {
                    removeLauncher(new Pos(buf));
                    return true;
                }
                else if (id == 2)
                {
                    int index = buf.readInt();
                    if (index == -1)
                        fireAllLaunchers();
                    else
                        fireLauncher(index);
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void doUpdateGuiUsers()
    {
        //Only send packets to the GUI if we have data to send
        if (launcherLocations.size() > 0)
        {
            PacketTile packet;
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagList list = new NBTTagList();

            //Construct launcher data structure
            for (TileAbstractLauncher launcher : getLaunchers())
            {
                list.appendTag(new LauncherData(launcher).toNBT());
            }
            nbt.setTag("launcherData", list);

            //Create and send packet
            packet = new PacketTile(this, 0, nbt);
            sendPacketToGuiUsers(packet);
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
        return new GuiController(this, player);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("locations"))
        {
            launcherLocations.clear();
            NBTTagList list = nbt.getTagList("locations", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                launcherLocations.add(new Pos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (launcherLocations != null && launcherLocations.size() > 0)
        {
            NBTTagList list = new NBTTagList();
            for (Pos pos : launcherLocations)
            { list.appendTag(pos.toIntNBT()); }
            nbt.setTag("locations", list);
        }
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IWorldPosItem)
            return false;

        if (isServer())
            openGui(player, ICBM.INSTANCE);
        return true;
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glScaled(.8f, .8f, .8f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.WEAPON_CASE_TEXTURE);
        Assets.WEAPON_CASE_MODEL.renderAll();
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new Cube(0, 0, 0, 1, 2, 1).add(x(), y(), z()).toAABB();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
        switch (facing)
        {
            case EAST:
                break;
            case WEST:
                GL11.glRotatef(180f, 0, 1f, 0);
                break;
            case SOUTH:
                GL11.glRotatef(-90f, 0, 1f, 0);
                break;
            default:
                GL11.glRotatef(90f, 0, 1f, 0);
                break;
        }
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.LAUNCHER_CONTROLLER_TEXTURE);
        Assets.LAUNCHER_CONTROLLER_MODEL.renderOnly("screen");
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.WEAPON_CASE_TEXTURE);
        Assets.LAUNCHER_CONTROLLER_MODEL.renderAllExcept("screen");
        GL11.glPopMatrix();
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {

    }

    @Override
    public IIcon getIcon()
    {
        return Blocks.gravel.getIcon(0, 0);
    }
}
