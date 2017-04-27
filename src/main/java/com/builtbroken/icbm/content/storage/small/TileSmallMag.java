package com.builtbroken.icbm.content.storage.small;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.display.TileMissileContainer;
import com.builtbroken.icbm.content.storage.IMissileMag;
import com.builtbroken.icbm.content.storage.IMissileMagOutput;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.core.content.parts.CraftingParts;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.framework.multiblock.EnumMultiblock;
import com.builtbroken.mc.framework.multiblock.MultiBlockHelper;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Small 2 block tall self contained launcher for small missiles.
 * Created by robert on 3/28/2015.
 */
public class TileSmallMag extends TileMissileContainer implements ISimpleItemRenderer, IMultiTileHost, IPostInit, IMissileMag, IRecipeContainer
{
    protected static final ForgeDirection[] rotations = new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.WEST};
    public static final HashMap<IPos3D, String> tileMapCache = new HashMap();
    public static final int MISSILE_MOTION_DELAY = 40; //2 seconds

    //TODO add custom delay to allow sorting of missile
    //TODO add animation for missile movement
    static
    {
        tileMapCache.put(new Pos(0, 1, 0), EnumMultiblock.TILE.getTileName());
        tileMapCache.put(new Pos(0, 2, 0), EnumMultiblock.TILE.getTileName());
    }

    /** List of tiles to update */
    protected List<WeakReference<TileSmallMag>> updateList = new LinkedList();
    /** Tile to move the missile into */
    protected WeakReference<IMissileMagOutput> targetTile;


    /** Are we destroying the structure */
    private boolean _destroyingStructure = false;

    /** Current tick delay for motion. */
    private int movementDelay = 0;

    /** Is motion queued to run. */
    private boolean doMotion = false;

    private static final Cube blockRenderBounds = new Cube(0, 0, 0, 1, .2, 1);

    @SideOnly(Side.CLIENT)
    public static IIcon main;

    @SideOnly(Side.CLIENT)
    public static IIcon[] arrow;

    public TileSmallMag()
    {
        super("smallMissileMag", Material.iron);
        this.itemBlock = ItemBlockSmallSilo.class;
        this.isOpaque = false;
        this.renderTileEntity = true;
    }

    @Override
    protected IInventory createInventory()
    {
        return new TileModuleInventory(this, 1);
    }

    @Override
    public void onPostInit()
    {
        //GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockSmallSilo), "I I", "I I", "CBC", 'I', Blocks.iron_bars, 'B', OreNames.BLOCK_IRON, 'C', UniversalRecipe.CIRCUIT_T1.get()));
    }

    @Override
    public void update()
    {
        super.update();
        if (isServer())
        {
            if (ticks % 20 == 0)
            {
                queueMotion();
            }
            if (doMotion && movementDelay > -2 && movementDelay-- < 0)
            {
                movementDelay = -2;
                doMovement();
            }
        }
    }

    @Override
    public void onNeighborChanged(Block block)
    {
        super.onNeighborChanged(block);
        updateList.clear();
        final Location location = toLocation();
        for (ForgeDirection side : rotations)
        {
            final Location l = location.add(side);
            if (location.isChunkLoaded())
            {
                TileEntity tile = l.getTileEntity();
                if (tile instanceof TileSmallMag)
                {
                    updateList.add(new WeakReference<TileSmallMag>((TileSmallMag) tile));
                }
            }
        }
        queueMotion();
    }

    /**
     * Called to move the missile
     */
    protected void doMovement()
    {
        doMotion = false;
        if (getMissileItem() != null)
        {
            if (targetTile == null
                    || targetTile.get() == null
                    || targetTile.get() instanceof TileEntity && ((TileEntity) targetTile.get()).isInvalid()
                    || targetTile.get() instanceof ITileNode && !((ITileNode) targetTile.get()).getHost().isHostValid())
            {
                Location location = toLocation().add(getFacing());
                TileEntity tile = location.getTileEntity();
                if (tile instanceof IMissileMagOutput)
                {
                    targetTile = new WeakReference<IMissileMagOutput>((IMissileMagOutput) tile);
                }
                else if (tile instanceof ITileNodeHost)
                {
                    ITileNode node = ((ITileNodeHost) tile).getTileNode();
                    if (node instanceof IMissileMagOutput)
                    {
                        targetTile = new WeakReference<IMissileMagOutput>((IMissileMagOutput) node);
                    }
                }
            }
            if (targetTile != null)
            {
                IMissile missile = getMissile();
                if (targetTile.get().canAcceptMissile(missile) && targetTile.get().storeMissile(missile))
                {
                    //Remove our missile copy
                    setInventorySlotContents(0, null);

                    //Update nearby tiles
                    Iterator<WeakReference<TileSmallMag>> it = updateList.iterator();
                    while (it.hasNext())
                    {
                        WeakReference<TileSmallMag> ref = it.next();
                        if (ref == null || ref.get() == null || ref.get().isInvalid())
                        {
                            it.remove();
                        }
                        else
                        {
                            ref.get().queueMotion();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean onPlayerRightClickWrench(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            if (getFacing() == ForgeDirection.NORTH)
            {
                setFacing(ForgeDirection.EAST);
            }
            else if (getFacing() == ForgeDirection.EAST)
            {
                setFacing(ForgeDirection.SOUTH);
            }
            else if (getFacing() == ForgeDirection.SOUTH)
            {
                setFacing(ForgeDirection.WEST);
            }
            else if (getFacing() == ForgeDirection.WEST)
            {
                setFacing(ForgeDirection.NORTH);
            }
            if (isServer())
            {
                player.addChatComponentMessage(new ChatComponentText("Rotation set to " + getFacing().toString().toLowerCase()));
            }
            sendDescPacket();
        }
        return true;
    }

    public void queueMotion()
    {
        doMotion = true;
        if (movementDelay <= -2)
        {
            movementDelay = MISSILE_MOTION_DELAY;
        }
    }

    @Override
    public boolean canAcceptMissile(IMissile missile)
    {
        return super.canAcceptMissile(missile) && missile.getMissileSize() == MissileCasings.SMALL.ordinal();
    }

    @Override
    public Tile newTile()
    {
        return new TileSmallMag();
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.iron_block.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (side == 1)
        {
            if (getFacing() == ForgeDirection.NORTH)
            {
                return arrow[0];
            }
            else if (getFacing() == ForgeDirection.EAST)
            {
                return arrow[3];
            }
            else if (getFacing() == ForgeDirection.SOUTH)
            {
                return arrow[1];
            }
            else if (getFacing() == ForgeDirection.WEST)
            {
                return arrow[2];
            }
        }
        return main;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        main = iconRegister.registerIcon(ICBM.PREFIX + "PowerRailBody");
        final String[] sideName = new String[]{"Up", "Down", "Left", "Right"};

        arrow = new IIcon[sideName.length];
        for (int i = 0; i < sideName.length; i++)
        {
            arrow[i] = iconRegister.registerIcon(ICBM.PREFIX + "PowerRailArrow" + sideName[i]);
        }
    }

    @Override
    public String getInventoryName()
    {
        return "tile.icbm:smallSilo.container";
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glScaled(.8f, .8f, .8f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_SILO_TEXTURE);
        Assets.SMALL_SILO_MODEL.renderOnly("Group_001", "Component_1_001");
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new Cube(0, 0, 0, 1, 3, 1).add(x(), y(), z()).toAABB();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf(), pos.zf() + 0.5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        if (getFacing() == ForgeDirection.EAST || getFacing() == ForgeDirection.WEST)
        {
            GL11.glRotatef(90, 0, 1, 0);
        }
        Assets.SMALL_MAG_MODEL.renderAll();
        GL11.glPopMatrix();

        final IMissile missile = getMissile();
        //Render missile
        if (missile != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.4f, pos.zf() + 0.5f);
            if (missile instanceof ICustomMissileRender)
            {
                GL11.glTranslatef(0, (float) (missile.getHeight() / 2.0), 0);
                if (!((ICustomMissileRender) missile).renderMissileInWorld(0, 0, frame))
                {
                    //TODO Either error or render fake model
                }
            }
            GL11.glPopMatrix();
        }
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        MultiBlockHelper.buildMultiBlock(world(), this, true);
    }

    @Override
    public void onMultiTileAdded(IMultiTile tileMulti)
    {
        if (tileMulti instanceof TileEntity)
        {
            if (tileMapCache.containsKey(new Pos((TileEntity) this).sub(new Pos((TileEntity) tileMulti))))
            {
                tileMulti.setHost(this);
            }
        }
    }

    @Override
    public boolean onMultiTileBroken(IMultiTile tileMulti, Object source, boolean harvest)
    {
        if (!_destroyingStructure && tileMulti instanceof TileEntity)
        {
            Pos pos = new Pos((TileEntity) tileMulti).sub(new Pos((TileEntity) this));

            if (tileMapCache.containsKey(pos))
            {
                MultiBlockHelper.destroyMultiBlockStructure(this, harvest, false, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public boolean removeByPlayer(EntityPlayer player, boolean willHarvest)
    {
        MultiBlockHelper.destroyMultiBlockStructure(this, false, false, false);
        if (willHarvest && getMissileItem() != null)
        {
            InventoryUtility.dropItemStack(toLocation(), getMissileItem());
            setInventorySlotContents(0, null);
        }
        return super.removeByPlayer(player, willHarvest);
    }

    @Override
    public void onTileInvalidate(IMultiTile tileMulti)
    {

    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, float xHit, float yHit, float zHit)
    {
        return this.onPlayerRightClick(player, side, new Pos(xHit, yHit, zHit));
    }

    @Override
    public void onMultiTileClicked(IMultiTile tile, EntityPlayer player)
    {

    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        HashMap<IPos3D, String> map = new HashMap();
        Pos center = new Pos((TileEntity) this);
        for (Map.Entry<IPos3D, String> entry : tileMapCache.entrySet())
        {
            map.put(center.add(entry.getKey()), entry.getValue());
        }
        return map;
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        //TODO change so inventory can only be accessed from front and back of the tile
        //  With front as output, back as input
        super.onInventoryChanged(slot, prev, item);
        if (slot == 0)
        {
            queueMotion();
        }
    }

    @Override
    public Cube getBlockBounds()
    {
        return blockRenderBounds;
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        markRender();
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        recipes.add(newShapedRecipe(InventoryUtility.getBlock("icbm:smallMissileMag"), "ICI", "IMI", "GBG", 'I', Blocks.iron_bars, 'B', OreNames.PLATE_IRON, 'C', UniversalRecipe.CIRCUIT_T1.get(), 'M', CraftingParts.MOTOR.oreName, 'G', OreNames.GEAR_IRON));
    }
}