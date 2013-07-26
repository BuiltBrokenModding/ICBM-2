package icbm.zhapin.muoxing.daodan;

import icbm.core.base.MICBM;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MMFanWuSu extends MICBM
{
	// fields
	ModelRenderer Shape1;
	ModelRenderer Shape2;
	ModelRenderer Shape3;
	ModelRenderer Shape4;
	ModelRenderer Shape36;
	ModelRenderer Shape4a;
	ModelRenderer Shape5a;
	ModelRenderer Shape6a;
	ModelRenderer Shape6b;
	ModelRenderer Shape6c;
	ModelRenderer Shape6d;
	ModelRenderer Shape6e;
	ModelRenderer Shape6f;
	ModelRenderer Shape6g;
	ModelRenderer Shape6h;
	ModelRenderer Shape7;
	ModelRenderer Shape8;
	ModelRenderer Shape9;
	ModelRenderer Shape10a;
	ModelRenderer Shape10b;
	ModelRenderer Shape10c;
	ModelRenderer Shape10d;
	ModelRenderer Shape11a;
	ModelRenderer Shape11b;
	ModelRenderer Shape2e;
	ModelRenderer Shape12;
	ModelRenderer Shape13;
	ModelRenderer Shape14;

	public MMFanWuSu()
	{
		textureWidth = 128;
		textureHeight = 128;

		Shape1 = new ModelRenderer(this, 27, 0);
		Shape1.addBox(0F, 0F, 0F, 8, 10, 14);
		Shape1.setRotationPoint(-8F, 14F, -2F);
		Shape1.setTextureSize(128, 128);
		Shape1.mirror = true;
		setRotation(Shape1, 0F, 0.7853982F, 0F);
		Shape2 = new ModelRenderer(this, 27, 0);
		Shape2.addBox(0F, 0F, 0F, 8, 10, 14);
		Shape2.setRotationPoint(2F, 14F, -8F);
		Shape2.setTextureSize(128, 128);
		Shape2.mirror = true;
		setRotation(Shape2, 0F, -0.7853982F, 0F);
		Shape3 = new ModelRenderer(this, 36, 47);
		Shape3.addBox(0F, 0F, 0F, 21, 11, 2);
		Shape3.setRotationPoint(-8F, 13F, 7F);
		Shape3.setTextureSize(128, 128);
		Shape3.mirror = true;
		setRotation(Shape3, 0F, 0.7853982F, 0F);
		Shape4 = new ModelRenderer(this, 36, 47);
		Shape4.addBox(0F, 0F, 0F, 21, 11, 2);
		Shape4.setRotationPoint(-7F, 13F, -8F);
		Shape4.setTextureSize(128, 128);
		Shape4.mirror = true;
		setRotation(Shape4, 0F, -0.7853982F, 0F);
		Shape36 = new ModelRenderer(this, 0, 0);
		Shape36.addBox(0F, 0F, 0F, 6, 40, 6);
		Shape36.setRotationPoint(-3F, -26F, -3F);
		Shape36.setTextureSize(128, 128);
		Shape36.mirror = true;
		setRotation(Shape36, 0F, 0F, 0F);
		Shape4a = new ModelRenderer(this, 27, 25);
		Shape4a.addBox(0F, 0F, 0F, 10, 10, 10);
		Shape4a.setRotationPoint(-5F, 4F, -5F);
		Shape4a.setTextureSize(128, 128);
		Shape4a.mirror = true;
		setRotation(Shape4a, 0F, 0F, 0F);
		Shape5a = new ModelRenderer(this, 72, 31);
		Shape5a.addBox(0F, 0F, 0F, 8, 4, 8);
		Shape5a.setRotationPoint(-4F, -30F, -4F);
		Shape5a.setTextureSize(128, 128);
		Shape5a.mirror = true;
		setRotation(Shape5a, 0F, 0F, 0F);
		Shape6a = new ModelRenderer(this, 89, 8);
		Shape6a.addBox(0F, 0F, 0F, 2, 5, 1);
		Shape6a.setRotationPoint(-1F, -35F, 3F);
		Shape6a.setTextureSize(128, 128);
		Shape6a.mirror = true;
		setRotation(Shape6a, 0F, 0F, 0F);
		Shape6b = new ModelRenderer(this, 89, 8);
		Shape6b.addBox(0F, 0F, 0F, 1, 5, 2);
		Shape6b.setRotationPoint(3F, -35F, -1F);
		Shape6b.setTextureSize(128, 128);
		Shape6b.mirror = true;
		setRotation(Shape6b, 0F, 0F, 0F);
		Shape6c = new ModelRenderer(this, 89, 8);
		Shape6c.addBox(0F, 0F, 0F, 1, 5, 2);
		Shape6c.setRotationPoint(-4F, -35F, -1F);
		Shape6c.setTextureSize(128, 128);
		Shape6c.mirror = true;
		setRotation(Shape6c, 0F, 0F, 0F);
		Shape6d = new ModelRenderer(this, 89, 8);
		Shape6d.addBox(0F, 0F, 0F, 2, 5, 1);
		Shape6d.setRotationPoint(-1F, -35F, -4F);
		Shape6d.setTextureSize(128, 128);
		Shape6d.mirror = true;
		setRotation(Shape6d, 0F, 0F, 0F);
		Shape6e = new ModelRenderer(this, 103, 8);
		Shape6e.addBox(0F, 0F, 0F, 1, 5, 2);
		Shape6e.setRotationPoint(-2F, -39F, -1F);
		Shape6e.setTextureSize(128, 128);
		Shape6e.mirror = true;
		setRotation(Shape6e, 0F, 0F, 0.4014257F);
		Shape6f = new ModelRenderer(this, 103, 8);
		Shape6f.addBox(0F, 0F, 0F, 1, 5, 2);
		Shape6f.setRotationPoint(1F, -39F, -1F);
		Shape6f.setTextureSize(128, 128);
		Shape6f.mirror = true;
		setRotation(Shape6f, 0F, 0F, -0.4014257F);
		Shape6g = new ModelRenderer(this, 96, 8);
		Shape6g.addBox(0F, 0F, 0F, 2, 5, 1);
		Shape6g.setRotationPoint(-1F, -39F, -2F);
		Shape6g.setTextureSize(128, 128);
		Shape6g.mirror = true;
		setRotation(Shape6g, -0.4014257F, 0F, 0F);
		Shape6h = new ModelRenderer(this, 96, 8);
		Shape6h.addBox(0F, 0F, 0F, 2, 5, 1);
		Shape6h.setRotationPoint(-1F, -39F, 1F);
		Shape6h.setTextureSize(128, 128);
		Shape6h.mirror = true;
		setRotation(Shape6h, 0.4014257F, 0F, 0F);
		Shape7 = new ModelRenderer(this, 110, 0);
		Shape7.addBox(0F, 0F, 0F, 4, 1, 4);
		Shape7.setRotationPoint(-2F, -40F, -2F);
		Shape7.setTextureSize(128, 128);
		Shape7.mirror = true;
		setRotation(Shape7, 0F, 0F, 0F);
		Shape8 = new ModelRenderer(this, 110, 19);
		Shape8.addBox(0F, 0F, 0F, 4, 5, 4);
		Shape8.setRotationPoint(-2F, -35F, -2F);
		Shape8.setTextureSize(128, 128);
		Shape8.mirror = true;
		setRotation(Shape8, 0F, 0F, 0F);
		Shape9 = new ModelRenderer(this, 110, 8);
		Shape9.addBox(0F, 0F, 0F, 2, 8, 2);
		Shape9.setRotationPoint(-1F, -43F, -1F);
		Shape9.setTextureSize(128, 128);
		Shape9.mirror = true;
		setRotation(Shape9, 0F, 0F, 0F);
		Shape10a = new ModelRenderer(this, 72, 8);
		Shape10a.addBox(0F, 0F, 0F, 6, 6, 2);
		Shape10a.setRotationPoint(8F, -23F, 1F);
		Shape10a.setTextureSize(128, 128);
		Shape10a.mirror = true;
		// Wing 1
		setRotation(Shape10a, 0F, -3.141593F, 0.7853982F);
		Shape10b = new ModelRenderer(this, 72, 17);
		Shape10b.addBox(0F, 0F, 0F, 2, 6, 6);
		Shape10b.setRotationPoint(-1F, -23F, -8F);
		Shape10b.setTextureSize(128, 128);
		Shape10b.mirror = true;
		setRotation(Shape10b, 0.7853982F, 0F, 0F);
		Shape10c = new ModelRenderer(this, 72, 8);
		Shape10c.addBox(0F, 0F, 0F, 6, 6, 2);
		Shape10c.setRotationPoint(-8F, -23F, -1F);
		Shape10c.setTextureSize(128, 128);
		Shape10c.mirror = true;
		setRotation(Shape10c, 0F, 0F, -0.7853982F);
		Shape10d = new ModelRenderer(this, 72, 8);
		Shape10d.addBox(0F, 0F, 0F, 6, 6, 2);
		Shape10d.setRotationPoint(-1F, -23F, 8F);
		Shape10d.setTextureSize(128, 128);
		Shape10d.mirror = true;
		// Wing 2
		setRotation(Shape10d, 0F, 1.570796F, -0.7853982F);
		Shape11a = new ModelRenderer(this, 72, 0);
		Shape11a.addBox(0F, 0F, 0F, 16, 5, 2);
		Shape11a.setRotationPoint(-8F, -23F, -1F);
		Shape11a.setTextureSize(128, 128);
		Shape11a.mirror = true;
		setRotation(Shape11a, 0F, 0F, 0F);
		Shape11b = new ModelRenderer(this, 72, 0);
		Shape11b.addBox(0F, 0F, 0F, 16, 5, 2);
		Shape11b.setRotationPoint(-1F, -23F, 8F);
		Shape11b.setTextureSize(128, 128);
		Shape11b.mirror = true;
		setRotation(Shape11b, 0F, 1.570796F, 0F);
		Shape2e = new ModelRenderer(this, 0, 103);
		Shape2e.addBox(0F, 0F, 0F, 16, 10, 4);
		Shape2e.setRotationPoint(-8F, 14F, -2F);
		Shape2e.setTextureSize(128, 128);
		Shape2e.mirror = true;
		setRotation(Shape2e, 0F, 0F, 0F);
		Shape12 = new ModelRenderer(this, 0, 76);
		Shape12.addBox(0F, 0F, 0F, 4, 10, 16);
		Shape12.setRotationPoint(-2F, 14F, -8F);
		Shape12.setTextureSize(128, 128);
		Shape12.mirror = true;
		setRotation(Shape12, 0F, 0F, 0F);
		Shape13 = new ModelRenderer(this, 0, 47);
		Shape13.addBox(0F, 0F, 0F, 3, 14, 14);
		Shape13.setRotationPoint(6F, 13F, -8F);
		Shape13.setTextureSize(128, 128);
		Shape13.mirror = true;
		setRotation(Shape13, 0.7853982F, -0.7853982F, 0F);
		Shape14 = new ModelRenderer(this, 0, 47);
		Shape14.addBox(0F, 0F, 0F, 3, 14, 14);
		Shape14.setRotationPoint(8F, 13F, 6F);
		Shape14.setTextureSize(128, 128);
		Shape14.mirror = true;
		setRotation(Shape14, 0.7853982F, -2.356194F, 0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		this.render(f5);
	}

	@Override
	public void render(float f5)
	{
		Shape1.render(f5);
		Shape2.render(f5);
		Shape3.render(f5);
		Shape4.render(f5);
		Shape36.render(f5);
		Shape4a.render(f5);
		Shape5a.render(f5);
		Shape6a.render(f5);
		Shape6b.render(f5);
		Shape6c.render(f5);
		Shape6d.render(f5);
		Shape6e.render(f5);
		Shape6f.render(f5);
		Shape6g.render(f5);
		Shape6h.render(f5);
		Shape7.render(f5);
		Shape8.render(f5);
		Shape9.render(f5);
		Shape10a.render(f5);
		Shape10b.render(f5);
		Shape10c.render(f5);
		Shape10d.render(f5);
		Shape11a.render(f5);
		Shape11b.render(f5);
		Shape2e.render(f5);
		Shape12.render(f5);
		Shape13.render(f5);
		Shape14.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
