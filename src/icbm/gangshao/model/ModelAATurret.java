package icbm.gangshao.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelAATurret extends ModelBase
{
	// fields
	ModelRenderer rot1;
	ModelRenderer radarNeck;
	ModelRenderer radarBase;
	ModelRenderer radarBack;
	ModelRenderer radarRight;
	ModelRenderer radarLeft;
	ModelRenderer baseEdge4;
	ModelRenderer base;
	ModelRenderer baseEdge3;
	ModelRenderer baseEdge1;
	ModelRenderer baseEdge2;
	ModelRenderer body;
	ModelRenderer bodyFace;
	ModelRenderer bodyTop;
	ModelRenderer bodyEdge;
	ModelRenderer bodyArmRight;
	ModelRenderer bodyBack;
	ModelRenderer bodyBubble;
	ModelRenderer bodyArmLeft;
	ModelRenderer cannonRight;
	ModelRenderer cannonFaceRight;
	ModelRenderer cannonBarrelTopRight;
	ModelRenderer cannonBarrelBotRight;
	ModelRenderer cannonCapTopRight;
	ModelRenderer cannonCapBotRight;
	ModelRenderer cannonInFaceRight;
	ModelRenderer cannonBarrelCouple;
	ModelRenderer cannonBarrelNeck;
	ModelRenderer cannonCapBotLeft;
	ModelRenderer cannonCapTopLeft;
	ModelRenderer cannonFaceLeft;
	ModelRenderer cannonLeft;
	ModelRenderer cannonInFaceLeft;
	ModelRenderer cannonBarrelNeckLeft;
	ModelRenderer cannonBarrelBotLeft;
	ModelRenderer cannonBarrelCoupleLeft;
	ModelRenderer cannonBarrelTopLeft;

	public ModelAATurret()
	{
		textureWidth = 128;
		textureHeight = 128;

		rot1 = new ModelRenderer(this, 0, 0);
		rot1.addBox(-8F, 0F, -8F, 16, 2, 18);
		rot1.setRotationPoint(0F, 19F, 0F);
		rot1.setTextureSize(128, 128);
		rot1.mirror = true;
		setRotation(rot1, 0F, 0F, 0F);
		radarNeck = new ModelRenderer(this, 69, 0);
		radarNeck.addBox(-1.5F, -1F, -0.5F, 3, 3, 2);
		radarNeck.setRotationPoint(0F, -3F, 2F);
		radarNeck.setTextureSize(128, 128);
		radarNeck.mirror = true;
		setRotation(radarNeck, 0F, 0F, 0F);
		radarBase = new ModelRenderer(this, 81, 0);
		radarBase.addBox(-3F, -1F, -3F, 6, 1, 6);
		radarBase.setRotationPoint(0F, 0F, 2F);
		radarBase.setTextureSize(128, 128);
		radarBase.mirror = true;
		setRotation(radarBase, 0F, 0F, 0F);
		radarBack = new ModelRenderer(this, 68, 6);
		radarBack.addBox(-2.5F, -1F, -0.5F, 5, 3, 1);
		radarBack.setRotationPoint(0F, -4F, 2F);
		radarBack.setTextureSize(128, 128);
		radarBack.mirror = true;
		setRotation(radarBack, 0F, 0F, 0F);
		radarRight = new ModelRenderer(this, 68, 6);
		radarRight.addBox(0.5F, -1F, 1.5F, 5, 3, 1);
		radarRight.setRotationPoint(0F, -4F, 2F);
		radarRight.setTextureSize(128, 128);
		radarRight.mirror = true;
		setRotation(radarRight, 0F, 0.7853982F, 0F);
		radarRight.mirror = false;
		radarLeft = new ModelRenderer(this, 68, 6);
		radarLeft.addBox(-5.5F, -1F, 1.5F, 5, 3, 1);
		radarLeft.setRotationPoint(0F, -4F, 2F);
		radarLeft.setTextureSize(128, 128);
		radarLeft.mirror = true;
		setRotation(radarLeft, 0F, -0.7853982F, 0F);
		baseEdge4 = new ModelRenderer(this, 73, 70);
		baseEdge4.addBox(-7F, 0F, -11F, 14, 3, 2);
		baseEdge4.setRotationPoint(0F, 21F, 0F);
		baseEdge4.setTextureSize(128, 128);
		baseEdge4.mirror = true;
		setRotation(baseEdge4, 0F, 3.141593F, 0F);
		base = new ModelRenderer(this, 0, 70);
		base.addBox(-9F, 0F, -9F, 18, 3, 18);
		base.setRotationPoint(0F, 21F, 0F);
		base.setTextureSize(128, 128);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
		baseEdge3 = new ModelRenderer(this, 73, 70);
		baseEdge3.addBox(-7F, 0F, -11F, 14, 3, 2);
		baseEdge3.setRotationPoint(0F, 21F, 0F);
		baseEdge3.setTextureSize(128, 128);
		baseEdge3.mirror = true;
		setRotation(baseEdge3, 0F, -1.570796F, 0F);
		baseEdge1 = new ModelRenderer(this, 73, 70);
		baseEdge1.addBox(-7F, 0F, -11F, 14, 3, 2);
		baseEdge1.setRotationPoint(0F, 21F, 0F);
		baseEdge1.setTextureSize(128, 128);
		baseEdge1.mirror = true;
		setRotation(baseEdge1, 0F, 0F, 0F);
		baseEdge2 = new ModelRenderer(this, 73, 70);
		baseEdge2.addBox(-7F, 0F, -11F, 14, 3, 2);
		baseEdge2.setRotationPoint(0F, 21F, 0F);
		baseEdge2.setTextureSize(128, 128);
		baseEdge2.mirror = true;
		setRotation(baseEdge2, 0F, 1.570796F, 0F);
		body = new ModelRenderer(this, 0, 92);
		body.addBox(-7F, 0F, -6F, 14, 17, 16);
		body.setRotationPoint(0F, 2F, 0F);
		body.setTextureSize(128, 128);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);
		bodyFace = new ModelRenderer(this, 61, 106);
		bodyFace.addBox(-5F, 0F, -8F, 10, 17, 2);
		bodyFace.setRotationPoint(0F, 2F, 0F);
		bodyFace.setTextureSize(128, 128);
		bodyFace.mirror = true;
		setRotation(bodyFace, 0F, 0F, 0F);
		bodyTop = new ModelRenderer(this, 73, 85);
		bodyTop.addBox(-5F, -2F, -7F, 10, 2, 16);
		bodyTop.setRotationPoint(0F, 2F, 0F);
		bodyTop.setTextureSize(128, 128);
		bodyTop.mirror = true;
		setRotation(bodyTop, 0F, 0F, 0F);
		bodyEdge = new ModelRenderer(this, 65, 126);
		bodyEdge.addBox(-4.5F, -1F, -7F, 9, 1, 1);
		bodyEdge.setRotationPoint(0F, 2F, 0F);
		bodyEdge.setTextureSize(128, 128);
		bodyEdge.mirror = true;
		setRotation(bodyEdge, 0F, 0F, 0F);
		bodyArmRight = new ModelRenderer(this, 86, 104);
		bodyArmRight.addBox(-1F, -3.5F, -3.5F, 2, 16, 7);
		bodyArmRight.setRotationPoint(-8F, 6F, 1F);
		bodyArmRight.setTextureSize(128, 128);
		bodyArmRight.mirror = true;
		setRotation(bodyArmRight, 0F, 0F, 0F);
		bodyBack = new ModelRenderer(this, 61, 104);
		bodyBack.addBox(-5F, 0F, 10F, 10, 19, 2);
		bodyBack.setRotationPoint(0F, 2F, 0F);
		bodyBack.setTextureSize(128, 128);
		bodyBack.mirror = true;
		setRotation(bodyBack, 0F, 0F, 0F);
		bodyBubble = new ModelRenderer(this, 105, 106);
		bodyBubble.addBox(-4F, 3F, -10F, 8, 8, 2);
		bodyBubble.setRotationPoint(0F, 2F, 0F);
		bodyBubble.setTextureSize(128, 128);
		bodyBubble.mirror = true;
		setRotation(bodyBubble, 0F, 0F, 0F);
		bodyArmLeft = new ModelRenderer(this, 86, 104);
		bodyArmLeft.addBox(-1F, -3.5F, -3.5F, 2, 16, 7);
		bodyArmLeft.setRotationPoint(8F, 6F, 1F);
		bodyArmLeft.setTextureSize(128, 128);
		bodyArmLeft.mirror = true;
		setRotation(bodyArmLeft, 0F, 0F, 0F);
		cannonRight = new ModelRenderer(this, 69, 11);
		cannonRight.addBox(-4F, -5.5F, -10.5F, 4, 11, 20);
		cannonRight.setRotationPoint(-10F, 7F, 1F);
		cannonRight.setTextureSize(128, 128);
		cannonRight.mirror = true;
		setRotation(cannonRight, -0.6108652F, 0F, 0F);
		cannonFaceRight = new ModelRenderer(this, 0, 43);
		cannonFaceRight.addBox(-5F, -4F, -9.5F, 1, 8, 18);
		cannonFaceRight.setRotationPoint(-5F, 7F, 1F);
		cannonFaceRight.setTextureSize(128, 128);
		cannonFaceRight.mirror = true;
		setRotation(cannonFaceRight, -0.6108652F, 0F, 0F);
		cannonBarrelTopRight = new ModelRenderer(this, 42, 52);
		cannonBarrelTopRight.addBox(-3F, -4F, -25.5F, 2, 2, 15);
		cannonBarrelTopRight.setRotationPoint(-10F, 7F, 1F);
		cannonBarrelTopRight.setTextureSize(128, 128);
		cannonBarrelTopRight.mirror = true;
		setRotation(cannonBarrelTopRight, -0.6108652F, 0F, 0F);
		cannonBarrelBotRight = new ModelRenderer(this, 42, 52);
		cannonBarrelBotRight.addBox(-3F, 1F, -25.5F, 2, 2, 15);
		cannonBarrelBotRight.setRotationPoint(-10F, 7F, 1F);
		cannonBarrelBotRight.setTextureSize(128, 128);
		cannonBarrelBotRight.mirror = true;
		setRotation(cannonBarrelBotRight, -0.6108652F, 0F, 0F);
		cannonCapTopRight = new ModelRenderer(this, 73, 77);
		cannonCapTopRight.addBox(-3.5F, -4.5F, -29.5F, 3, 3, 4);
		cannonCapTopRight.setRotationPoint(-10F, 7F, 1F);
		cannonCapTopRight.setTextureSize(128, 128);
		cannonCapTopRight.mirror = true;
		setRotation(cannonCapTopRight, -0.6108652F, 0F, 0F);
		cannonCapBotRight = new ModelRenderer(this, 73, 77);
		cannonCapBotRight.addBox(-3.5F, 0.5F, -29.5F, 3, 3, 4);
		cannonCapBotRight.setRotationPoint(-10F, 7F, 1F);
		cannonCapBotRight.setTextureSize(128, 128);
		cannonCapBotRight.mirror = true;
		setRotation(cannonCapBotRight, -0.6108652F, 0F, 0F);
		cannonInFaceRight = new ModelRenderer(this, 77, 43);
		cannonInFaceRight.addBox(-5F, -4F, -9.5F, 1, 8, 18);
		cannonInFaceRight.setRotationPoint(-10F, 7F, 1F);
		cannonInFaceRight.setTextureSize(128, 128);
		cannonInFaceRight.mirror = true;
		setRotation(cannonInFaceRight, -0.6108652F, 0F, 0F);
		cannonBarrelCouple = new ModelRenderer(this, 89, 77);
		cannonBarrelCouple.addBox(-2.5F, -4F, -14.5F, 1, 4, 3);
		cannonBarrelCouple.setRotationPoint(-10F, 9F, 1F);
		cannonBarrelCouple.setTextureSize(128, 128);
		cannonBarrelCouple.mirror = true;
		setRotation(cannonBarrelCouple, -0.6108652F, 0F, 0F);
		cannonBarrelNeck = new ModelRenderer(this, 106, 70);
		cannonBarrelNeck.addBox(-3.5F, -6F, -13.3F, 3, 8, 2);
		cannonBarrelNeck.setRotationPoint(-10F, 9F, 1F);
		cannonBarrelNeck.setTextureSize(128, 128);
		cannonBarrelNeck.mirror = true;
		setRotation(cannonBarrelNeck, -0.6108652F, 0F, 0F);
		cannonCapBotLeft = new ModelRenderer(this, 73, 77);
		cannonCapBotLeft.addBox(0.5F, 0.5F, -29.5F, 3, 3, 4);
		cannonCapBotLeft.setRotationPoint(10F, 7F, 1F);
		cannonCapBotLeft.setTextureSize(128, 128);
		cannonCapBotLeft.mirror = true;
		setRotation(cannonCapBotLeft, -0.6108652F, 0F, 0F);
		cannonCapTopLeft = new ModelRenderer(this, 73, 77);
		cannonCapTopLeft.addBox(0.7F, -4.5F, -29.5F, 3, 3, 4);
		cannonCapTopLeft.setRotationPoint(10F, 7F, 1F);
		cannonCapTopLeft.setTextureSize(128, 128);
		cannonCapTopLeft.mirror = true;
		setRotation(cannonCapTopLeft, -0.6108652F, 0F, 0F);
		cannonFaceLeft = new ModelRenderer(this, 0, 43);
		cannonFaceLeft.addBox(-1F, -4F, -9.5F, 1, 8, 18);
		cannonFaceLeft.setRotationPoint(10F, 7F, 1F);
		cannonFaceLeft.setTextureSize(128, 128);
		cannonFaceLeft.mirror = true;
		setRotation(cannonFaceLeft, -0.6108652F, 0F, 0F);
		cannonLeft = new ModelRenderer(this, 69, 11);
		cannonLeft.addBox(0F, -5.5F, -10.5F, 4, 11, 20);
		cannonLeft.setRotationPoint(10F, 7F, 1F);
		cannonLeft.setTextureSize(128, 128);
		cannonLeft.mirror = true;
		setRotation(cannonLeft, -0.6108652F, 0F, 0F);
		cannonInFaceLeft = new ModelRenderer(this, 77, 43);
		cannonInFaceLeft.addBox(4F, -4F, -9.5F, 1, 8, 18);
		cannonInFaceLeft.setRotationPoint(10F, 7F, 1F);
		cannonInFaceLeft.setTextureSize(128, 128);
		cannonInFaceLeft.mirror = true;
		setRotation(cannonInFaceLeft, -0.6108652F, 0F, 0F);
		cannonBarrelNeckLeft = new ModelRenderer(this, 106, 70);
		cannonBarrelNeckLeft.addBox(0.5F, -6F, -13.3F, 3, 8, 2);
		cannonBarrelNeckLeft.setRotationPoint(10F, 9F, 1F);
		cannonBarrelNeckLeft.setTextureSize(128, 128);
		cannonBarrelNeckLeft.mirror = true;
		setRotation(cannonBarrelNeckLeft, -0.6108652F, 0F, 0F);
		cannonBarrelBotLeft = new ModelRenderer(this, 42, 52);
		cannonBarrelBotLeft.addBox(1F, 1F, -25.5F, 2, 2, 15);
		cannonBarrelBotLeft.setRotationPoint(10F, 7F, 1F);
		cannonBarrelBotLeft.setTextureSize(128, 128);
		cannonBarrelBotLeft.mirror = true;
		setRotation(cannonBarrelBotLeft, -0.6108652F, 0F, 0F);
		cannonBarrelCoupleLeft = new ModelRenderer(this, 89, 77);
		cannonBarrelCoupleLeft.addBox(1.5F, -4F, -14.5F, 1, 4, 3);
		cannonBarrelCoupleLeft.setRotationPoint(10F, 9F, 1F);
		cannonBarrelCoupleLeft.setTextureSize(128, 128);
		cannonBarrelCoupleLeft.mirror = true;
		setRotation(cannonBarrelCoupleLeft, -0.6108652F, 0F, 0F);
		cannonBarrelTopLeft = new ModelRenderer(this, 42, 52);
		cannonBarrelTopLeft.addBox(1F, -4F, -25.5F, 2, 2, 15);
		cannonBarrelTopLeft.setRotationPoint(10F, 7F, 1F);
		cannonBarrelTopLeft.setTextureSize(128, 128);
		cannonBarrelTopLeft.mirror = true;
		setRotation(cannonBarrelTopLeft, -0.6108652F, 0F, 0F);
	}

	public void render(float f5)
	{
		rot1.render(f5);
		radarNeck.render(f5);
		radarBase.render(f5);
		radarBack.render(f5);
		radarRight.render(f5);
		radarLeft.render(f5);
		baseEdge4.render(f5);
		base.render(f5);
		baseEdge3.render(f5);
		baseEdge1.render(f5);
		baseEdge2.render(f5);
		body.render(f5);
		bodyFace.render(f5);
		bodyTop.render(f5);
		bodyEdge.render(f5);
		bodyArmRight.render(f5);
		bodyBack.render(f5);
		bodyBubble.render(f5);
		bodyArmLeft.render(f5);
		cannonRight.render(f5);
		cannonFaceRight.render(f5);
		cannonBarrelTopRight.render(f5);
		cannonBarrelBotRight.render(f5);
		cannonCapTopRight.render(f5);
		cannonCapBotRight.render(f5);
		cannonInFaceRight.render(f5);
		cannonBarrelCouple.render(f5);
		cannonBarrelNeck.render(f5);
		cannonCapBotLeft.render(f5);
		cannonCapTopLeft.render(f5);
		cannonFaceLeft.render(f5);
		cannonLeft.render(f5);
		cannonInFaceLeft.render(f5);
		cannonBarrelNeckLeft.render(f5);
		cannonBarrelBotLeft.render(f5);
		cannonBarrelCoupleLeft.render(f5);
		cannonBarrelTopLeft.render(f5);
	}

	public void renderBody(float f5)
	{
		rot1.render(f5);
		baseEdge4.render(f5);
		base.render(f5);
		baseEdge3.render(f5);
		baseEdge1.render(f5);
		baseEdge2.render(f5);
		body.render(f5);
		bodyFace.render(f5);
		bodyTop.render(f5);
		bodyEdge.render(f5);
		bodyArmRight.render(f5);
		bodyBack.render(f5);
		bodyBubble.render(f5);
		bodyArmLeft.render(f5);
		radarBase.render(f5);

	}

	public void renderCannon(float f5, float rot)
	{
		ModelRenderer[] cannon = { cannonRight, cannonFaceRight, cannonBarrelTopRight, cannonBarrelBotRight, cannonCapTopRight, cannonCapBotRight, cannonInFaceRight, cannonBarrelCouple, cannonBarrelNeck, cannonCapBotLeft, cannonCapTopLeft, cannonFaceLeft, cannonLeft, cannonInFaceLeft, cannonBarrelNeckLeft, cannonBarrelBotLeft, cannonBarrelCoupleLeft, cannonBarrelTopLeft };

		for (int i = 0; i < cannon.length; i++)
		{
			cannon[i].rotateAngleX = rot;
		}
		for (int i = 0; i < cannon.length; i++)
		{
			cannon[i].render(f5);
		}
	}

	public void renderRadar(float f5)
	{
		radarNeck.render(f5);
		radarBack.render(f5);
		radarRight.render(f5);
		radarLeft.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
