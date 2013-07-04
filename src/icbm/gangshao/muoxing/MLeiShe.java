package icbm.gangshao.muoxing;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class MLeiShe extends ModelBase
{
	// fields
	ModelRenderer basePlate;
	ModelRenderer leftMount;
	ModelRenderer rightMount;
	ModelRenderer body;
	ModelRenderer bodyTop;
	ModelRenderer bodyRight;
	ModelRenderer bodyLeft;
	ModelRenderer leftBarrel;
	ModelRenderer rightBarrel;
	ModelRenderer l1;
	ModelRenderer l2;
	ModelRenderer l3;
	ModelRenderer l4;
	ModelRenderer l5;
	ModelRenderer r1;
	ModelRenderer r2;
	ModelRenderer r3;
	ModelRenderer r4;
	ModelRenderer r5;
	ModelRenderer lCap;
	ModelRenderer rCap;
	ModelRenderer lEar;
	ModelRenderer rEar;

	public MLeiShe()
	{
		textureWidth = 128;
		textureHeight = 128;

		basePlate = new ModelRenderer(this, 0, 67);
		basePlate.addBox(-5.5F, 0F, -5.5F, 11, 1, 11);
		basePlate.setRotationPoint(0F, 23F, 0F);
		basePlate.setTextureSize(128, 128);
		basePlate.mirror = true;
		setRotation(basePlate, 0F, 0F, 0F);
		leftMount = new ModelRenderer(this, 0, 53);
		leftMount.addBox(4F, -8F, -2F, 1, 8, 4);
		leftMount.setRotationPoint(0F, 23F, 0F);
		leftMount.setTextureSize(128, 128);
		leftMount.mirror = true;
		setRotation(leftMount, 0F, 0F, 0F);
		rightMount = new ModelRenderer(this, 11, 53);
		rightMount.addBox(-5F, -8F, -2F, 1, 8, 4);
		rightMount.setRotationPoint(0F, 23F, 0F);
		rightMount.setTextureSize(128, 128);
		rightMount.mirror = true;
		setRotation(rightMount, 0F, 0F, 0F);
		body = new ModelRenderer(this, 0, 37);
		body.addBox(-3F, -2.8F, -4F, 6, 5, 9);
		body.setRotationPoint(0F, 17F, 0F);
		body.setTextureSize(128, 128);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);
		bodyTop = new ModelRenderer(this, 36, 51);
		bodyTop.addBox(-2F, -4F, -3F, 4, 2, 7);
		bodyTop.setRotationPoint(0F, 17F, 0F);
		bodyTop.setTextureSize(128, 128);
		bodyTop.mirror = true;
		setRotation(bodyTop, 0F, 0F, 0F);
		bodyRight = new ModelRenderer(this, 31, 37);
		bodyRight.addBox(-4F, -2.5F, -3F, 1, 4, 7);
		bodyRight.setRotationPoint(0F, 17F, 0F);
		bodyRight.setTextureSize(128, 128);
		bodyRight.mirror = true;
		setRotation(bodyRight, 0F, 0F, 0F);
		bodyLeft = new ModelRenderer(this, 48, 37);
		bodyLeft.addBox(3F, -2.5F, -3F, 1, 4, 7);
		bodyLeft.setRotationPoint(0F, 17F, 0F);
		bodyLeft.setTextureSize(128, 128);
		bodyLeft.mirror = true;
		setRotation(bodyLeft, 0F, 0F, 0F);
		leftBarrel = new ModelRenderer(this, 29, 81);
		leftBarrel.addBox(2F, -1F, -17F, 1, 1, 13);
		leftBarrel.setRotationPoint(0F, 17F, 0F);
		leftBarrel.setTextureSize(128, 128);
		leftBarrel.mirror = true;
		setRotation(leftBarrel, 0F, 0F, 0F);
		rightBarrel = new ModelRenderer(this, 0, 81);
		rightBarrel.addBox(-3F, -1F, -17F, 1, 1, 13);
		rightBarrel.setRotationPoint(0F, 17F, 0F);
		rightBarrel.setTextureSize(128, 128);
		rightBarrel.mirror = true;
		setRotation(rightBarrel, 0F, 0F, 0F);
		l1 = new ModelRenderer(this, 1, 14);
		l1.addBox(0.9F, -2F, -6F, 3, 3, 3);
		l1.setRotationPoint(0F, 17F, 0F);
		l1.setTextureSize(128, 128);
		l1.mirror = true;
		setRotation(l1, 0F, 0F, 0F);
		l2 = new ModelRenderer(this, 9, 28);
		l2.addBox(1F, -2F, -8F, 3, 3, 1);
		l2.setRotationPoint(0F, 17F, 0F);
		l2.setTextureSize(128, 128);
		l2.mirror = true;
		setRotation(l2, 0F, 0F, 0F);
		l3 = new ModelRenderer(this, 9, 28);
		l3.addBox(1F, -2F, -10F, 3, 3, 1);
		l3.setRotationPoint(0F, 17F, 0F);
		l3.setTextureSize(128, 128);
		l3.mirror = true;
		setRotation(l3, 0F, 0F, 0F);
		l4 = new ModelRenderer(this, 9, 28);
		l4.addBox(1F, -2F, -12F, 3, 3, 1);
		l4.setRotationPoint(0F, 17F, 0F);
		l4.setTextureSize(128, 128);
		l4.mirror = true;
		setRotation(l4, 0F, 0F, 0F);
		l5 = new ModelRenderer(this, 9, 28);
		l5.addBox(1F, -2F, -14F, 3, 3, 1);
		l5.setRotationPoint(0F, 17F, 0F);
		l5.setTextureSize(128, 128);
		l5.mirror = true;
		setRotation(l5, 0F, 0F, 0F);
		r1 = new ModelRenderer(this, 14, 14);
		r1.addBox(-3.9F, -2F, -6F, 3, 3, 3);
		r1.setRotationPoint(0F, 17F, 0F);
		r1.setTextureSize(128, 128);
		r1.mirror = true;
		setRotation(r1, 0F, 0F, 0F);
		r2 = new ModelRenderer(this, 9, 28);
		r2.addBox(-4F, -2F, -8F, 3, 3, 1);
		r2.setRotationPoint(0F, 17F, 0F);
		r2.setTextureSize(128, 128);
		r2.mirror = true;
		setRotation(r2, 0F, 0F, 0F);
		r3 = new ModelRenderer(this, 9, 28);
		r3.addBox(-4F, -2F, -10F, 3, 3, 1);
		r3.setRotationPoint(0F, 17F, 0F);
		r3.setTextureSize(128, 128);
		r3.mirror = true;
		setRotation(r3, 0F, 0F, 0F);
		r4 = new ModelRenderer(this, 9, 28);
		r4.addBox(-4F, -2F, -12F, 3, 3, 1);
		r4.setRotationPoint(0F, 17F, 0F);
		r4.setTextureSize(128, 128);
		r4.mirror = true;
		setRotation(r4, 0F, 0F, 0F);
		r5 = new ModelRenderer(this, 9, 28);
		r5.addBox(-4F, -2F, -14F, 3, 3, 1);
		r5.setRotationPoint(0F, 17F, 0F);
		r5.setTextureSize(128, 128);
		r5.mirror = true;
		setRotation(r5, 0F, 0F, 0F);
		lCap = new ModelRenderer(this, 33, 25);
		lCap.addBox(1F, -2F, -20F, 3, 3, 4);
		lCap.setRotationPoint(0F, 17F, 0F);
		lCap.setTextureSize(128, 128);
		lCap.mirror = true;
		setRotation(lCap, 0F, 0F, 0F);
		rCap = new ModelRenderer(this, 18, 25);
		rCap.addBox(-4F, -2F, -20F, 3, 3, 4);
		rCap.setRotationPoint(0F, 17F, 0F);
		rCap.setTextureSize(128, 128);
		rCap.mirror = true;
		setRotation(rCap, 0F, 0F, 0F);
		lEar = new ModelRenderer(this, 28, 56);
		lEar.addBox(2F, -5F, -4F, 1, 5, 1);
		lEar.setRotationPoint(0F, 17F, 0F);
		lEar.setTextureSize(128, 128);
		lEar.mirror = true;
		setRotation(lEar, -0.6632251F, 0F, 0F);
		rEar = new ModelRenderer(this, 23, 56);
		rEar.addBox(-3F, -5F, -4F, 1, 5, 1);
		rEar.setRotationPoint(0F, 17F, 0F);
		rEar.setTextureSize(128, 128);
		rEar.mirror = true;
		setRotation(rEar, -0.6632251F, 0F, 0F);
	}

	public void renderYaw(float f5)
	{
		basePlate.render(f5);
		leftMount.render(f5);
		rightMount.render(f5);
	}

	public void renderYawPitch(float f5)
	{
		body.render(f5);
		bodyTop.render(f5);
		bodyRight.render(f5);
		bodyLeft.render(f5);
		leftBarrel.render(f5);
		rightBarrel.render(f5);
		l1.render(f5);
		l2.render(f5);
		l3.render(f5);
		l4.render(f5);
		l5.render(f5);
		r1.render(f5);
		r2.render(f5);
		r3.render(f5);
		r4.render(f5);
		r5.render(f5);
		lCap.render(f5);
		rCap.render(f5);
		lEar.render(f5);
		rEar.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
