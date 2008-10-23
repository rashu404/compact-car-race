package net.ropelato.compactcarrace.graphics3d;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Observable;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Vector3d;

import net.ropelato.compactcarrace.util.MyObservable;
import net.ropelato.compactcarrace.util.Util;

import com.glyphein.j3d.loaders.milkshape.MS3DLoader;
import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.Scene;

public class Model extends BranchGroup
{
    TransformGroup transformGroup = null;
    Transform3D transform3D = null;
    Scene scene = null;

    ArrayList collidingObjects = new ArrayList();
    boolean collision = false;
    boolean autoUpdate = false;

    float rotationX = 0f;
    float rotationY = 0f;
    float rotationZ = 0f;
    float positionX = 0f;
    float positionY = 0f;
    float positionZ = 0f;

    float scale = 0f;

    String fileName = "";

    //MyObservable observable = new MyObservable();

    public Model()
    {
        transform3D = new Transform3D();
        transformGroup = new TransformGroup(transform3D);

        setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        addChild(transformGroup);
    }

    public Model(String fileName)
    {
        this();
        this.fileName = fileName;
        try
        {
            Loader loader = new MS3DLoader(MS3DLoader.LOAD_ALL);
            File file = new java.io.File(fileName);
            if (file.getParent().length() > 0)
            {
                loader.setBasePath(file.getParent() + java.io.File.separator);
            }

            scene = loader.load(file.getName());

            BranchGroup branchGroup = scene.getSceneGroup();
            transformGroup.addChild(branchGroup);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Object clone()
    {
        Model cloneModel = new Model();

        TransformGroup cloneTransformGroup = new TransformGroup();
        Enumeration e = transformGroup.getAllChildren();
        while (e.hasMoreElements())
        {
            Node node = (Node) e.nextElement();
            Node cloneNode = node.cloneTree(true);
            cloneTransformGroup.addChild(cloneNode);
        }
        cloneTransformGroup.setCollidable(transformGroup.getCollidable());
        cloneTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        cloneTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Transform3D cloneTransform3D = new Transform3D();

        cloneModel.addChild(cloneTransformGroup);
        cloneModel.setTransformGroup(cloneTransformGroup);
        cloneModel.setTransform3D(cloneTransform3D);
        cloneModel.setScene(scene);
        cloneModel.setCollision(collision);
        cloneModel.setRotationX(rotationX);
        cloneModel.setRotationY(rotationY);
        cloneModel.setRotationZ(rotationZ);
        cloneModel.setPositionX(positionX);
        cloneModel.setPositionY(positionY);
        cloneModel.setPositionZ(positionZ);
        cloneModel.setScale(scale);
        cloneModel.setFileName(fileName);

        return cloneModel;
    }

    public void resetRotation()
    {
        transform3D.set(new AxisAngle4f(0, 0, 0, 0f));
        setScale(scale);
        setPosition(positionX, positionY, positionZ);
    }

    public void setRotationX(float rotationX)
    {
        setRotation(rotationX, rotationY, rotationZ);
    }

    public void setRotationY(float rotationY)
    {
        setRotation(rotationX, rotationY, rotationZ);
    }

    public void setRotationZ(float rotationZ)
    {
        setRotation(rotationX, rotationY, rotationZ);
    }

    public void setRotation(float rotationX, float rotationY, float rotationZ)
    {
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;

        Transform3D transformX = new Transform3D();
        Transform3D transformY = new Transform3D();
        Transform3D transformZ = new Transform3D();

        if (autoUpdate)
        {
            setAutoUpdate(false);
            resetRotation();
            setAutoUpdate(true);
        }
        else
        {
            resetRotation();
        }

        transformX.rotX(Math.toRadians(rotationX));
        transform3D.mul(transformX);
        transformZ.rotZ(Math.toRadians(rotationZ));
        transform3D.mul(transformZ);
        transformY.rotY(Math.toRadians(rotationY));
        transform3D.mul(transformY);

        if (autoUpdate)
        {
            update();
        }

    }

    public void setPositionX(float positionX)
    {
        setPosition(positionX, positionY, positionZ);
    }

    public void setPositionY(float positionY)
    {
        setPosition(positionX, positionY, positionZ);
    }

    public void setPositionZ(float positionZ)
    {
        setPosition(positionX, positionY, positionZ);
    }

    public void setPosition(float positionX, float positionY, float positionZ)
    {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;

        transform3D.setTranslation(new Vector3d(positionX, positionY, positionZ));

        if (autoUpdate)
        {
            update();
        }

    }

    public void setScale(float scale)
    {
        this.scale = scale;
        transform3D.setScale(scale);
    }

    public void restore()
    {
        setScale(1f);
        setRotation(0f, 0f, 0f);
        setPosition(0f, 0f, 0f);
        setCollision(false);
    }

    public void setCollision(boolean collision)
    {
        this.collision = collision;
    }

    public boolean isCollision()
    {
        return (collidingObjects.size() > 0);
    }

    public float getPositionX()
    {
        return positionX;
    }

    public float getPositionY()
    {
        return positionY;
    }

    public float getPositionZ()
    {
        return positionZ;
    }

    public float getRotationX()
    {
        return rotationX;
    }

    public float getRotationY()
    {
        return rotationY;
    }

    public float getRotationZ()
    {
        return rotationZ;
    }

    public float getScale()
    {
        return scale;
    }

    public String getFileName()
    {
        return fileName;
    }

    public Scene getScene()
    {
        return scene;
    }

    public void setScene(Scene scene)
    {
        this.scene = scene;
    }

    public void setTransformGroup(TransformGroup transformGroup)
    {
        this.transformGroup = transformGroup;
    }

    public void setTransform3D(Transform3D transform3D)
    {
        this.transform3D = transform3D;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public void update()
    {
        rotationX = Util.modifyAngle(rotationX);
        rotationY = Util.modifyAngle(rotationY);
        rotationZ = Util.modifyAngle(rotationZ);
        transformGroup.setTransform(transform3D);

        //observable.update();
        System.out.println("notifying");
    }

    public TransformGroup getTransformGroup()
    {
        return transformGroup;
    }

    public Transform3D getTransform3D()
    {
        return transform3D;
    }

    public ArrayList getCollidingObjects()
    {
        return collidingObjects;
    }

    public void setCollidingObjects(ArrayList collidingObjects)
    {
        this.collidingObjects = collidingObjects;
    }

    public boolean isAutoUpdate()
    {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate)
    {
        this.autoUpdate = autoUpdate;
    }

    /*public Observable getObservable()
    {
        return observable;
    }*/
}
