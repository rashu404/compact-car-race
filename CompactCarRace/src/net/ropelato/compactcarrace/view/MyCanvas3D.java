package net.ropelato.compactcarrace.view;

import java.awt.GraphicsConfiguration;

import javax.media.j3d.Canvas3D;

public class MyCanvas3D extends Canvas3D
{
    public MyCanvas3D(GraphicsConfiguration graphicsConfiguration)
    {
        super(graphicsConfiguration);
        System.out.println("MyCanvas3D used");
    }
}
