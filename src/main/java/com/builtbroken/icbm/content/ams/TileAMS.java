package com.builtbroken.icbm.content.ams;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.radar.RadarRegistry;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/5/2016.
 */
public class TileAMS extends TileModuleMachine implements IPacketIDReceiver, IGuiTile
{
    protected final double ROTATION_TIME = 50000000.0;
    protected final EulerAngle aim = new EulerAngle(0, 0, 0);
    protected final EulerAngle currentAim = new EulerAngle(0, 0, 0);

    protected EntityTargetingSelector selector;
    protected Entity target = null;

    long lastRotationUpdate = System.nanoTime();

    public TileAMS()
    {
        super("ICMBxAMS", Material.iron);
        this.hardness = 15f;
        this.resistance = 50f;
        this.itemBlock = ItemBlockAMS.class;
        this.renderNormalBlock = false;
        this.addInventoryModule(10);
    }

    @Override
    public void update()
    {
        super.update();
        if (isServer())
        {
            //Set selector if missing
            if (selector == null)
            {
                selector = new EntityTargetingSelector(this);
            }
            //Clear target if invalid
            if (target != null && target.isDead)
            {
                target = null;
            }
            //Get new target
            if (target == null)
            {
                target = getClosestTarget();
            }

            //Sound effect for rotation
            if (ticks % 5 == 0 && !aim.isWithin(currentAim, 5))
            {
                worldObj.playSoundEffect(x() + 0.5, y() + 0.2, z() + 0.5, "icbm:icbm.servo", ICBM.ams_rotation_volume, 1.0F);
            }

            //Update server rotation value, can be independent from client
            currentAim.lerp(aim, (System.nanoTime() - lastRotationUpdate) / ROTATION_TIME).clampTo360();
            lastRotationUpdate = System.nanoTime();

            if (target != null)
            {
                //Updates aim point every 3 ticks
                if (ticks % 3 == 0)
                {
                    Pos aimPoint = new Pos(this.target);
                    aim.set(toPos().add(0.5).toEulerAngle(aimPoint).clampTo360());
                    sendDescPacket();
                }

                //Fires weapon, if aimed every, 10 ticks
                if (ticks % 10 == 0 && aim.isWithin(currentAim, 1))
                {
                    fireAt(target);
                }
            }
        }
    }

    /**
     * Called to fire a shot at the target. Assumes the target is valid, in range, and in line of sight.
     *
     * @param target - target, no null
     */
    protected void fireAt(Entity target)
    {
        if (eatAmmo())
        {
            //TODO move to tip of gun for better effect
            worldObj.playSoundEffect(x() + 0.5, y() + 0.5, z() + 0.5, "icbm:icbm.gun", ICBM.ams_gun_volume, 1.0F);
            if (world().rand.nextFloat() > 0.4)
            {
                if (target instanceof IMissileEntity)
                {
                    if (target instanceof EntityMissile)
                    {
                        target.attackEntityFrom(DamageSource.generic, world().rand.nextFloat() * 5f);
                    }
                    else
                    {
                        ((IMissileEntity) target).destroyMissile(this, DamageSource.generic, 0.1f, true, true, true);
                    }
                    sendPacket(new PacketTile(this, 2));
                    this.target = null;
                    currentAim.setYaw(0);
                    currentAim.setPitch(0);
                    sendAimPacket();
                }
            }
        }
        else
        {
            worldObj.playSoundEffect(x() + 0.5, y() + 0.5, z() + 0.5, "icbm:icbm.gun.empty", ICBM.ams_gun_volume, 1.0F);
        }
    }

    protected void sendAimPacket()
    {
        sendPacket(new PacketTile(this, 3, aim));
    }

    /**
     * Called to consume ammo from the inventory
     *
     * @return true if ammo was consumed
     */
    protected boolean eatAmmo()
    {
        if (getInventory() instanceof TileModuleInventory && !((TileModuleInventory) getInventory()).isEmpty())
        {
            Iterator<Map.Entry<Integer, ItemStack>> it = ((TileModuleInventory) getInventory()).iterator();
            while (it.hasNext())
            {
                Map.Entry<Integer, ItemStack> e = it.next();
                if (e != null && e.getValue() != null)
                {
                    ItemStack stack = e.getValue();
                    if (isAmmo(stack))
                    {
                        stack.stackSize--;
                        return true;
                    }
                    if (stack.stackSize <= 0)
                    {
                        it.remove();
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the stack is valid ammo
     *
     * @param stack - compare stack, never null
     * @return true if stack is ammo
     */
    public boolean isAmmo(ItemStack stack)
    {
        if (stack.stackSize > 0)
        {
            for (int id : OreDictionary.getOreIDs(stack))
            {
                String name = OreDictionary.getOreName(id);
                if (name.startsWith("nugget"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get closest target to AA system that is valid. Uses an {@link net.minecraft.command.IEntitySelector} script to
     * find targets.
     *
     * @return valid target, or null if none found
     */
    protected Entity getClosestTarget()
    {
        List<Entity> list = RadarRegistry.getAllLivingObjectsWithin(world(), new Cube(toPos().add(-100, -10, -100), toPos().add(100, 200, 100)), selector);
        if (!list.isEmpty())
        {
            //TODO find closest target
            //TODO line of sight check
            return list.get(0);
        }
        return null;
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            openGui(player, ICBM.INSTANCE);
        }
        return true;
    }

    @Override
    public void onRemove(Block block, int par6)
    {
        if (isServer())
        {
            Location loc = toLocation();
            for (int slot = 0; slot < getInventory().getSizeInventory(); slot++)
            {
                ItemStack stack = getInventory().getStackInSlotOnClosing(slot);
                if (stack != null)
                {
                    InventoryUtility.dropItemStack(loc, stack);
                    getInventory().setInventorySlotContents(slot, null);
                }
            }
        }
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        aim.writeBytes(buf);
        currentAim.writeBytes(buf);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("aim"))
        {
            aim.readFromNBT(nbt.getCompoundTag("aim"));
        }
        if (nbt.hasKey("currentAim"))
        {
            currentAim.readFromNBT(nbt.getCompoundTag("currentAim"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setTag("aim", aim.toNBT());
        nbt.setTag("currentAim", currentAim.toNBT());
    }

    @Override
    public Tile newTile()
    {
        return new TileAMS();
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerAMSTurret(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return null;
    }
}
