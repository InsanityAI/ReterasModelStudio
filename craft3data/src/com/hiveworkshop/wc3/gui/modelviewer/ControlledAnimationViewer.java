package com.hiveworkshop.wc3.gui.modelviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.lwjgl.LWJGLException;

import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.mdl.Animation;
import com.hiveworkshop.wc3.mdl.Camera;
import com.hiveworkshop.wc3.mdl.v2.ModelView;

public class ControlledAnimationViewer extends JPanel
		implements AnimationControllerListener, CameraManagerPanelListener {
	private ModelView mdlDisp;
	private AnimatedPerspectiveViewport perspectiveViewport;

	public ControlledAnimationViewer(final ModelView mdlDisp, final ProgramPreferences programPreferences,
			final boolean doDefaultCamera) {
		this.mdlDisp = mdlDisp;
		try {
			perspectiveViewport = new AnimatedPerspectiveViewport(mdlDisp, programPreferences, doDefaultCamera);
			perspectiveViewport.setMinimumSize(new Dimension(200, 200));
			perspectiveViewport.setAnimationTime(0);
			perspectiveViewport.setLive(true);
		} catch (final LWJGLException e) {
			throw new RuntimeException(e);
		}
		setLayout(new BorderLayout());
		add(perspectiveViewport, BorderLayout.CENTER);
	}

	public void setModel(final ModelView modelView) {
		this.mdlDisp = modelView;
		perspectiveViewport.setModel(modelView);
		reload();
	}

	public void setTitle(final String title) {
		setBorder(BorderFactory.createTitledBorder(title));
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// perspectiveViewport.repaint();
		perspectiveViewport.paint(perspectiveViewport.getGraphics());
	}

	public void reloadAllTextures() {
		perspectiveViewport.reloadAllTextures();
	}

	public void reload() {
		perspectiveViewport.reloadTextures();
	}

	@Override
	public void setAnimation(final Animation animation) {
		perspectiveViewport.setAnimation(animation);
	}

	@Override
	public void playAnimation() {
		perspectiveViewport.setAnimationTime(0);
	}

	@Override
	public void setLoop(final LoopType loopType) {
		perspectiveViewport.setLoopType(loopType);
	}

	@Override
	public void setSpeed(final float speed) {
		perspectiveViewport.setAnimationSpeed(speed);
	}

	public Animation getCurrentAnimation() {
		return perspectiveViewport.getCurrentAnimation();
	}

	public void setSpawnParticles(final boolean b) {
		perspectiveViewport.setSpawnParticles(b);
	}

	@Override
	public void setLevelOfDetail(final int levelOfDetail) {
		perspectiveViewport.setLevelOfDetail(levelOfDetail);
	}

	public BufferedImage getBufferedImage() {
		return perspectiveViewport.getBufferedImage();
	}

	@Override
	public void setCamera(final Camera camera) {
		perspectiveViewport.setCamera(camera);
	}

	@Override
	public void setDefaultCamera() {
		perspectiveViewport.loadDefaultCamera();
	}

	public AnimatedPerspectiveViewport getPerspectiveViewport() {
		return perspectiveViewport;
	}

	@Override
	public Camera createCameraFromCurrentView() {
		return perspectiveViewport.createCameraFromCurrentView();
	}
}
