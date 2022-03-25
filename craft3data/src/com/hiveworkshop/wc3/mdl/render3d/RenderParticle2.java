package com.hiveworkshop.wc3.mdl.render3d;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.hiveworkshop.wc3.gui.modelviewer.AnimatedRenderEnvironment;
import com.hiveworkshop.wc3.mdl.ParticleEmitter2;
import com.hiveworkshop.wc3.mdl.Vertex;
import com.hiveworkshop.wc3.util.MathUtils;

public class RenderParticle2 extends EmittedObject<RenderParticleEmitter2View> {
	private static final Vector4f vector4Heap = new Vector4f();
	private static final Vector4f colorHeap = new Vector4f();
	private static final Vector4f color1Heap = new Vector4f();
	private static final Vector4f color2Heap = new Vector4f();
	private static Quaternion rotationZHeap = new Quaternion();
	private static Quaternion rotationYHeap = new Quaternion();
	private static Quaternion rotationXHeap = new Quaternion();
	private static Matrix4f matrixHeap = new Matrix4f();
	private static Vector3f locationHeap = new Vector3f();
	private static Vector4f location4Heap = new Vector4f();
	private static Vector4f startHeap = new Vector4f();
	private static Vector4f endHeap = new Vector4f();
	private static Vector3f tailHeap = new Vector3f();
	private static Vector3f normalHeap = new Vector3f();
	private final RenderParticleEmitter2 emitter;
	private boolean head;
	private final Vector3f location;
	private final Vector3f velocity;
	private float gravity;
	private final Vector3f nodeScale;

	private RenderNode node;

	public RenderParticle2(final RenderParticleEmitter2 emitter) {
		this.emitter = emitter;
		this.emitterView = null;
		this.health = 0;
		this.head = true;
		this.location = new Vector3f();
		this.velocity = new Vector3f();
		this.gravity = 0;
		this.nodeScale = new Vector3f();

		vertices = new float[12];
		this.lta = 0;
		this.lba = 0;
		this.rta = 0;
		this.rba = 0;
		this.rgb = 0;
	}

	@Override
	public void reset(final RenderParticleEmitter2View emitterView, final boolean isHead) {
		double width = emitterView.getLength();
		double length = emitterView.getWidth();
		double latitude = emitterView.getLatitude();
		final double variation = emitterView.getVariation();
		final double speed = emitterView.getSpeed();
		final double gravity = emitterView.getGravity();

		final ParticleEmitter2 modelObject = emitter.modelObject;
		final RenderNode node = emitterView.instance.getRenderNode(modelObject);
		final Vertex pivotPoint = modelObject.getPivotPoint();
		final Vector3f scale = node.getWorldScale();
		width *= 0.5;
		length *= 0.5;
		latitude = Math.toRadians(latitude);

		this.emitterView = emitterView;
		this.node = node;
		this.health = (float) modelObject.getLifeSpan();
		this.head = isHead;
		this.gravity = (float) (gravity * scale.z);

		this.nodeScale.set(scale);

		// Local location
		location.x = (float) (pivotPoint.x + MathUtils.randomInRange(-width, width));
		location.y = (float) (pivotPoint.y + MathUtils.randomInRange(-length, length));
		location.z = (float) (pivotPoint.z);

		// World location
		if (!modelObject.isModelSpace()) {
			vector4Heap.set(location.x, location.y, location.z, 1);
			Matrix4f.transform(node.getWorldMatrix(), vector4Heap, vector4Heap);
			location.set(vector4Heap);
		}

		// Location rotation
		rotationZHeap.setIdentity();
		vector4Heap.set(0, 0, 1, (float) (Math.PI / 2));
		rotationZHeap.setFromAxisAngle(vector4Heap);
		vector4Heap.set(1, 0, 0, MathUtils.randomInRange(-latitude, latitude));
		rotationYHeap.setFromAxisAngle(vector4Heap);
		Quaternion.mul(rotationYHeap, rotationZHeap, rotationYHeap);

		// If this is not a line emitter, emit in a sphere rather than a circle
		if (!modelObject.isLineEmitter()) {
			vector4Heap.set(0, 1, 0, MathUtils.randomInRange(-latitude, latitude));
			rotationXHeap.setFromAxisAngle(vector4Heap);
			Quaternion.mul(rotationXHeap, rotationYHeap, rotationYHeap);
		}

		// World rotation
		if (!modelObject.isModelSpace()) {
			Quaternion.mul(node.getWorldRotation(), rotationYHeap, rotationYHeap);
		}

		// Apply the rotation
		MathUtils.fromQuat(rotationYHeap, matrixHeap);
		vector4Heap.set(0, 0, 1, 1);
		Matrix4f.transform(matrixHeap, vector4Heap, vector4Heap);
		velocity.set(vector4Heap);

		// Apply speed
		velocity.scale((float) speed + MathUtils.randomInRange(-variation, variation));

		// Apply the parent's scale
		velocity.x *= scale.x;
		velocity.y *= scale.y;
		velocity.z *= scale.z;
	}

	@Override
	public void update() {
		final ParticleEmitter2 modelObject = emitter.modelObject;
		final float dt = AnimatedRenderEnvironment.FRAMES_PER_UPDATE * 0.001f;
		final Vector3f worldLocation = locationHeap;
		final Vector4f worldLocation4f = location4Heap;

		this.health -= dt;

		velocity.z -= this.gravity * dt;

		location.x = location.x + (velocity.x * dt);
		location.y = location.y + (velocity.y * dt);
		location.z = location.z + (velocity.z * dt);

		worldLocation.set(location);
		worldLocation4f.set(location.x, location.y, location.z, 1);

		final float lifeFactor = (float) ((modelObject.getLifeSpan() - this.health) / modelObject.getLifeSpan());
		final float timeMiddle = (float) modelObject.getTime();
		float factor;
		int firstColor;
		Vertex interval;

		if (lifeFactor < timeMiddle) {
			factor = lifeFactor / timeMiddle;

			firstColor = 0;

			if (head) {
				interval = modelObject.getLifeSpanUVAnim();
			}
			else {
				interval = modelObject.getTailUVAnim();
			}
		}
		else {
			factor = (lifeFactor - timeMiddle) / (1 - timeMiddle);

			firstColor = 1;

			if (head) {
				interval = modelObject.getDecayUVAnim();
			}
			else {
				interval = modelObject.getTailDecayUVAnim();
			}
		}

		factor = Math.min(factor, 1);

		final float start = (float) interval.x;
		final float end = (float) interval.y;
		final float repeat = (float) interval.z;
		final Vertex scaling = modelObject.getParticleScaling();
		final Vertex[] colors = modelObject.getSegmentColors();
		final float scale = (float) MathUtils.lerp((float) scaling.getCoord((byte) firstColor),
				(float) scaling.getCoord((byte) (firstColor + 1)), factor);
		float left, top, right, bottom;
		final RenderModel instance = this.emitterView.instance;

		// If this is a team colored emitter, get the team color tile from the atlas
		// Otherwise do normal texture atlas handling.
		if (modelObject.isTeamColored()) {
			// except that Matrix Eater has no such atlas and we are simply copying from
			// Ghostwolf
			left = 0;
			top = 0;
			right = left + 1;
			bottom = top + 1;
		}
		else {
			final int columns = modelObject.getCols();
			float index = 0;
			final float spriteCount = end - start;
			if ((spriteCount > 0) && ((columns > 1) || (modelObject.getRows() > 1))) {
				// Repeating speeds up the sprite animation, which makes it effectively run N
				// times in its interval.
				// E.g. if repeat is 4, the sprite animation will be seen 4 times, and thus also
				// run 4 times as fast
				index = (float) (start + (Math.floor(spriteCount * repeat * factor) % spriteCount));
			}

			left = index % columns;
			top = (int) (index / columns);
			right = left + 1;
			bottom = top + 1;
		}

		final Vertex firstColorVertexME = colors[firstColor];
		final Vertex secondColorVertexME = colors[firstColor + 1];
		color1Heap.set((float) firstColorVertexME.x, (float) firstColorVertexME.y, (float) firstColorVertexME.z,
				(float) modelObject.getAlpha().getCoord((byte) firstColor));
		color2Heap.set((float) secondColorVertexME.x, (float) secondColorVertexME.y, (float) secondColorVertexME.z,
				(float) modelObject.getAlpha().getCoord((byte) (firstColor + 1)));
		MathUtils.lerp(colorHeap, color1Heap, color2Heap, factor);

		final int a = ((int) colorHeap.w) & 0xFF;

		this.lta = MathUtils.uint8ToUint24((byte) right, (byte) bottom, (byte) a);
		this.lba = MathUtils.uint8ToUint24((byte) left, (byte) bottom, (byte) a);
		this.rta = MathUtils.uint8ToUint24((byte) right, (byte) top, (byte) a);
		this.rba = MathUtils.uint8ToUint24((byte) left, (byte) top, (byte) a);
		this.rgb = MathUtils.uint8ToUint24((byte) ((int) (colorHeap.z * 255) & 0xFF),
				(byte) ((int) (colorHeap.y * 255) & 0xFF), (byte) ((int) (colorHeap.x * 255) & 0xFF));

		final Vector3f[] vectors;

		// Choose between a default rectangle or a billboarded one
		if (modelObject.isXYQuad()) {
			vectors = instance.getSpacialVectors();
		}
		else {
			vectors = instance.getBillboardVectors();
		}

		final float[] vertices = this.vertices;
		final Vector3f nodeScale = this.nodeScale;

		final float scalex = scale * nodeScale.x;
		final float scaley = scale * nodeScale.y;
		final float scalez = scale * nodeScale.z;

		if (head) {
			// If this is a model space emitter, the particle location is in local space, so
			// convert it now to world space.
			if (modelObject.isModelSpace()) {
				Matrix4f.transform(this.node.getWorldMatrix(), worldLocation4f, worldLocation4f);
			}

			final float px = worldLocation4f.x;
			final float py = worldLocation4f.y;
			final float pz = worldLocation4f.z;

			final Vector3f pv1 = vectors[0];
			final Vector3f pv2 = vectors[1];
			final Vector3f pv3 = vectors[2];
			final Vector3f pv4 = vectors[3];

			vertices[0] = px + (pv1.x * scalex);
			vertices[1] = py + (pv1.y * scaley);
			vertices[2] = pz + (pv1.z * scalez);
			vertices[3] = px + (pv2.x * scalex);
			vertices[4] = py + (pv2.y * scaley);
			vertices[5] = pz + (pv2.z * scalez);
			vertices[6] = px + (pv3.x * scalex);
			vertices[7] = py + (pv3.y * scaley);
			vertices[8] = pz + (pv3.z * scalez);
			vertices[9] = px + (pv4.x * scalex);
			vertices[10] = py + (pv4.y * scaley);
			vertices[11] = pz + (pv4.z * scalez);
		}
		else {
			final double tailLength = modelObject.getTailLength();
			final double offsetx = tailLength * velocity.x * 1;
			final double offsety = tailLength * velocity.y * 1;
			final double offsetz = tailLength * velocity.z * 1;

			// The start and end of the tail
			startHeap.set((float) (worldLocation4f.x - offsetx), (float) (worldLocation4f.y - offsety),
					(float) (worldLocation4f.z - offsetz), 1);
			endHeap.set((worldLocation4f.x), (worldLocation4f.y), (worldLocation4f.z), 1);

			// If this is a model space emitter, the start and end are in local space, so
			// convert them to world space.
			if (modelObject.isModelSpace()) {
				Matrix4f.transform(this.node.getWorldMatrix(), startHeap, startHeap);
				Matrix4f.transform(this.node.getWorldMatrix(), endHeap, endHeap);
			}

			final float startx = startHeap.x;
			final float starty = startHeap.y;
			final float startz = startHeap.z;
			final float endx = endHeap.x;
			final float endy = endHeap.y;
			final float endz = endHeap.z;

			// Get the normal to the tail in camera space
			// This allows to build a 2D rectangle around the 3D tail
			tailHeap.set(endx - startx, endy - starty, endz - startz);
			if (tailHeap.lengthSquared() > 0) {
				tailHeap.normalise();
			}
			normalHeap.set(instance.getBillboardVectors()[6]);
			Vector3f.cross(normalHeap, tailHeap, normalHeap);
			if (normalHeap.lengthSquared() > 0) {
				normalHeap.normalise();
			}

			final float normalX = normalHeap.x * scalex;
			final float normalY = normalHeap.y * scaley;
			final float normalZ = normalHeap.z * scalez;

			vertices[0] = startx - normalX;
			vertices[1] = starty - normalY;
			vertices[2] = startz - normalZ;

			vertices[6] = endx + normalX;
			vertices[7] = endy + normalY;
			vertices[8] = endz + normalZ;

			vertices[3] = endx - normalX;
			vertices[4] = endy - normalY;
			vertices[5] = endz - normalZ;

			vertices[9] = startx + normalX;
			vertices[10] = starty + normalY;
			vertices[11] = startz + normalZ;
		}
	}
}
