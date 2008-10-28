package net.ropelato.compactcarrace.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.J3DGraphics2D;

import net.ropelato.compactcarrace.util.Util;

public class MyCanvas3D extends Canvas3D
{
    Image tacho = null;
    Image tachoPointer = null;
    AffineTransform imgTransform = null;

    public MyCanvas3D(GraphicsConfiguration graphicsConfiguration)
    {
        super(graphicsConfiguration);

        tacho = Util.loadImage(new File("./img/mini_tacho.png"));
        tachoPointer = Util.loadImage(new File("./img/mini_tacho_pointer.png"));
        imgTransform = new AffineTransform();
    }

    public void rotatePointer(float angle)
    {
        imgTransform.setToTranslation(20, 20);
        imgTransform.rotate(Math.toRadians(angle), tacho.getWidth(this) / 2, tacho.getHeight(this) / 2);

        // imgTransform.rotate(angle, img.getWidth(this)/2, img.getHeight(this)/2);
    }

    public void postRender()
    {
        J3DGraphics2D g = getGraphics2D();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(tacho, 20, 20, this);
        g.drawImage(tachoPointer, imgTransform, this);

        g.flush(false);
        super.postRender();
    }

}
