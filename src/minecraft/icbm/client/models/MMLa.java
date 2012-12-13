package icbm.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class MMLa extends ModelBase
{
	// fields
	ModelRenderer a;
	ModelRenderer b;
	ModelRenderer c;
	ModelRenderer d;
	ModelRenderer e;
	ModelRenderer f;
	ModelRenderer g;
	ModelRenderer h;
	ModelRenderer i;
	ModelRenderer j;
	ModelRenderer k;
	ModelRenderer l;
	ModelRenderer m;
	ModelRenderer n;
	ModelRenderer o;
	ModelRenderer p;

	public MMLa()
	{
		textureWidth = 128;
		textureHeight = 128;

		a = new ModelRenderer(this, 0, 0);
		a.addBox(0F, 0F, 0F, 6, 50, 6);
		a.setRotationPoint(-3F, -26F, -3F);
		a.setTextureSize(128, 128);
		a.mirror = true;
		setRotation(a, 0F, 0F, 0F);
		b = new ModelRenderer(this, 0, 57);
		b.addBox(-5F, 0F, -5F, 10, 16, 10);
		b.setRotationPoint(0F, 8F, 0F);
		b.setTextureSize(128, 128);
		b.mirror = true;
		setRotation(b, 0F, 0.7853982F, 0F);
		c = new ModelRenderer(this, 59, 26);
		c.addBox(-1F, 0F, -9F, 2, 12, 18);
		c.setRotationPoint(0F, 12F, 0F);
		c.setTextureSize(128, 128);
		c.mirror = true;
		setRotation(c, 0F, 0.7853982F, 0F);
		d = new ModelRenderer(this, 59, 26);
		d.addBox(-1F, 0F, -9F, 2, 12, 18);
		d.setRotationPoint(0F, 12F, 0F);
		d.setTextureSize(128, 128);
		d.mirror = true;
		setRotation(d, 0F, -0.7853982F, 0F);
		e = new ModelRenderer(this, 59, 0);
		e.addBox(-1F, 0F, 0F, 2, 10, 10);
		e.setRotationPoint(0F, -24F, 0F);
		e.setTextureSize(128, 128);
		e.mirror = true;
		setRotation(e, -0.7853982F, 0.7853982F, 0F);
		f = new ModelRenderer(this, 59, 0);
		f.addBox(-1F, 0F, 0F, 2, 10, 10);
		f.setRotationPoint(0F, -24F, 0F);
		f.setTextureSize(128, 128);
		f.mirror = true;
		setRotation(f, -0.7853982F, -0.7853982F, 0F);
		g = new ModelRenderer(this, 25, 0);
		g.addBox(-1F, 0F, -7F, 2, 10, 14);
		g.setRotationPoint(0F, -17F, 0F);
		g.setTextureSize(128, 128);
		g.mirror = true;
		setRotation(g, 0F, 0.7853982F, 0F);
		h = new ModelRenderer(this, 25, 0);
		h.addBox(-1F, 0F, -7F, 2, 10, 14);
		h.setRotationPoint(0F, -17F, 0F);
		h.setTextureSize(128, 128);
		h.mirror = true;
		setRotation(h, 0F, -0.7853982F, 0F);
		i = new ModelRenderer(this, 25, 26);
		i.addBox(-1F, 0F, 0F, 2, 13, 13);
		i.setRotationPoint(0F, 3F, 0F);
		i.setTextureSize(128, 128);
		i.mirror = true;
		setRotation(i, -0.7853982F, -0.7853982F, 0F);
		j = new ModelRenderer(this, 25, 26);
		j.addBox(-1F, 0F, 0F, 2, 13, 13);
		j.setRotationPoint(0F, 3F, 0F);
		j.setTextureSize(128, 128);
		j.mirror = true;
		setRotation(j, -0.7853982F, 0.7853982F, 0F);
		k = new ModelRenderer(this, 0, 86);
		k.addBox(2F, 0F, 2F, 2, 8, 2);
		k.setRotationPoint(0F, -29F, 0F);
		k.setTextureSize(128, 128);
		k.mirror = true;
		setRotation(k, 0F, 0F, 0F);
		l = new ModelRenderer(this, 0, 86);
		l.addBox(2F, 0F, 2F, 2, 8, 2);
		l.setRotationPoint(0F, -29F, 0F);
		l.setTextureSize(128, 128);
		l.mirror = true;
		setRotation(l, 0F, 1.570796F, 0F);
		m = new ModelRenderer(this, 0, 86);
		m.addBox(2F, 0F, 2F, 2, 8, 2);
		m.setRotationPoint(0F, -29F, 0F);
		m.setTextureSize(128, 128);
		m.mirror = true;
		setRotation(m, 0F, 3.141593F, 0F);
		n = new ModelRenderer(this, 0, 86);
		n.addBox(2F, 0F, 2F, 2, 8, 2);
		n.setRotationPoint(0F, -29F, 0F);
		n.setTextureSize(128, 128);
		n.mirror = true;
		setRotation(n, 0F, -1.570796F, 0F);
		o = new ModelRenderer(this, 10, 86);
		o.addBox(-2F, 0F, -2F, 4, 6, 4);
		o.setRotationPoint(0F, -32F, 0F);
		o.setTextureSize(128, 128);
		o.mirror = true;
		setRotation(o, 0F, 0F, 0F);
		p = new ModelRenderer(this, 28, 86);
		p.addBox(-1F, 0F, -1F, 2, 6, 2);
		p.setRotationPoint(0F, -38F, 0F);
		p.setTextureSize(128, 128);
		p.mirror = true;
		setRotation(p, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float f3, float f4, float f5)
	{
		super.render(entity, x, y, z, f3, f4, f5);
		this.setRotationAngles(x, y, z, f3, f4, f5, entity);
		a.render(f5);
		b.render(f5);
		c.render(f5);
		d.render(f5);
		e.render(f5);
		f.render(f5);
		g.render(f5);
		h.render(f5);
		i.render(f5);
		j.render(f5);
		k.render(f5);
		l.render(f5);
		m.render(f5);
		n.render(f5);
		o.render(f5);
		p.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
