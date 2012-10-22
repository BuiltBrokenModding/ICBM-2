package icbm.cart;

import icbm.ZhuYao;
import icbm.zhapin.ZhaPin;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import railcraft.common.api.carts.IPrimableCart;
import universalelectricity.prefab.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EChe extends EntityMinecart implements IPrimableCart, IEntityAdditionalSpawnData
{
    public int explosiveID = 0;
	public int fuse = -1;
	private boolean isPrimed = false;
	
	public EChe(World par1World)
    {
		super(par1World);
    }
	
	public EChe(World par1World, double x, double y, double z, int explosiveID)
	{
		super(par1World, x, y, z, 3);
		this.explosiveID = explosiveID;
		this.fuse = Math.max(ZhaPin.list[explosiveID].getYinXin(), 60);
	}
	
	@Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		data.writeInt(this.explosiveID);
		data.writeInt(this.fuse);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		this.explosiveID = data.readInt();
		this.fuse = data.readInt();
	}
	
	@Override
    public void onUpdate()
	{
		super.onUpdate();
		
		if(this.isPrimed)
		{
			if(this.fuse < 1)
	        {
		       this.explode();
	        }
	        else
	        {
	        	ZhaPin.list[explosiveID].onYinZha(this.worldObj, new Vector3(this.posX, this.posY, this.posZ), this.explosiveID);
	            this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 0.8D, this.posZ, 0.0D, 0.0D, 0.0D);
	        }
			
			this.fuse --;
		}
		else
		{
			if(this.worldObj.getBlockId((int)this.posX, (int)this.posY, (int)this.posZ) == Block.rail.blockID && this.worldObj.isBlockIndirectlyGettingPowered((int)this.posX, (int)this.posY, (int)this.posZ))
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
	public short getFuse()
	{
		return (short) this.fuse;
	}

	@Override
	public void setFuse(int fuse) 
	{
		if(fuse < 0)
		{
			this.fuse = ZhaPin.list[explosiveID].getYinXin();
		}
		else
		{
			this.fuse = fuse;
		}
	}

	@Override
	public float getBlastRadius()
	{
		return ZhaPin.list[explosiveID].getBanJing();
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
         ZhaPin.createBaoZha(this.worldObj, Vector3.get(this), this, this.explosiveID);
         this.setDead();
    }
	
	@Override
	public boolean interact(EntityPlayer par1EntityPlayer)
    {
		if(par1EntityPlayer.getCurrentEquippedItem() != null)
		{
			if(par1EntityPlayer.getCurrentEquippedItem().itemID == Item.flintAndSteel.shiftedIndex)
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
        items.add(new ItemStack(ZhuYao.itChe, 1, this.explosiveID));
        return items;
    }
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
		super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("explosiveID", this.explosiveID);
        par1NBTTagCompound.setInteger("fuse", this.fuse);
        par1NBTTagCompound.setBoolean("isPrimed", this.isPrimed);
    }
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
		super.readEntityFromNBT(par1NBTTagCompound);
		this.explosiveID = par1NBTTagCompound.getInteger("explosiveID");
		this.fuse = par1NBTTagCompound.getInteger("fuse");
		this.isPrimed = par1NBTTagCompound.getBoolean("isPrimed");
    }
}
