package com.builtbroken.icbm.content.missile.tile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.blast.IBlastTileMissile;
import com.builtbroken.icbm.api.blast.IExHandlerTileMissile;
import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.missile.ITileMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.api.edit.IWorldChangeAction;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileEnt;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public class TileCrashedMissile extends TileEnt implements IPacketIDReceiver, ITileMissile
{
    /** List of blocks to mimic as part of the model */
    public static List<Block> blocksToMimic = new ArrayList();

    static
    {
        blocksToMimic.add(Blocks.tallgrass);
        blocksToMimic.add(Blocks.snow_layer);
        blocksToMimic.add(Blocks.cake);
        blocksToMimic.add(ICBM.blockCake);
        blocksToMimic.add(Blocks.fire);
        blocksToMimic.add(Blocks.ice);
        blocksToMimic.add(Blocks.glass);
    }

    /** Missile object that defines render and blast information */
    public Missile missile;

    private ForgeDirection attachedSide;
    /** Render rotation yaw of the entity */
    private float yaw = 0;
    /** Render rotation pitch of the entity */
    private float pitch = -90;
    /** Slightly random offset to improve render randomization */
    private Pos posOffset = new Pos();

    /** Currently blast running in the missile */
    private IBlastTileMissile blast;
    /** Called to do the blast reguardless of what the blast code is */
    private boolean doBlast = false;
    /** Cause to use if triggering the blast */
    private TriggerCause cause;

    /** Amount of time to run the engine */
    private int ticksForEngine;
    /** Amount of time to generate smoke */
    private int ticksForSmoke;

    private Pos misislePos = toPos().add(0.5);

    private Block block;
    private int meta;


    //TODO add engine flames for a few seconds after landing
    //TODO generate smoke for a few mins after landing

    public TileCrashedMissile()
    {
        super("missile", Material.iron);
        this.hardness = 10f;
        this.resistance = 10f;
        this.itemBlock = ItemBlockMissile.class;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
        this.isOpaque = false;
        this.bounds = new Cube(0.3, 0, 0.3, .7, .7, .7);
    }

    @Override
    public Tile newTile()
    {
        return new TileCrashedMissile();
    }

    @Override
    public ItemStack toItemStack()
    {
        return missile != null ? missile.toStack() : null;
    }

    /**
     * Called to place this tile into the world from a missile entity
     *
     * @param missile
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public static void placeFromMissile(EntityMissile missile, World world, int x, int y, int z)
    {
        //TODO if block is a fluid place an entity version of this tile instead, fixed fluid renderer
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        while (y < 255 && !blocksToMimic.contains(block) && !(block.isAir(world, x, y, z) || block.isReplaceable(world, x, y, z)))
        {
            y += 1;
            block = world.getBlock(x, y, z);
            meta = world.getBlockMetadata(x, y, z);
        }
        if (block == Blocks.lava || block == Blocks.flowing_lava)
        {
            //Missile is destroyed
            return;
        }
        if (block instanceof IFluidBlock)
        {
            Fluid fluid = ((IFluidBlock) block).getFluid();
            if (fluid != null && fluid.getTemperature(world, x, y, z) > 1000)
            {
                //Missile is destroyed
                return;
            }
        }
        if (world.setBlock(x, y, z, ICBM.blockCrashMissile))
        {
            ICBM.INSTANCE.logger().info(String.format("Placed missile %d@dim %dx %dy %dz", world.provider.dimensionId, x, y, z));
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileCrashedMissile)
            {
                if (blocksToMimic.contains(block))
                {
                    ((TileCrashedMissile) tile).block = block;
                    ((TileCrashedMissile) tile).meta = meta;
                    switch (missile.sideTile)
                    {
                        case 0:
                            ((TileCrashedMissile) tile).posOffset.sub(0, block.getBlockBoundsMinY(), 0);
                            break;
                        case 1:
                            ((TileCrashedMissile) tile).posOffset.add(0, block.getBlockBoundsMaxY(), 0);
                            break;
                    }
                }
                if (missile.getMissile() != null)
                {
                    ((TileCrashedMissile) tile).missile = missile.getMissile();
                }
                ((TileCrashedMissile) tile).yaw = missile.rotationYaw;
                ((TileCrashedMissile) tile).pitch = missile.rotationPitch;
                ((TileCrashedMissile) tile).cause = new TriggerCause.TriggerCauseEntity(missile);
                ((TileCrashedMissile) tile).attachedSide = ForgeDirection.getOrientation(missile.sideTile);
            }
        }
        missile.setDead();
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        misislePos = misislePos.add(posOffset);
    }

    @Override
    public void update()
    {
        super.update();
        if (isServer())
        {
            //TODO implement gravity
            //TODO check if block inside moves
            //TODO allow to be pushed a little bit
            //TODO on punched by entity push a little
            if (blast == null && ticks % 20 == 0)
            {
                if (missile != null && missile.getWarhead() != null && missile.getWarhead().getExplosiveStack() != null)
                {
                    IExplosiveHandler handler = ExplosiveRegistry.get(missile.getWarhead().getExplosiveStack());

                    if (doBlast || handler instanceof IExHandlerTileMissile && ((IExHandlerTileMissile) handler).doesSpawnMissileTile(missile, null))
                    {
                        IWorldChangeAction action = handler.createBlastForTrigger(world(), xi() + 0.5, yi() + 0.5, zi() + 0.5, cause, missile.getWarhead().getExplosiveSize(), missile.getWarhead().getAdditionalExplosiveData());
                        if (action != null)
                        {
                            if (action instanceof IBlastTileMissile)
                            {
                                blast = (IBlastTileMissile) action;
                            }
                            else
                            {
                                missile.getWarhead().trigger(cause, world(), xi() + 0.5, yi() + 0.5, zi() + 0.5);
                            }
                        }
                    }
                }
            }

            //Tick the explosive
            if (blast != null)
            {
                blast.tickBlast(this, missile);
                if (blast.isCompleted())
                {
                    blast = null;
                    missile.getWarhead().setExplosiveStack(null);
                }
            }

            if (missile != null && missile.getEngine() != null)
            {

            }
            //TODO if block we are in is not solid fall
            //TODO if block is no full sized offset render pos and collision box
            if (ticks % 5 == 0)
            {
                if (attachedSide != null)
                {
                    Pos pos = toPos().add(attachedSide.getOpposite());
                    if (pos.isAirBlock(world()))
                    {
                        attemptToFall();
                    }
                }
                else
                {
                    attemptToFall();
                }
            }
        }
    }

    /**
     * Used to cause the block to drop a bit
     */
    protected void attemptToFall()
    {
        Pos pos = toPos().sub(0, 1, 0);
        if (pos.isAirBlock(world()))
        {
            EntityMissile missile = new EntityMissile(world());
            missile.setPosition(x() + 0.5, y() + 0.5, z() + 0.5);
            missile.setMissile(this.missile);
            missile.rotationYaw = yaw;
            missile.rotationPitch = pitch;
            toPos().setBlockToAir(world());
        }
        else
        {
            attachedSide = ForgeDirection.UP;
        }
    }

    @Override
    public Iterable<Cube> getCollisionBoxes(Cube intersect, Entity entity)
    {
        List<Cube> boxes = new ArrayList<>();
        boxes.add(getCollisionBounds());
        if (block != null)
        {
            AxisAlignedBB bb = block.getCollisionBoundingBoxFromPool(world(), xi(), yi(), zi());
            boxes.add(new Cube(bb).subtract(xi(), yi(), zi()));
        }
        return boxes;
    }

    @Override
    public boolean onPlayerActivated(EntityPlayer player, int side, Pos hit)
    {
        //Removed wrench support
        return onPlayerRightClick(player, side, hit);
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (block != null)
        {
            //TODO do collisions check for block,
        }
        if (isServer())
        {
            if (missile != null)
            {
                ItemStack stack = missile.toStack();

                if (player.inventory.addItemStackToInventory(stack))
                {
                    //stack size is only one so no need to do checks
                    toPos().setBlock(world(), block != null ? block : Blocks.air, block != null ? meta : 0);
                }
            }
        }
        return true;
    }

    @Override
    public boolean removeByPlayer(EntityPlayer player, boolean willHarvest)
    {
        //TODO chance to blow up missile
        if (missile != null && willHarvest)
        {
            ItemStack stack = missile.toStack();
            InventoryUtility.dropItemStack(world(), x() + 0.5, y() + 0.5, z() + 0.5, stack, 10, 0);
        }
        if (block != null)
        {
            return world().setBlock(xi(), yi(), zi(), block, meta, 3);
        }
        return world().setBlockToAir(xi(), yi(), zi());
    }

    @Override
    public void onCollide(Entity entity)
    {
        if (world().rand.nextFloat() <= 0.3 && missile.getWarhead() != null)
        {
            missile.getWarhead().trigger(new TriggerCause.TriggerCauseEntity(entity), world(), x() + 0.5, y() + 0.5, z() + 0.5);
        }
        else
        {
            //TODO push missile around
        }
    }

    @Override
    public boolean onPlayerLeftClick(EntityPlayer player)
    {
        if (world().rand.nextFloat() <= 0.3 && missile.getWarhead() != null)
        {
            missile.getWarhead().trigger(new TriggerCause.TriggerCauseEntity(player), world(), x() + 0.5, y() + 0.5, z() + 0.5);
        }
        else
        {
            //TODO push missile around
        }
        return false;
    }


    @Override
    public void onDestroyedByExplosion(Explosion ex)
    {
        //TODO attempt to set off warhead
        if (missile.getWarhead() != null)
        {
            missile.getWarhead().trigger(new TriggerCause.TriggerCauseExplosion(ex), world(), x() + 0.5, y() + 0.5, z() + 0.5);
        }
    }

    @Override
    public void onFillRain()
    {
        //TODO decrease smoke timer faster
        //TODO make pop noise like car engine cooling down
        //TODO make metal rain noise
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("missile"))
        {
            ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("missile"));
            missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
        }
        yaw = nbt.getFloat("yaw");
        pitch = nbt.getFloat("pitch");
        if (nbt.hasKey("offset"))
        {
            posOffset = new Pos(nbt.getCompoundTag("offset"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (missile != null)
        {
            ItemStack stack = missile.toStack();
            nbt.setTag("missile", stack.writeToNBT(new NBTTagCompound()));
        }
        nbt.setFloat("yaw", yaw);
        nbt.setFloat("pitch", pitch);
        if (posOffset != null)
        {
            nbt.setTag("offset", posOffset.toNBT());
        }
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        ItemStack stack = ByteBufUtils.readItemStack(buf);
        missile = stack.getItem() instanceof IMissileItem ? MissileModuleBuilder.INSTANCE.buildMissile(stack) : null;
        yaw = buf.readFloat();
        pitch = buf.readFloat();
        posOffset = new Pos(buf);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        if (missile != null)
        {
            ByteBufUtils.writeItemStack(buf, missile.toStack());
        }
        else
        {
            ByteBufUtils.writeItemStack(buf, new ItemStack(Items.stone_axe)); //random sync data, set the missile to null
        }
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        posOffset.writeByteBuf(buf);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        GL11.glPushMatrix();
        if (missile != null)
        {
            GL11.glTranslated(pos.x() + 0.5, pos.y() + (float) (missile.getHeight() / 2.0) - (float) (missile.getHeight() / 3.0), pos.z() + 0.5);
            GL11.glTranslated(posOffset.x(), posOffset.y(), posOffset.z());

            if (!(missile instanceof ICustomMissileRender) || !((ICustomMissileRender) missile).renderMissileInWorld(yaw - 90, pitch - 90, frame))
            {
                renderDefaultMissile();
            }
        }
        else
        {
            renderDefaultMissile();
        }
        GL11.glPopMatrix();
    }

    private final void renderDefaultMissile()
    {
        GL11.glTranslated(0.5, 2, 0.5);
        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(pitch - 90, 0.0F, 0.0F, 1.0F);
        GL11.glScalef(.5f, .5f, .5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.CLASSIC_MISSILE_TEXTURE);
        if (missile == null || missile.getWarhead() != null)
        {
            Assets.CLASSIC_MISSILE_MODEL.renderOnly("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
        }
        Assets.CLASSIC_MISSILE_MODEL.renderAllExcept("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
    }

    @Override
    public ArrayList<ItemStack> getDrops(int metadata, int fortune)
    {
        ArrayList<ItemStack> drops = new ArrayList();
        if (missile != null)
        {
            ItemStack m = missile.toStack();
            if (m != null && m.getItem() != null)
            {
                //TODO implement dropping scrap parts instead of full missile
                drops.add(m);
            }
        }
        return drops;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.iron_block.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int meta, int side)
    {
        return Blocks.iron_block.getIcon(0, 0);
    }

    public void setTextureName(String value)
    {
        textureName = value;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
    }
}
