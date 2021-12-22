package com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.animation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Quaternion;

import com.hiveworkshop.wc3.gui.animedit.NodeAnimationModelEditor;
import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.util.GenericRotateAction;
import com.hiveworkshop.wc3.mdl.IdObject;
import com.hiveworkshop.wc3.mdl.Vertex;

public class RotationKeyframeAction implements GenericRotateAction {
	private final UndoAction addingTimelinesOrKeyframesAction;
	private final int trackTime;
	private final HashMap<IdObject, Quaternion> nodeToLocalRotation;
	private final NodeAnimationModelEditor modelEditor;
	private final Vertex center;
	private final byte dim1;
	private final byte dim2;
	private final Integer trackGlobalSeq;

	public RotationKeyframeAction(final UndoAction addingTimelinesOrKeyframesAction, final int trackTime,
			final Integer trackGlobalSeq, final Collection<IdObject> nodeSelection,
			final NodeAnimationModelEditor modelEditor, final double centerX, final double centerY,
			final double centerZ, final byte dim1, final byte dim2) {
		this.addingTimelinesOrKeyframesAction = addingTimelinesOrKeyframesAction;
		this.trackTime = trackTime;
		this.trackGlobalSeq = trackGlobalSeq;
		this.modelEditor = modelEditor;
		this.dim1 = dim1;
		this.dim2 = dim2;
		nodeToLocalRotation = new HashMap<>();
		for (final IdObject node : nodeSelection) {
			nodeToLocalRotation.put(node, new Quaternion());
		}
		center = new Vertex(centerX, centerY, centerZ);
	}

	@Override
	public void undo() {
		for (final Map.Entry<IdObject, Quaternion> nodeAndLocalTranslation : nodeToLocalRotation.entrySet()) {
			final IdObject node = nodeAndLocalTranslation.getKey();
			final Quaternion localTranslation = nodeAndLocalTranslation.getValue();
			node.updateLocalRotationKeyframeInverse(trackTime, trackGlobalSeq, localTranslation);
		}
		addingTimelinesOrKeyframesAction.undo();
	}

	@Override
	public void redo() {
		addingTimelinesOrKeyframesAction.redo();
		for (final Map.Entry<IdObject, Quaternion> nodeAndLocalTranslation : nodeToLocalRotation.entrySet()) {
			final IdObject node = nodeAndLocalTranslation.getKey();
			final Quaternion localTranslation = nodeAndLocalTranslation.getValue();
			node.updateLocalRotationKeyframe(trackTime, trackGlobalSeq, localTranslation);
		}
	}

	@Override
	public String actionName() {
		return "edit rotation";
	}

	@Override
	public GenericRotateAction updateRotation(final double radians) {
		modelEditor.rawRotate2d(center.x, center.y, center.z, radians, dim1, dim2, nodeToLocalRotation);
		return this;
	}

}
