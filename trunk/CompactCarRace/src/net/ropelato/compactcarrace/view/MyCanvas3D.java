package net.ropelato.compactcarrace.view;

import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.J3DGraphics2D;

import net.ropelato.compactcarrace.graphics2d.PaintComponent;
import net.ropelato.compactcarrace.util.Util;

public class MyCanvas3D extends Canvas3D
{
    ArrayList paintComponents = new ArrayList();

    public MyCanvas3D(GraphicsConfiguration graphicsConfiguration)
    {
        super(graphicsConfiguration);
    }
    
    public void addPaintComponent(PaintComponent paintComponent)
    {
        paintComponents.add(paintComponent);
    }

    public void postRender()
    {
        J3DGraphics2D g = getGraphics2D();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for(int i=0; i<paintComponents.size(); i++)
        {
            PaintComponent paintComponent = (PaintComponent)paintComponents.get(i);
            paintComponent.paint(g);
        }
        
        g.flush(false);
        super.postRender();
    }

}
