package icbm.sentry.turret.block;

import icbm.Reference;
import icbm.TabICBM;
import icbm.core.prefab.BlockICBM;
import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.TurretRegistry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import calclavia.lib.access.Nodes;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.prefab.block.BlockAdvanced;
import calclavia.lib.prefab.item.ItemBlockSaved;
import calclavia.lib.utility.inventory.InventoryUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Block turret is a class used by all turrets. Each type of turret will have a different tile
 * entity.
 * 
 * @author Calclavia, tgame14 */
public class BlockTurret extends BlockICBM
{
    public BlockTurret(int id)
    {
        super(id, "turret");
        this.setCreativeTab(TabICBM.INSTANCE);
        this.setHardness(8f);
        this.setResistance(50f);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        TileEntity ent = world.getBlockTileEntity(x, y, z);

        if (ent instanceof TileTurret)
        {
            Entity fakeEntity = ((TileTurret) ent).getFakeEntity();

            if (fakeEntity != null)
            {
                this.setBlockBounds(.2f, 0, .2f, .8f, .4f, .8f);
            }
            else
            {
                this.setBlockBounds(.2f, 0, .2f, .8f, .8f, .8f);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        this.blockIcon = iconRegister.registerIcon(Reference.PREFIX + "machine");
    }

    @Override
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof IBlockActivate)
        {
            return ((IBlockActivate) tileEntity).onActivated(entityPlayer);
        }

        return false;
    }

    @Override
    public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileTurret)
        {
            if (((TileTurret) tile).canUse(Nodes.PROFILE_ADMIN, entityPlayer) || ((TileTurret) tile).canUse(Nodes.PROFILE_OWNER, entityPlayer))
                InventoryUtility.dropBlockAsItem(world, x, y, z, true);
        }
        return true;
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        /** Checks the TileEntity if it can activate. If not, then try to activate the turret
         * platform below it. */
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof IBlockActivate)
        {
            if (((IBlockActivate) tileEntity).onActivated(entityPlayer))
            {
                return true;
            }
        }

        int id = world.getBlockId(x, y - 1, z);
        Block block = Block.blocksList[id];

        if (block instanceof BlockAdvanced)
        {
            return ((BlockAdvanced) block).onMachineActivated(world, x, y - 1, z, entityPlayer, side, hitX, hitY, hitZ);
        }

        return false;
    }

    @Override
    public boolean onSneakMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        if (entityPlayer != null)
        {
            entityPlayer.openGui(ICBMSentry.INSTANCE, 1, world, x, y, z);
            return true;
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof TileTurret)
        {
            if (!canBlockStay(world, x, y, z))
            {
                InventoryUtility.dropBlockAsItem(world, x, y, z, true);
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileTurret();
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int damageDropped(int metadata)
    {
        return 0;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        return world.getBlockId(x, y - 1, z) == ICBMSentry.blockPlatform.blockID;
    }

    @Override
    public void getSubBlocks(int id, CreativeTabs creativeTab, List list)
    {
        for (Class<? extends Turret> sentry : TurretRegistry.getSentryMap().values())
            list.add(TurretRegistry.getItemStack(sentry));
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack)
    {
        if (!world.isRemote)
        {
            TileEntity te = world.getBlockTileEntity(x, y, z);
            if (te instanceof TileTurret && entity instanceof EntityPlayer)
            {
                ((TileTurret) te).getAccessProfile().setUserAccess(((EntityPlayer) entity).username, ((TileTurret) te).getAccessProfile().getOwnerGroup());
                ((TileTurret) te).onProfileChange();
            }
        }
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(ItemBlockSaved.getItemStackWithNBT(this, world, x, y, z));
        return ret;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        return ItemBlockSaved.getItemStackWithNBT(this, world, x, y, z);
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }
}
