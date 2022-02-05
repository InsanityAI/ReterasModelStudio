package com.hiveworkshop.wc3.gui.modeledit.selection;

import javax.swing.ImageIcon;

import com.hiveworkshop.wc3.gui.icons.RMSIcons;
import com.hiveworkshop.wc3.gui.modeledit.toolbar.ToolbarButtonType;

public enum SelectionItemTypes implements ToolbarButtonType {
	VERTEX("Select Vertices", RMSIcons.loadToolBarImageIcon("vertex.png")),
	FACE("Select Faces", RMSIcons.loadToolBarImageIcon("poly.png")),
	GROUP("Select Groups", RMSIcons.loadToolBarImageIcon("bundle.png")),
	ANIMATE("Select Nodes and Animate", RMSIcons.loadToolBarImageIcon("animate.png")),
	CLUSTER("Select Cluster", RMSIcons.loadToolBarImageIcon("bundle.png")),
	TPOSE("Select and T-Pose", RMSIcons.loadToolBarImageIcon("T.png"));

	private final String name;
	private final ImageIcon imageIcon;

	private SelectionItemTypes(final String name, final ImageIcon imageIcon) {
		this.name = name;
		this.imageIcon = imageIcon;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ImageIcon getImageIcon() {
		return imageIcon;
	}

}
