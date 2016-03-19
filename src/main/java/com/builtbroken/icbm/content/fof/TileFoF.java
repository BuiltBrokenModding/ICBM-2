package com.builtbroken.icbm.content.fof;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.IFoF;
import com.builtbroken.icbm.content.fof.gui.ContainerFoF;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.jlib.lang.EnglishLetters;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.access.*;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import com.builtbroken.mc.prefab.tile.multiblock.EnumMultiblock;
import com.builtbroken.mc.prefab.tile.multiblock.MultiBlockHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StringUtils;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Friend or foe controller, used to sync FoF tags between launchers, AMS, and other tiles.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2016.
 */
public class TileFoF extends TileModuleMachine implements IGuiTile, IMultiTileHost, IFoFStation, IPacketIDReceiver, IProfileContainer, IPostInit
{
    /** Structure map cache... bit pointless but saves a little ram */
    private static final HashMap<IPos3D, String> STRUCTURE = new HashMap();

    static
    {
        STRUCTURE.put(new Pos(0, 1, 0), EnumMultiblock.TILE.getName());
    }

    /** Main ID used for FoF system */
    protected String userFoFID;
    /** Archive of past FoF ids that should still be considered active but will not be applied to new objects. */
    protected List<String> archivedFoFIDs = new ArrayList();

    /** Toggle to note the tile is currently breaking down it's multi-block, used to prevent duplication and infinite loops */
    private boolean breaking = false;
    /** Current access profile used for user permissions */
    private AccessProfile profile;
    /** Global profile ID used to load access profile */
    private String globalProfileID;

    public TileFoF()
    {
        super("ICBMxFoF", Material.iron);
        this.itemBlock = ItemBlockFoF.class;
        this.hardness = 15f;
        this.resistance = 50f;
        this.renderNormalBlock = false;
        this.addInventoryModule(2);
    }

    @Override
    public Tile newTile()
    {
        return new TileFoF();
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        if (isServer())
        {
            if (userFoFID == null || userFoFID.isEmpty())
            {
                userFoFID = getRandomString();
            }
            MultiBlockHelper.buildMultiBlock(world(), this, true, true);
        }
    }

    /**
     * Generates a random string containing numbers and letters between a length of 10 - 30
     * 1,264,020,397,516,800 to 2,730,903,391,116,338,302,840,472,139,202,560,000,000 possible permutations
     * using this method. Not including the number of permutation if capital letters are considered.
     *
     * @return new String
     */
    protected String getRandomString()
    {
        String string = "";
        //Generate random default string
        int[] l = MathHelper.generateRandomIntArray(world().rand, EnglishLetters.values().length + 9, 10 + world().rand.nextInt(20));
        for (int i : l)
        {
            if (i < 10)
            {
                string += i;
            }
            else if (world().rand.nextBoolean())
            {
                string += EnglishLetters.values()[i - 10].name();
            }
            else
            {
                string += EnglishLetters.values()[i - 10].name().toLowerCase();
            }
        }
        return string;
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (isServer())
            {
                //Set FoF ID, Main Gui
                if (id == 2)
                {
                    if (hasNode(player, Permissions.machineConfigure.toString()))
                    {
                        String change = ByteBufUtils.readUTF8String(buf);
                        if (buf.readBoolean() && !archivedFoFIDs.contains(userFoFID))
                        {
                            archivedFoFIDs.add(userFoFID);
                        }
                        this.userFoFID = change;
                        sendPacketToGuiUsers(new PacketTile(this, 1, "confirm"));
                    }
                    else
                    {
                        sendPacketToGuiUsers(new PacketTile(this, 1, "missing.perm"));
                    }
                    return true;
                }
                //Enable permission system, GuiSettings
                else if (id == 3)
                {
                    if (hasNode(player, Permissions.machineConfigure.toString()))
                    {
                        if (buf.readBoolean())
                        {
                            initProfile();
                            getAccessProfile().getOwnerGroup().addMember(new AccessUser(player));
                        }
                        else
                        {
                            profile = null;
                            globalProfileID = null;
                            sendDescPacket();
                        }
                        sendPacketToGuiUsers(new PacketTile(this, 1, "[1]confirm"));
                    }
                    else
                    {
                        sendPacketToGuiUsers(new PacketTile(this, 1, "[2]missing.perm"));
                    }
                }
                //Clear FoF id archive, GuiSettings
                else if (id == 4)
                {
                    if (hasNode(player, Permissions.machineConfigure.toString()))
                    {
                        int s = archivedFoFIDs.size();
                        archivedFoFIDs.clear();
                        sendPacketToGuiUsers(new PacketTile(this, 1, "[2]removed.ids{" + s + "}"));
                    }
                    else
                    {
                        sendPacketToGuiUsers(new PacketTile(this, 1, "[2]missing.perm"));
                    }
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        buf.writeBoolean(profile != null);
    }

    @Override
    public void doUpdateGuiUsers()
    {
        if (ticks % 20 == 0 && userFoFID != null)
        {
            sendPacketToGuiUsers(new PacketTile(this, 3, userFoFID));
        }
    }

    protected void initProfile()
    {
        if (profile == null)
        {
            if (!StringUtils.isNullOrEmpty(globalProfileID))
            {
                profile = GlobalAccessSystem.getOrCreateProfile(globalProfileID, true);
            }
            else
            {
                profile = new AccessProfile().generateNew("Default", this);
                if (this.username != null)
                {
                    profile.getOwnerGroup().addMember(new AccessUser(this.username, this.owner));
                }
            }
            sendDescPacket();
        }
    }

    @Override
    public String getProvidedFoFTag()
    {
        return userFoFID;
    }

    @Override
    public boolean isFriendly(Entity entity)
    {
        if (entity instanceof IFoF)
        {
            if (((IFoF) entity).getFoFTag() != null)
            {
                if (((IFoF) entity).getFoFTag().equals(getProvidedFoFTag()))
                {
                    return true;
                }
                return archivedFoFIDs.contains(((IFoF) entity).getFoFTag());
            }
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("fofID"))
        {
            userFoFID = nbt.getString("fofID");
        }
        if (nbt.hasKey("fofArchive"))
        {
            archivedFoFIDs.clear();
            NBTTagCompound tag = nbt.getCompoundTag("fofArchive");
            int size = tag.getInteger("size");
            for (int i = 0; i < size; i++)
            {
                archivedFoFIDs.add(tag.getString("" + i));
            }

        }
        if (nbt.hasKey("globalAccessID"))
        {
            globalProfileID = nbt.getString("globalAccessID");
        }
        else if (nbt.hasKey("localProfile"))
        {
            profile = new AccessProfile(nbt.getCompoundTag("localProfile"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (!StringUtils.isNullOrEmpty(userFoFID))
        {
            nbt.setString("fofID", userFoFID);
        }
        if (!archivedFoFIDs.isEmpty())
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("size", archivedFoFIDs.size());
            for (int i = 0; i < archivedFoFIDs.size(); i++)
            {
                tag.setString("" + i, archivedFoFIDs.get(i));
            }
            nbt.setTag("fofArchive", tag);
        }
        if (StringUtils.isNullOrEmpty(globalProfileID))
        {
            if (profile != null)
            {
                nbt.setTag("localProfile", profile.save(new NBTTagCompound()));
            }
        }
        else
        {
            nbt.setString("globalAccessID", globalProfileID);
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerFoF(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return null;
    }

    @Override
    public void onMultiTileAdded(IMultiTile tileMulti)
    {

    }

    @Override
    public boolean onMultiTileBroken(IMultiTile tileMulti, Object source, boolean harvest)
    {
        if (!breaking && tileMulti instanceof TileEntity && ((TileEntity) tileMulti).xCoord == xCoord && ((TileEntity) tileMulti).yCoord == (yCoord + 1) && ((TileEntity) tileMulti).zCoord == zCoord)
        {
            breaking = true;
            Location loc = toLocation();
            if (harvest)
            {
                InventoryUtility.dropItemStack(loc, toItemStack());
            }
            loc.setBlockToAir();
            breaking = false;
        }
        return false;
    }

    @Override
    public void onRemove(Block block, int par6)
    {
        breaking = true;
        world().setBlockToAir(xCoord, yCoord + 1, zCoord);
        setAccessProfile(null);
        breaking = false;
    }

    @Override
    public void onTileInvalidate(IMultiTile tileMulti)
    {

    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, IPos3D hit)
    {
        return onPlayerActivated(player, side, hit instanceof Pos ? (Pos) hit : new Pos(hit));
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            openGui(player, ICBM.INSTANCE);
            if (player instanceof EntityPlayerMP)
            {
                Engine.instance.packetHandler.sendToPlayer(new PacketTile(this, 3, userFoFID), (EntityPlayerMP) player);
            }
        }
        return true;
    }

    @Override
    public void onMultiTileClicked(IMultiTile tile, EntityPlayer player)
    {

    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        return STRUCTURE;
    }

    @Override
    public AccessProfile getAccessProfile()
    {
        return profile;
    }

    @Override
    public void setAccessProfile(AccessProfile profile)
    {
        if (this.profile != null)
        {
            this.profile.removeContainer(this);
        }

        this.profile = profile;
        if (profile != null)
        {
            profile.addContainer(this);
        }
    }

    @Override
    public boolean canAccess(String username)
    {
        return getAccessProfile() == null || getAccessProfile().getUserAccess(username).hasNode(Permissions.machineOpen.toString());
    }

    @Override
    public boolean hasNode(EntityPlayer player, String node)
    {
        return getAccessProfile() == null || getAccessProfile().hasNode(player, node);
    }

    @Override
    public boolean hasNode(String username, String node)
    {
        return getAccessProfile() == null || getAccessProfile().hasNode(username, node);
    }

    @Override
    public void onProfileChange()
    {
        //TODO kick users out of GUI if they do not have access anymore
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockFoFStation), "RCR", "PRP", 'C', ICBM.blockSiloController, 'R', UniversalRecipe.CIRCUIT_T2.get(), 'P', UniversalRecipe.PRIMARY_PLATE.get()));
    }
}
