package icbm.cart;

import icbm.zhapin.ZhaPin;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.World;
import railcraft.common.api.carts.IPrimableCart;
import universalelectricity.prefab.Vector3;

public class EChe extends EntityMinecart implements IPrimableCart
{
    public int explosiveID = 0;
	private short fuse = 0;
	
	public EChe(World par1World, double par2, double par4, double par6, int par8)
	{
		super(par1World, par2, par4, par6, par8);
	}
	
	@Override
    public void onUpdate()
	{
		super.onUpdate();
		
		if(this.fuse < 1)
        {
	       this.explode();
        }
        else
        {
        	ZhaPin.list[explosiveID].onYinZha(this.worldObj, new Vector3(this.posX, this.posY, this.posZ), this.fuse);
        }
		
		this.fuse --;
	}
	
	@Override
	public void setPrimed(boolean primed)
	{
	}

	@Override
	public boolean isPrimed()
	{
		return fuse > 0;
	}

	@Override
	public short getFuse()
	{
		return this.fuse;
	}

	@Override
	public void setFuse(int fuse) 
	{
		this.fuse = (short)fuse;
	}

	@Override
	public float getBlastRadius()
	{
		return 0;
	}

	@Override
	public void setBlastRadius(float radius)
	{
		
	}

	@Override
	public void explode()
    {
    	 this.worldObj.spawnParticle("hugeexplosion", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
         ZhaPin.createBaoZha(this.worldObj, Vector3.get(this), this, this.explosiveID);
         this.setDead();
    }

	/**
	     public void renderTNTCart(EntityCartTNT cart, float light, float time) {
        GL11.glPushMatrix();
        loadTexture("/terrain.png");
        GL11.glTranslatef(0.0F, 0.3125F, 0.0F);
        GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
        EntityCartTNT tnt = (EntityCartTNT)cart;
        if(tnt.isPrimed() && ((float)tnt.getFuse() - time) + 1.0F < 10F) {
            float scale = 1.0F - (((float)tnt.getFuse() - time) + 1.0F) / 10F;
            if(scale < 0.0F) {
                scale = 0.0F;
            }
            if(scale > 1.0F) {
                scale = 1.0F;
            }
            scale *= scale;
            scale *= scale;
            scale = 1.0F + scale * 0.3F;
            GL11.glScalef(scale, scale, scale);
        }
        (new RenderBlocks()).renderBlockAsItem(Block.tnt, 0, light);
        if(tnt.isPrimed() && (tnt.getFuse() / 5) % 2 == 0) {
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 772);
            float alpha = (1.0F - (((float)tnt.getFuse() - time) + 1.0F) / 100F) * 0.8F;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
            renderBlocks.renderBlockAsItem(Block.tnt, 0, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
        }
        GL11.glPopMatrix();
    }
	 */
}
