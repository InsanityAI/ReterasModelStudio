package com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.util;

import com.hiveworkshop.wc3.gui.modeledit.UndoAction;

public interface GenericRotateAction extends UndoAction {
	GenericRotateAction updateRotation(double radians);
}
