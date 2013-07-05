package icbm.gangshao.damage;

import icbm.gangshao.turret.TPaoDaiBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Entity designed to take damage and apply it to the tile from an Entity. Simulates the tile is
 * alive and can be harmed by normal AIs without additional code.
 * 
 * @author DarkGuardsman
 * 
 */
public class EntityTileDamagable extends EntityLiving implements IEntityAdditionalSpawnData
{
	private TPaoDaiBase host;
	public int hp = 100;

	public EntityTileDamagable(World par1World)
	{
		super(par1World);
		this.isImmuneToFire = true;
		this.setSize(1.1F, 1.1F);
	}

	public EntityTileDamagable(TPaoDaiBase host)
	{
		this(host.worldObj);
		this.setPosition(host.xCoord + 0.5, host.yCoord, host.zCoord + 0.5);
		this.host = host;
		this.host.entityDamagable = this;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, int amount)
	{
		if (this.host instanceof IHealthTile)
		{
			return ((IHealthTile) this.host).onDamageTaken(source, amount);
		}
		else
		{
			if ((this.hp -= amount) <= 0)
			{
				if (this.host != null)
				{
					Vector3 vec = new Vector3(this.host.xCoord, this.host.yCoord, this.host.zCoord);
					int id = vec.getBlockID(this.worldObj);
					int meta = vec.getBlockID(this.worldObj);
					Block block = Block.blocksList[id];

					if (block != null)
					{
						block.breakBlock(this.worldObj, this.host.xCoord, this.host.yCoord, this.host.zCoord, id, meta);
					}

					vec.setBlock(this.worldObj, 0);
				}
				this.setDead();

			}
			return true;
		}
	}

	@Override
	public boolean isPotionApplicable(PotionEffect par1PotionEffect)
	{
		if (par1PotionEffect != null && this.host instanceof IHealthTile)
		{
			return ((IHealthTile) this.host).canApplyPotion(par1PotionEffect);
		}
		return false;
	}

	@Override
	public void addPotionEffect(PotionEffect par1PotionEffect)
	{
		if (this.isPotionApplicable(par1PotionEffect))
		{
			if (this.activePotionsMap.containsKey(Integer.valueOf(par1PotionEffect.getPotionID())))
			{
				((PotionEffect) this.activePotionsMap.get(Integer.valueOf(par1PotionEffect.getPotionID()))).combine(par1PotionEffect);
				this.onChangedPotionEffect((PotionEffect) this.activePotionsMap.get(Integer.valueOf(par1PotionEffect.getPotionID())));
			}
			else
			{
				this.activePotionsMap.put(Integer.valueOf(par1PotionEffect.getPotionID()), par1PotionEffect);
				this.onNewPotionEffect(par1PotionEffect);
			}
		}
	}

	@Override
	public String getEntityName()
	{
		return "EntityTileTarget";
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		if (this.host != null)
		{
			data.writeInt(this.host.xCoord);
			data.writeInt(this.host.yCoord);
			data.writeInt(this.host.zCoord);
		}
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		try
		{
			if (this.worldObj.getBlockTileEntity(data.readInt(), data.readInt(), data.readInt()) instanceof TPaoDaiBase)
			{
				this.host = (TPaoDaiBase) this.worldObj.getBlockTileEntity(data.readInt(), data.readInt(), data.readInt());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onUpdate()
	{
		if (!this.worldObj.isRemote)
		{
			if (this.host == null || this.host.isInvalid())
			{
				this.setDead();
			}
			else if (this.host instanceof IHealthTile && !((IHealthTile) this.host).isAlive())
			{
				this.setDead();
			}
			else
			{
				this.updatePotionEffects();
				this.setPosition(this.host.xCoord + 0.5, this.host.yCoord, this.host.zCoord + 0.5);
			}
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{

	}

	@Override
	public void moveEntity(double par1, double par3, double par5)
	{

	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{

	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity)
	{
		return AxisAlignedBB.getBoundingBox(this.posX - .6, this.posY - .6, this.posZ - .6, this.posX + .6, this.posY + .6, this.posZ + .6);
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderVec3D(Vec3 par1Vec3)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double par1)
	{
		return false;
	}

	@Override
	public void setVelocity(double par1, double par3, double par5)
	{

	}

	@Override
	public boolean isInsideOfMaterial(Material par1Material)
	{
		return false;
	}

	@Override
	public boolean interact(EntityPlayer player)
	{
		if (this.host != null && player != null)
		{
			Block block = Block.blocksList[this.worldObj.getBlockId(this.host.xCoord, this.host.yCoord, this.host.zCoord)];
			if (block != null)
			{
				return block.onBlockActivated(this.worldObj, this.host.xCoord, this.host.yCoord, this.host.zCoord, player, 0, 0, 0, 0);
			}
		}
		return false;
	}

	@Override
	public int getMaxHealth()
	{
		return this.host != null && host instanceof IHealthTile ? ((IHealthTile) host).getMaxHealth() : 100;
	}
}
