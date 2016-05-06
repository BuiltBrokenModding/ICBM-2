package com.builtbroken.icbm.content.ams;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.content.fof.IFoFStation;
import com.builtbroken.icbm.content.launcher.controller.local.TileLocalController;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.icbm.content.prefab.ItemBlockICBM;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.radar.RadarRegistry;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import cpw.mods.fml.common.registry.GameRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/5/2016.
 */
public class TileAMS extends TileModuleMachine implements IPacketIDReceiver, IGuiTile, ILinkable, IPostInit
{
    protected static final double ROTATION_TIME = 500000000.0;

    /** Desired aim angle, updated every tick if target != null */
    protected final EulerAngle aim = new EulerAngle(0, 0, 0);
    /** Current aim angle, updated each tick */
    protected final EulerAngle currentAim = new EulerAngle(0, 0, 0);
    /** Default aim to use when not targeting things */
    protected final EulerAngle defaultAim = new EulerAngle(0, 0, 0);

    /** Current selector used to filter missiles */
    protected EntityTargetingSelector selector;
    /** Current target */
    protected Entity target = null;

    protected Cube fireArea = null;

    /** Location of FoF station */
    public Pos fofStationPos;
    /** Cached fof station tile */
    public IFoFStation fofStation;

    /** Last time rotation was updated, used in {@link EulerAngle#lerp(EulerAngle, double)} function for smooth rotation */
    protected long lastRotationUpdate = System.nanoTime();

    /** User set rotation yaw when target is null */
    protected int desiredRotationYaw = 0;
    /** User set rotation pitch when target is null */
    protected int desiredRotationPitch = 0;

    public TileAMS()
    {
        super("AMS", Material.iron);
        this.hardness = 15f;
        this.resistance = 50f;
        this.itemBlock = ItemBlockICBM.class;
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
            if (fireArea == null)
            {
                fireArea = new Cube(toPos().add(-100, -10, -100), toPos().add(100, 200, 100));
            }
            //Clear target if invalid
            if (target != null && (target.isDead || !selector.isEntityApplicable(target) || toPos().distance(target) > 200))
            {
                target = null;
            }
            //Get new target
            if (target == null)
            {
                target = getClosestTarget();
            }

            //Sound effect for rotation
            if (ticks % 10 == 0 && !aim.isWithin(currentAim, 5))
            {
                worldObj.playSoundEffect(x() + 0.5, y() + 0.2, z() + 0.5, "icbm:icbm.servo", ICBM.ams_rotation_volume, 1.0F);
            }

            //Update server rotation value, can be independent from client
            double deltaTime = (System.nanoTime() - lastRotationUpdate) / ROTATION_TIME;
            currentAim.lerp(aim, deltaTime).clampTo360();


            if (target != null)
            {
                //Updates aim point every 3 ticks
                if (ticks % 3 == 0)
                {
                    Pos aimPoint = new Pos(this.target);
                    aim.set(toPos().add(0.5).toEulerAngle(aimPoint).clampTo360());
                    sendAimPacket();
                }

                //Fires weapon, if aimed every, 10 ticks
                if (ticks % 5 == 0 && aim.isWithin(currentAim, 1))
                {
                    fireAt(target);
                }
            }
            else if (ticks % 3 == 0 && !aim.isZero())
            {
                aim.set(defaultAim);
                sendAimPacket();
            }
            lastRotationUpdate = System.nanoTime();
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
            float rand = world().rand.nextFloat();
            System.out.println(rand);
            if (rand >= 0.3)
            {
                if (target instanceof IMissileEntity)
                {
                    if (target instanceof EntityMissile || target instanceof EntityLivingBase)
                    {
                        target.attackEntityFrom(DamageSource.generic, 3 + world().rand.nextFloat() * 2f);
                    }
                    else
                    {
                        ((IMissileEntity) target).destroyMissile(this, DamageSource.generic, 0.1f, true, true, true);
                    }
                    sendPacket(new PacketTile(this, 2));
                    if (target.isDead)
                    {
                        this.target = null;
                        currentAim.setYaw(0);
                        currentAim.setPitch(0);
                        sendAimPacket();
                    }
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
        if (fireArea != null)
        {
            List<Entity> list = RadarRegistry.getAllLivingObjectsWithin(world(), fireArea, selector);
            if (!list.isEmpty())
            {
                //TODO find closest target
                //TODO line of sight check
                return list.get(0);
            }
        }
        return null;
    }

    public IFoFStation getFoFStation()
    {
        if ((fofStation == null || fofStation instanceof TileEntity && ((TileEntity) fofStation).isInvalid()) && fofStationPos != null)
        {
            TileEntity tile = fofStationPos.getTileEntity(world());
            if (tile instanceof IFoFStation)
            {
                fofStation = (IFoFStation) tile;
            }
            else
            {
                fofStationPos = null;
            }
        }
        return fofStation;
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
        if (distance(pos) > TileLocalController.MAX_LINK_DISTANCE)
        {
            return "link.error.pos.distance.max";
        }

        //Compare tile pass code
        TileEntity tile = pos.getTileEntity(loc.world());
        if (tile instanceof IPassCode && ((IPassCode) tile).getCode() != code)
        {
            return "link.error.code.match";
        }
        else if (tile instanceof IFoFStation)
        {
            IFoFStation station = getFoFStation();
            if (station == tile)
            {
                return "link.error.tile.already.added";
            }
            else
            {
                fofStation = (IFoFStation) tile;
                fofStationPos = new Pos(tile);
            }
            return "";
        }
        else
        {
            return "link.error.tile.invalid";
        }
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IWorldPosItem)
        {
            return false;
        }

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
        defaultAim.writeBytes(buf);
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
        if (nbt.hasKey("fofStationPos"))
        {
            fofStationPos = new Pos(nbt.getCompoundTag("fofStationPos"));
        }
        if(nbt.hasKey("defaultAim"))
        {
            defaultAim.readFromNBT(nbt.getCompoundTag("defaultAim"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setTag("aim", aim.toNBT());
        nbt.setTag("currentAim", currentAim.toNBT());
        nbt.setTag("defaultAim", defaultAim.toNBT());
        if (fofStationPos != null)
        {
            nbt.setTag("fofStationPos", fofStationPos.toNBT());
        }
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if(!super.read(buf, id, player, type))
        {
            if(id == 3)
            {
                currentAim.setYaw(buf.readFloat());
                currentAim.setPitch(buf.readFloat());
                return true;
            }
            return false;
        }
        return true;
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

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockAMS), "RPP", "RCP", "PTP", 'C', UniversalRecipe.CIRCUIT_T2.get(), 'P', UniversalRecipe.PRIMARY_PLATE.get(), 'R', OreNames.INGOT_IRON, 'T', Blocks.dropper));
    }
}
