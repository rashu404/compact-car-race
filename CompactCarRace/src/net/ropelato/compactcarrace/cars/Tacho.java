package net.ropelato.compactcarrace.cars;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.media.j3d.J3DGraphics2D;

import net.ropelato.compactcarrace.graphics2d.PaintComponent;
import net.ropelato.compactcarrace.util.Util;

public class Tacho implements PaintComponent
{
    Image tacho = null;
    Image tachoPointer = null;
    AffineTransform pointerTransform = new AffineTransform();
    ImageObserver imageObserver = null;

    public Tacho(String tachoImage, String pointerImage, ImageObserver imageObserver)
    {
        tacho = Util.loadImage(new File(tachoImage));
        tachoPointer = Util.loadImage(new File(pointerImage));

        this.imageObserver = imageObserver;
        pointerTransform = new AffineTransform();
    }

    public void rotatePointer(float angle)
    {
        if (tachoPointer != null)
        {
            pointerTransform.setToTranslation(20, 20);
            pointerTransform.rotate(Math.toRadians(angle), tachoPointer.getWidth(imageObserver) / 2, tachoPointer.getHeight(imageObserver) / 2);
        }
    }

    public void paint(J3DGraphics2D g)
    {
        g.drawImage(tacho, 20, 20, imageObserver);
        g.drawImage(tachoPointer, pointerTransform, imageObserver);
    }
}
