package com.builtbroken.icbm.content.launcher;

import com.builtbroken.icbm.api.ILauncher;
import com.builtbroken.icbm.api.IMissileItem;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.display.TileMissileContainer;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.helper.MathUtility;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

/**
 * Prefab for all missile launchers and silos.
 * Created by robert on 1/18/2015.
 */
public abstract class TileAbstractLauncher extends TileMissileContainer implements ILauncher, IPacketIDReceiver, IPassCode, ILinkFeedback
{
    public Pos target = new Pos(0, -1, 0);
    protected short link_code;

    protected List<LauncherReport> launcherReports = new ArrayList();

    public TileAbstractLauncher(String name, Material mat, int slots)
    {
        super(name, mat, 1);
    }

    public void setTarget(Pos target)
    {
        this.target = target;
        sendPacketToServer(new PacketTile(this, 1, target));
    }

    @Override
    public short getCode()
    {
        if (link_code == 0)
            link_code = MathUtility.randomShort();
        return link_code;
    }

    @Override
    public void firstTick()
    {
        if (!target.isAboveBedrock())
            target = new Pos(this);
        if (link_code == 0)
            link_code = MathUtility.randomShort();
    }

    @Override
    public void update()
    {
        //TODO track location of missiles if enabled
        //TODO track ETA to target of missiles if enabled
        super.update();
        if (isServer())
        {
            if (ticks % 20 == 0)
            {
                if (world().isBlockIndirectlyGettingPowered(xi(), yi(), zi()))
                {
                    fireMissile(target);
                }
            }
        }
    }

    @Override
    public void doCleanupCheck()
    {
        //Cleans up the report list looking for broken reports, temp fix for NULL UUIDs
        List<LauncherReport> newList = new ArrayList();
        for (LauncherReport report : launcherReports)
        {
            if (report.entityUUID != null && report.missile != null)
            {
                newList.add(report);
            }
        }
        launcherReports.clear();
        launcherReports = newList;
    }


    public void fireMissile()
    {
        fireMissile(target);
    }

    public void fireMissile(final Pos target)
    {
        Missile missile = getMissile();
        if (missile != null)
        {
            if (isServer())
            {
                //Create and setup missile
                EntityMissile entity = new EntityMissile(world());
                entity.setMissile(missile);

                //Set location data
                Pos start = new Pos(this).add(getMissileLaunchOffset());
                entity.setPositionAndRotation(start.x(), start.y(), start.z(), 0, 0);
                entity.setVelocity(0, 2, 0);

                //Set target data
                entity.setTarget(target, true);
                entity.sourceOfProjectile = new Pos(this);

                //Spawn and start moving
                world().spawnEntityInWorld(entity);
                addLaunchReport(entity);

                entity.setIntoMotion();

                //Empty inventory slot
                this.setInventorySlotContents(0, null);
                sendDescPacket();
            }
            else
            {
                triggerLaunchingEffects();
            }
        }
    }

    protected void addLaunchReport(EntityMissile missile)
    {
        launcherReports.add(new LauncherReport(missile));
        if (launcherReports.size() > 20)
            launcherReports.remove(0);
    }

    /**
     * Called to ensure the missile doesn't clip the edge of a multi-block
     * structure that holds the missile.
     *
     * @return Position in relation to the launcher base, do not add location data
     */
    public Pos getMissileLaunchOffset()
    {
        return new Pos(0.5, 3, 0.5);
    }

    /**
     * Called to load up and populate some effects in addition to the missile's own
     * launching effects.
     */
    public void triggerLaunchingEffects()
    {
        //TODO add more effects
        for (int l = 0; l < 20; ++l)
        {
            double f = x() + 0.5 + 0.3 * (world().rand.nextFloat() - world().rand.nextFloat());
            double f1 = y() + 0.1 + 0.5 * (world().rand.nextFloat() - world().rand.nextFloat());
            double f2 = z() + 0.5 + 0.3 * (world().rand.nextFloat() - world().rand.nextFloat());
            world().spawnParticle("largesmoke", f, f1, f2, 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Called when the missile fired from this launcher impacts
     * the ground. Used for tracking information and feed back
     * for users.
     *
     * @param missile - entity fired by this launcher
     */
    public void onImpactOfMissile(EntityMissile missile)
    {
        if (isServer() && missile != null)
        {
            //TODO thin out list to only include active reports, or rather ones still waiting on death times to be added
            for (LauncherReport report : launcherReports)
            {
                if (report.entityUUID != null && report.entityUUID.getMostSignificantBits() == missile.getUniqueID().getMostSignificantBits())
                {
                    report.impacted = true;
                    break;
                }
            }
        }
    }

    public void onDeathOfMissile(EntityMissile missile)
    {
        if (isServer() && missile != null)
        {
            for (LauncherReport report : launcherReports)
            {
                if (report.entityUUID != null && report.entityUUID.getMostSignificantBits() == missile.getUniqueID().getMostSignificantBits())
                {
                    report.deathTime = System.nanoTime();
                    break;
                }
            }
        }
    }

    @Override
    public void onLinked(Location location)
    {

    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (id == 1)
        {
            this.target = new Pos(buf);
            return true;
        }
        return super.read(buf, id, player, type);
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        target = new Pos(buf);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        target.writeByteBuf(buf);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("target"))
            this.target = new Pos(nbt.getCompoundTag("target"));
        if (nbt.hasKey("link_code"))
            this.link_code = nbt.getShort("link_code");
        else
            this.link_code = (short) MathUtility.rand.nextInt(Short.MAX_VALUE);

        if (nbt.hasKey("launchReports"))
        {
            //Clear list to remove duplication
            launcherReports.clear();

            NBTTagList list = nbt.getTagList("launchReports", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                launcherReports.add(new LauncherReport(list.getCompoundTagAt(i)));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (target != null)
            nbt.setTag("target", target.toNBT());
        if (link_code != 0)
            nbt.setShort("link_code", link_code);

        if (launcherReports != null && launcherReports.size() > 0)
        {
            NBTTagList list = new NBTTagList();
            for (LauncherReport report : launcherReports)
            {
                list.appendTag(report.save());
            }
            nbt.setTag("launchReports", list);
        }
    }
}
