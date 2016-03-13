package com.builtbroken.icbm.content.launcher.fof;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.IFoF;
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
import com.builtbroken.mc.lib.access.AccessProfile;
import com.builtbroken.mc.lib.access.GlobalAccessSystem;
import com.builtbroken.mc.lib.access.IProfileContainer;
import com.builtbroken.mc.lib.access.Permissions;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import com.builtbroken.mc.prefab.tile.multiblock.EnumMultiblock;
import com.builtbroken.mc.prefab.tile.multiblock.MultiBlockHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Friend or foe controller, used to sync FoF tags between launchers, AMS, and other tiles.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2016.
 */
public class TileFoF extends TileModuleMachine implements IGuiTile, IMultiTileHost, IFoFStation, IPacketIDReceiver, IProfileContainer
{
    private static final HashMap<IPos3D, String> STRUCTURE = new HashMap();

    static
    {
        STRUCTURE.put(new Pos(0, 1, 0), EnumMultiblock.TILE.getName());
    }

    /** Main ID used for FoF system */
    protected String userFoFID;
    /** Archive of past FoF ids that should still be considered active but will not be applied to new objects. */
    protected List<String> archivedFoFIDs = new ArrayList();

    private boolean breaking = false;
    private AccessProfile profile;
    private String globalProfileID;

    public TileFoF()
    {
        super("ICBMxFoF", Material.iron);
        this.hardness = 15f;
        this.resistance = 50f;
        //this.renderNormalBlock = false;
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
                userFoFID = "";
                //Generate random default string
                int[] l = MathHelper.generateRandomIntArray(world().rand, EnglishLetters.values().length - 1 + 10, 10 + world().rand.nextInt(20));
                for (int i : l)
                {
                    if (i <= 10)
                    {
                        userFoFID += i - 1;
                    }
                    else if (world().rand.nextBoolean())
                    {
                        userFoFID += EnglishLetters.values()[i - 10].name();
                    }
                    else
                    {
                        userFoFID += EnglishLetters.values()[i - 10].name().toLowerCase();
                    }
                }
            }
            MultiBlockHelper.buildMultiBlock(world(), this);
        }
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (isServer())
            {
                //Set FoF ID
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
                //Enable permission system
                else if (id == 3)
                {
                    if (buf.readBoolean())
                    {
                        initProfile();
                    }
                    else
                    {
                        profile = null;
                        globalProfileID = null;
                    }
                    sendPacketToGuiUsers(new PacketTile(this, 1, "confirm"));
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
            }
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
            return ((IFoF) entity).getFoFTag() != null && ((IFoF) entity).getFoFTag().equals(getProvidedFoFTag());
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
        if (userFoFID != null && !userFoFID.isEmpty())
        {
            nbt.setString("fofID", userFoFID);
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
            profile.removeContainer(this);
        }
        if (profile != null)
        {
            profile.addContainer(this);
        }
        this.profile = profile;
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
}
