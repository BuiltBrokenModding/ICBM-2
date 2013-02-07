package icbm.explosion.cart;

import icbm.api.IExplosive;
import icbm.api.IExplosiveContainer;
import icbm.explosion.ZhuYao;
import icbm.explosion.zhapin.ZhaPin;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import railcraft.common.api.carts.IExplosiveCart;
import universalelectricity.core.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EChe extends EntityMinecart implements IExplosiveContainer, IExplosiveCart, IEntityAdditionalSpawnData
{
	public int haoMa = 0;
	public int yinXin = -1;
	private boolean isPrimed = false;

	public EChe(World par1World)
	{
		super(par1World);
	}

	public EChe(World par1World, double x, double y, double z, int explosiveID)
	{
		super(par1World, x, y, z, 3);
		this.haoMa = explosiveID;
		this.yinXin = Math.max(ZhaPin.list[explosiveID].getYinXin(), 60);
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		data.writeInt(this.haoMa);
		data.writeInt(this.yinXin);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		this.haoMa = data.readInt();
		this.yinXin = data.readInt();
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(20, (int) this.yinXin);
		this.dataWatcher.addObject(21, (byte) 0);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.worldObj.isRemote)
		{
			this.yinXin = this.dataWatcher.getWatchableObjectInt(20);

			if (this.dataWatcher.getWatchableObjectByte(21) > 0)
			{
				this.isPrimed = true;
			}
			else
			{
				this.isPrimed = false;
			}
		}
		else
		{
			this.dataWatcher.updateObject(20, this.yinXin);

			byte isPri = 0;

			if (this.isPrimed)
				isPri = 1;

			this.dataWatcher.updateObject(21, isPri);
		}

		if (this.isPrimed)
		{
			if (this.yinXin < 1)
			{
				this.explode();
			}
			else
			{
				ZhaPin.list[haoMa].onYinZha(this.worldObj, new Vector3(this.posX, this.posY, this.posZ), this.haoMa);
				this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 0.8D, this.posZ, 0.0D, 0.0D, 0.0D);
			}

			this.yinXin--;
		}
		else
		{
			if (this.worldObj.getBlockId((int) this.posX, (int) this.posY, (int) this.posZ) == Block.rail.blockID && this.worldObj.isBlockIndirectlyGettingPowered((int) this.posX, (int) this.posY, (int) this.posZ))
			{
				this.setPrimed(true);
			}
		}
	}

	@Override
	public void setPrimed(boolean primed)
	{
		this.isPrimed = primed;
	}

	@Override
	public boolean isPrimed()
	{
		return this.isPrimed;
	}

	@Override
	public int getFuse()
	{
		return this.yinXin;
	}

	@Override
	public void setFuse(int fuse)
	{
		if (fuse < 0)
		{
			this.yinXin = ZhaPin.list[haoMa].getYinXin();
		}
		else
		{
			this.yinXin = fuse;
		}
	}

	@Override
	public float getBlastRadius()
	{
		return ZhaPin.list[haoMa].getRadius();
	}

	@Override
	public void setBlastRadius(float radius)
	{
		FMLLog.severe("Tried to set a blast radius to an ICBM cart! This does not work!");
	}

	@Override
	public void explode()
	{
		this.worldObj.spawnParticle("hugeexplosion", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
		ZhaPin.createBaoZha(this.worldObj, new Vector3(this), this, this.haoMa);
		this.setDead();
	}

	@Override
	public boolean interact(EntityPlayer par1EntityPlayer)
	{
		if (par1EntityPlayer.getCurrentEquippedItem() != null)
		{
			if (par1EntityPlayer.getCurrentEquippedItem().itemID == Item.flintAndSteel.itemID)
			{
				this.setPrimed(true);
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ItemStack> getItemsDropped()
	{
		List<ItemStack> items = new ArrayList<ItemStack>();

		if (!this.isPrimed)
		{
			items.add(new ItemStack(ZhuYao.itChe, 1, this.haoMa));
		}

		return items;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("haoMa", this.haoMa);
		par1NBTTagCompound.setInteger("yinXin", this.yinXin);
		par1NBTTagCompound.setBoolean("isPrimed", this.isPrimed);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readEntityFromNBT(par1NBTTagCompound);
		this.haoMa = par1NBTTagCompound.getInteger("haoMa");
		this.yinXin = par1NBTTagCompound.getInteger("yinXin");
		this.isPrimed = par1NBTTagCompound.getBoolean("isPrimed");
	}

	@Override
	public IExplosive getExplosiveType()
	{
		return ZhaPin.list[this.haoMa];
	}
}
