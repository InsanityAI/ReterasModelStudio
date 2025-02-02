package com.hiveworkshop.wc3.gui.modeledit.newstuff.uv;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.gui.modeledit.UVPanel;
import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.util.GenericMoveAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.util.GenericRotateAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.util.GenericScaleAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.listener.ComponentVisibilityListener;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionView;
import com.hiveworkshop.wc3.mdl.TVertex;
import com.hiveworkshop.wc3.mdl.Vertex;

/**
 * So, in some ideal future this would be an implementation of the ModelEditor
 * interface, I believe, and the editor would be operating on an interface who
 * could capture clicks and convert them into 2D operations regardless of
 * whether the underlying thing being editor was UV or Mesh.
 *
 * It isn't like that right now, though, so this is just going to be a 2D copy
 * pasta.
 */
public interface TVertexEditor extends ComponentVisibilityListener {
	// should move to a Util at a later date, if it does not require internal
	// knowledge of center point from state holders
	UndoAction translate(double x, double y);

	UndoAction setPosition(TVertex center, double x, double y);

	UndoAction rotate(TVertex center, double rotateRadians);

	UndoAction mirror(byte dim, double centerX, double centerY);

	UndoAction snapSelectedVertices();

	UndoAction snapXSelectedVertices();

	UndoAction snapYSelectedVertices();

	UndoAction setSelectedRegion(Rectangle2D region, CoordinateSystem coordinateSystem);

	UndoAction removeSelectedRegion(Rectangle2D region, CoordinateSystem coordinateSystem);

	UndoAction addSelectedRegion(Rectangle2D region, CoordinateSystem coordinateSystem);

	UndoAction expandSelection();

	UndoAction invertSelection();

	UndoAction selectAll();

	UndoAction selectFromViewer(SelectionView viewerSelectionView);

	void selectByVertices(Collection<? extends Vertex> newSelection);

	boolean canSelectAt(Point point, CoordinateSystem axes);

	GenericMoveAction beginTranslation();

	GenericScaleAction beginScaling(double centerX, double centerY);

	GenericRotateAction beginRotation(double centerX, double centerY, byte dim1, byte dim2);

	void rawTranslate(double x, double y);

	void rawScale(double centerX, double centerY, double scaleX, double scaleY);

	void rawRotate2d(double centerX, double centerY, double radians, byte firstXYZ, byte secondXYZ);

	TVertex getSelectionCenter();

	void setUVLayerIndex(int uvLayerIndex);

	int getUVLayerIndex();

	UndoAction remap(byte xDim, byte yDim, UVPanel.UnwrapDirection unwrapDirection);

}
