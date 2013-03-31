package icbm.zhapin.muoxing.daodan;

import icbm.core.muoxing.ICBMModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MMZhen extends ICBMModelBase
{
	// fields
	ModelRenderer A;
	ModelRenderer B;
	ModelRenderer C;
	ModelRenderer D;
	ModelRenderer E;
	ModelRenderer F;
	ModelRenderer G;
	ModelRenderer H;
	ModelRenderer I;
	ModelRenderer J;
	ModelRenderer K;
	ModelRenderer L;
	ModelRenderer M;
	ModelRenderer N;
	ModelRenderer O;
	ModelRenderer P;
	ModelRenderer Q;
	ModelRenderer R;
	ModelRenderer S;

	public MMZhen()
	{
		textureWidth = 128;
		textureHeight = 128;

		A = new ModelRenderer(this, 0, 0);
		A.addBox(0F, 0F, 0F, 6, 50, 6);
		A.setRotationPoint(-3F, -26F, -3F);
		A.setTextureSize(128, 128);
		A.mirror = true;
		setRotation(A, 0F, 0F, 0F);
		B = new ModelRenderer(this, 0, 57);
		B.addBox(-5F, 0F, -5F, 10, 16, 10);
		B.setRotationPoint(0F, 8F, 0F);
		B.setTextureSize(128, 128);
		B.mirror = true;
		setRotation(B, 0F, 0.7853982F, 0F);
		C = new ModelRenderer(this, 59, 26);
		C.addBox(-1F, 0F, -9F, 2, 12, 18);
		C.setRotationPoint(0F, 12F, 0F);
		C.setTextureSize(128, 128);
		C.mirror = true;
		setRotation(C, 0F, 0.7853982F, 0F);
		D = new ModelRenderer(this, 59, 26);
		D.addBox(-1F, 0F, -9F, 2, 12, 18);
		D.setRotationPoint(0F, 12F, 0F);
		D.setTextureSize(128, 128);
		D.mirror = true;
		setRotation(D, 0F, -0.7853982F, 0F);
		E = new ModelRenderer(this, 59, 0);
		E.addBox(-1F, 0F, 0F, 2, 10, 10);
		E.setRotationPoint(0F, -24F, 0F);
		E.setTextureSize(128, 128);
		E.mirror = true;
		setRotation(E, -0.7853982F, 0.7853982F, 0F);
		F = new ModelRenderer(this, 59, 0);
		F.addBox(-1F, 0F, 0F, 2, 10, 10);
		F.setRotationPoint(0F, -24F, 0F);
		F.setTextureSize(128, 128);
		F.mirror = true;
		setRotation(F, -0.7853982F, -0.7853982F, 0F);
		G = new ModelRenderer(this, 25, 0);
		G.addBox(-1F, 0F, -7F, 2, 10, 14);
		G.setRotationPoint(0F, -17F, 0F);
		G.setTextureSize(128, 128);
		G.mirror = true;
		setRotation(G, 0F, 0.7853982F, 0F);
		H = new ModelRenderer(this, 25, 0);
		H.addBox(-1F, 0F, -7F, 2, 10, 14);
		H.setRotationPoint(0F, -17F, 0F);
		H.setTextureSize(128, 128);
		H.mirror = true;
		setRotation(H, 0F, -0.7853982F, 0F);
		I = new ModelRenderer(this, 25, 26);
		I.addBox(-1F, 0F, 0F, 2, 13, 13);
		I.setRotationPoint(0F, 3F, 0F);
		I.setTextureSize(128, 128);
		I.mirror = true;
		setRotation(I, -0.7853982F, -0.7853982F, 0F);
		J = new ModelRenderer(this, 25, 26);
		J.addBox(-1F, 0F, 0F, 2, 13, 13);
		J.setRotationPoint(0F, 3F, 0F);
		J.setTextureSize(128, 128);
		J.mirror = true;
		setRotation(J, -0.7853982F, 0.7853982F, 0F);
		K = new ModelRenderer(this, 44, 62);
		K.addBox(0F, 0F, 0F, 8, 2, 8);
		K.setRotationPoint(-4F, -28F, -4F);
		K.setTextureSize(128, 128);
		K.mirror = true;
		setRotation(K, 0F, 0F, 0F);
		L = new ModelRenderer(this, 72, 62);
		L.addBox(0F, 0F, 0F, 8, 1, 2);
		L.setRotationPoint(-3F, -28F, -1F);
		L.setTextureSize(128, 128);
		L.mirror = true;
		setRotation(L, 0F, 0F, -0.9773844F);
		M = new ModelRenderer(this, 72, 62);
		M.addBox(0F, 0F, 0F, 8, 1, 2);
		M.setRotationPoint(3F, -28F, 1F);
		M.setTextureSize(128, 128);
		M.mirror = true;
		setRotation(M, 0F, 3.141593F, -0.9773844F);
		N = new ModelRenderer(this, 44, 74);
		N.addBox(0F, 0F, 0F, 6, 2, 6);
		N.setRotationPoint(-3F, -35F, -3F);
		N.setTextureSize(128, 128);
		N.mirror = true;
		setRotation(N, 0F, 0F, 0F);
		O = new ModelRenderer(this, 44, 85);
		O.addBox(0F, 0F, 0F, 6, 6, 8);
		O.setRotationPoint(-4F, -41F, -4F);
		O.setTextureSize(128, 128);
		O.mirror = true;
		setRotation(O, 0F, 0F, 0F);
		P = new ModelRenderer(this, 75, 92);
		P.addBox(0F, 0F, 0F, 7, 2, 8);
		P.setRotationPoint(-4F, -41F, -4F);
		P.setTextureSize(128, 128);
		P.mirror = true;
		setRotation(P, 0F, 0F, -0.1745329F);
		Q = new ModelRenderer(this, 44, 101);
		Q.addBox(0F, 0F, 0F, 5, 2, 8);
		Q.setRotationPoint(3F, -42F, -4F);
		Q.setTextureSize(128, 128);
		Q.mirror = true;
		setRotation(Q, 0F, 0F, 1.186824F);
		R = new ModelRenderer(this, 73, 85);
		R.addBox(0F, 0F, 0F, 6, 2, 2);
		R.setRotationPoint(-3F, -39F, -6F);
		R.setTextureSize(128, 128);
		R.mirror = true;
		setRotation(R, 0F, 0F, 0.2094395F);
		S = new ModelRenderer(this, 92, 83);
		S.addBox(4F, 0F, 0F, 2, 2, 4);
		S.setRotationPoint(-3F, -39F, -6F);
		S.setTextureSize(128, 128);
		S.mirror = true;
		setRotation(S, 0F, 0F, 0.2094395F);
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
		A.render(f5);
		B.render(f5);
		C.render(f5);
		D.render(f5);
		E.render(f5);
		F.render(f5);
		G.render(f5);
		H.render(f5);
		I.render(f5);
		J.render(f5);
		K.render(f5);
		L.render(f5);
		M.render(f5);
		N.render(f5);
		O.render(f5);
		P.render(f5);
		Q.render(f5);
		R.render(f5);
		S.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
