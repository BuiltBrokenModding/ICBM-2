package atomicscience.api;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import atomicscience.api.poison.PoisonRadiation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRadioactive extends Block
{
	public static int RECOMMENDED_ID = 3768;
	public boolean canSpread = true;
	public float radius = 5;
	public int amplifier = 2;
	public boolean canWalkPoison = true;
	public boolean isRandomlyRadioactive = true;
	public boolean spawnParticle = true;

	private Icon iconTop;
	private Icon iconBottom;

	public BlockRadioactive(int id, Material material)
	{
		super(id, material);
		this.setTickRandomly(true);
		this.setHardness(0.2F);
		this.setLightValue(0.1F);
	}

	public BlockRadioactive(int id)
	{
		this(id, Material.rock);
	}

	@Override
	public Icon getIcon(int side, int metadata)
	{
		return side == 1 ? this.iconTop : (side == 0 ? this.iconBottom : this.blockIcon);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		super.registerIcons(iconRegister);
		this.iconTop = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_top");
		this.iconBottom = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_bottom");
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		if (world.rand.nextFloat() > 0.8)
		{
			this.updateTick(world, x, y, z, world.rand);
		}
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		if (!world.isRemote)
		{
			if (this.isRandomlyRadioactive)
			{
				AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(x - this.radius, y - this.radius, z - this.radius, x + this.radius, y + this.radius, z + this.radius);
				List<EntityLivingBase> entitiesNearby = world.getEntitiesWithinAABB(EntityLivingBase.class, bounds);

				for (EntityLivingBase entity : entitiesNearby)
				{
					PoisonRadiation.INSTANCE.poisonEntity(new Vector3(x, y, z), entity, amplifier);
				}
			}

			if (this.canSpread)
			{
				for (int i = 0; i < 4; ++i)
				{
					int newX = x + rand.nextInt(3) - 1;
					int newY = y + rand.nextInt(5) - 3;
					int newZ = z + rand.nextInt(3) - 1;
					int blockID = world.getBlockId(newX, newY, newZ);

					if (rand.nextFloat() > 0.4 && (blockID == Block.tilledField.blockID || blockID == Block.grass.blockID))
					{
						world.setBlockMetadataWithNotify(newX, newY, newZ, this.blockID, 2);
					}
				}

				if (rand.nextFloat() > 0.85)
				{
					world.setBlockMetadataWithNotify(x, y, z, Block.mycelium.blockID, 2);
				}
			}
		}
	}

	/**
	 * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityWalking(World par1World, int x, int y, int z, Entity par5Entity)
	{
		if (par5Entity instanceof EntityLiving && this.canWalkPoison)
		{
			PoisonRadiation.INSTANCE.poisonEntity(new Vector3(x, y, z), (EntityLiving) par5Entity);
		}
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random par5Random)
	{
		if (this.spawnParticle)
		{
			if (Minecraft.getMinecraft().gameSettings.particleSetting == 0)
			{
				int radius = 3;

				for (int i = 0; i < 2; i++)
				{
					Vector3 diDian = new Vector3(x, y, z);

					diDian.x += Math.random() * radius - radius / 2;
					diDian.y += Math.random() * radius - radius / 2;
					diDian.z += Math.random() * radius - radius / 2;

					EntitySmokeFX fx = new EntitySmokeFX(world, diDian.x, diDian.y, diDian.z, (Math.random() - 0.5) / 2, (Math.random() - 0.5) / 2, (Math.random() - 0.5) / 2);
					fx.setRBGColorF(0.2f, 0.8f, 0);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
		}
	}
}
