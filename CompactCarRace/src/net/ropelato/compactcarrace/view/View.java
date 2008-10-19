package net.ropelato.compactcarrace.view;

import java.awt.GraphicsConfiguration;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.TransformGroup;

import net.ropelato.compactcarrace.graphics3d.Camera;

import com.sun.j3d.utils.universe.SimpleUniverse;

public class View
{

    Canvas3D canvas3D = null;

    GraphicsConfiguration graphicsConfiguration = null;

    SimpleUniverse universe = null;

    Camera camera = null;

    public View()
    {
        // *** set up graphics configuration ***
        // GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);
        graphicsConfiguration = SimpleUniverse.getPreferredConfiguration();
        canvas3D = new Canvas3D(graphicsConfiguration);
        canvas3D.setDoubleBufferEnable(true);
        canvas3D.getGraphicsContext3D().setBufferOverride(false);

        // *** create universe ***
        universe = new SimpleUniverse(canvas3D);
        universe.getViewer().getView().setSceneAntialiasingEnable(true);
        canvas3D.getView().setFrontClipDistance(0.1f);
        canvas3D.getView().setBackClipDistance(50000f);
        // universe.setJ3DThreadPriority(Thread.MIN_PRIORITY);

        // *** create camera ***
        TransformGroup cameraTransformGroup = universe.getViewingPlatform().getMultiTransformGroup().getTransformGroup(0);
        camera = new Camera(cameraTransformGroup);

        canvas3D.startRenderer();
    }

    public void addBranchGroup(BranchGroup branchGroup)
    {
        universe.addBranchGraph(branchGroup);
    }

    public Canvas3D getCanvas3D()
    {
        return canvas3D;
    }

    public GraphicsConfiguration getGraphicsConfiguration()
    {
        return graphicsConfiguration;
    }

    public SimpleUniverse getUniverse()
    {
        return universe;
    }

    public Camera getCamera()
    {
        return camera;
    }

}
