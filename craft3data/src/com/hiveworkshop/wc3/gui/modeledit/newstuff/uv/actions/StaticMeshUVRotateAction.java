package com.hiveworkshop.wc3.gui.modeledit.newstuff.uv.actions;

import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.util.GenericRotateAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.uv.TVertexEditor;
import com.hiveworkshop.wc3.mdl.TVertex;

public final class StaticMeshUVRotateAction implements GenericRotateAction {
	private final TVertexEditor modelEditor;
	private final TVertex center;
	private double radians;
	private final byte dim1;
	private final byte dim2;

	public StaticMeshUVRotateAction(final TVertexEditor modelEditor, final TVertex center, final byte dim1,
			final byte dim2) {
		this.modelEditor = modelEditor;
		this.center = center;
		this.dim1 = dim1;
		this.dim2 = dim2;
		this.radians = 0;
	}

	@Override
	public void undo() {
		modelEditor.rawRotate2d(center.x, center.y, -radians, dim1, dim2);
	}

	@Override
	public void redo() {
		modelEditor.rawRotate2d(center.x, center.y, radians, dim1, dim2);
	}

	@Override
	public String actionName() {
		return "rotate";
	}

	@Override
	public GenericRotateAction updateRotation(final double radians) {
		this.radians += radians;
		modelEditor.rawRotate2d(center.x, center.y, radians, dim1, dim2);
		return this;
	}

}
