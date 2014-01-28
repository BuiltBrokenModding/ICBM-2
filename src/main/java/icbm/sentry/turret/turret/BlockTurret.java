package icbm.sentry.turret.turret;

import icbm.Reference;
import icbm.core.CreativeTabICBM;
import icbm.core.prefab.BlockICBM;
import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.SentryRegistry;
import icbm.sentry.turret.sentryhandler.Sentry;
import icbm.sentry.turret.tiles.TileSentry;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import universalelectricity.api.UniversalElectricity;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.prefab.block.BlockAdvanced;
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
        super(id, "turret", UniversalElectricity.machine);
        this.setCreativeTab(CreativeTabICBM.INSTANCE);
        this.setHardness(100f);
        this.setResistance(50f);
    }

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess world, int x, int y, int z)
    {
        TileEntity ent = world.getBlockTileEntity(x, y, z);
        if (ent instanceof TileSentry)
        {
            Entity dEnt = ((TileSentry) ent).getFakeEntity();
            if (dEnt != null)
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
    public void registerIcons (IconRegister iconRegister)
    {
        this.blockIcon = iconRegister.registerIcon(Reference.PREFIX + "machine");
    }

    @Override
    public boolean onUseWrench (World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof IBlockActivate)
        {
            return ((IBlockActivate) tileEntity).onActivated(entityPlayer);
        }

        return false;
    }

    @Override
    public boolean onMachineActivated (World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        /** Checks the TileEntity if it can activate. If not, then try to activate the turret
         * platform below it. */
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof IBlockActivate)
        {
            return ((IBlockActivate) tileEntity).onActivated(entityPlayer);
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
    public void onNeighborBlockChange (World world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof TileSentry)
        {
            if (!this.canBlockStay(world, x, y, z))
            {
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 1);
            }
        }
    }

    @Override
    public TileEntity createTileEntity (World world, int meta)
    {
        return new TileSentry();

    }

    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    @Override
    public int damageDropped (int metadata)
    {
        return metadata;
    }

    @Override
    public boolean canPlaceBlockAt (World world, int x, int y, int z)
    {
        return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z);
    }

    @Override
    public boolean canBlockStay (World world, int x, int y, int z)
    {
        return world.getBlockId(x, y - 1, z) == ICBMSentry.blockPlatform.blockID;
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs par2CreativeTabs, List list)
    {
        for (Class<? extends Sentry> clazz : SentryRegistry.getSentryList())
        {
            
        }

        for (Class<? extends Sentry> clazz : SentryRegistry.getSentryList())
        {
            if (clazz != null)
            {
                ItemStack stack = new ItemStack(this);
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setString("sentryID", clazz.getSimpleName());
                list.add(stack);

            }
        }

    }
}
