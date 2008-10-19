package net.ropelato.compactcarrace.graphics3d;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

public class Camera
{
    TransformGroup transformGroup = null;

    Transform3D transform3D = null;

    float rotationX = 0f;

    float rotationY = 0f;

    float rotationZ = 0f;

    float positionX = 0f;

    float positionY = 0f;

    float positionZ = 0f;

    int cameraMode = 0;

    Model targetModel = null;

    float cameraDistance = 10f;

    float cameraSpeed = 0f;

    float cameraHeight = 2f;

    float distance = 0f;

    float higherThanTargetModel = 1.5f;

    boolean updateReady = false;

    public static int STATIC = 0;

    public static int THIRD_PERSON = 1;

    public static int FIRST_PERSON = 2;

    public Camera(TransformGroup transformGroup)
    {
        this.transformGroup = transformGroup;
        transform3D = new Transform3D();
    }

    public void setRotationX(float rotationX)
    {
        Transform3D transformX = new Transform3D();
        transformX.rotX(Math.toRadians(rotationX - this.rotationX));
        transform3D.mul(transformX);

        this.rotationX = rotationX;
    }

    public void setRotationY(float rotationY)
    {
        Transform3D transformY = new Transform3D();
        transformY.rotY(Math.toRadians(rotationY - this.rotationY));
        transform3D.mul(transformY);

        this.rotationY = rotationY;
    }

    public void setRotationZ(float rotationZ)
    {
        Transform3D transformZ = new Transform3D();
        transformZ.rotZ(Math.toRadians(rotationZ - this.rotationZ));
        transform3D.mul(transformZ);

        this.rotationZ = rotationZ;
    }

    public void setRotation(float rotationX, float rotationY, float rotationZ)
    {
        Transform3D transformX = new Transform3D();
        Transform3D transformY = new Transform3D();
        Transform3D transformZ = new Transform3D();

        transformX.rotX(Math.toRadians(rotationX - this.rotationX));
        transform3D.mul(transformX);
        transformY.rotY(Math.toRadians(rotationY - this.rotationY));
        transform3D.mul(transformY);
        transformZ.rotZ(Math.toRadians(rotationZ - this.rotationZ));
        transform3D.mul(transformZ);

        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
    }

    public void setPositionX(float positionX)
    {
        this.positionX = positionX;
        transform3D.setTranslation(new Vector3d(positionX, positionY, positionZ));
    }

    public void setPositionY(float positionY)
    {
        this.positionY = positionY;
        transform3D.setTranslation(new Vector3d(positionX, positionY, positionZ));
    }

    public void setPositionZ(float positionZ)
    {
        this.positionZ = positionZ;
        transform3D.setTranslation(new Vector3d(positionX, positionY, positionZ));
    }

    public void setPosition(float positionX, float positionY, float positionZ)
    {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        transform3D.setTranslation(new Vector3d(positionX, positionY, positionZ));
    }

    public void setTargetModel(Model targetModel)
    {
        this.targetModel = targetModel;
    }

    public void setCameraMode(int cameraMode)
    {
        this.cameraMode = cameraMode;
    }

    public void setUpdateReady(boolean updateReady)
    {
        this.updateReady = updateReady;
    }

    public boolean getUpdateReady()
    {
        return updateReady;
    }

    public void update(boolean soft)
    {
        updateCameraPosition(soft);
        transformGroup.setTransform(transform3D);
        updateReady = true;
    }

    private void updateCameraPosition(boolean soft)
    {
        if (targetModel != null)
        {
            if (cameraMode == THIRD_PERSON)
            {
                positionX = targetModel.getPositionX();
                positionZ = targetModel.getPositionZ();

                boolean correction = true;
                while (correction)
                {
                    correction = false;
                    if (targetModel.getRotationY() >= rotationY + 180)
                    {
                        rotationY += 360;
                        correction = true;
                    }
                    if (targetModel.getRotationY() <= rotationY - 180)
                    {
                        rotationY -= 360;
                        correction = true;
                    }
                }

                if (soft)
                    setRotationY((getRotationY() * 10 + targetModel.getRotationY()) / 11);
                else
                    setRotationY(targetModel.getRotationY());

                distance = (float) Math.sqrt(((targetModel.getPositionX() - positionX) * (targetModel.getPositionX() - positionX)) + ((targetModel.getPositionZ() - positionZ) * (targetModel.getPositionZ() - positionZ)));
                cameraSpeed = -1 * cameraDistance;

                positionX -= (float) Math.sin(Math.toRadians(rotationY)) * -1 * cameraDistance;
                positionZ -= (float) Math.cos(Math.toRadians(rotationY)) * -1 * cameraDistance;
                positionY = targetModel.getPositionY() + cameraHeight;

                setPosition(positionX, positionY, positionZ);
            }
            if (cameraMode == FIRST_PERSON)
            {
                setRotationY(targetModel.getRotationY());
                setRotationX(0f);

                positionX = targetModel.getPositionX();
                positionZ = targetModel.getPositionZ();
                positionY = targetModel.getPositionY() + higherThanTargetModel;

                setPosition(positionX, positionY, positionZ);
            }
            if (cameraMode == STATIC)
            {
            	if(targetModel.getPositionZ() - getPositionZ() != 0f)
            	{
	                if (targetModel.getPositionZ() >= positionZ)
	                {
	                    setRotationY((float) Math.toDegrees(Math.atan((((targetModel.getPositionX() - getPositionX())) / ((targetModel.getPositionZ() - getPositionZ()))))) + 180);
	                }
	                else
	                {
	                    setRotationY((float) Math.toDegrees(Math.atan((((targetModel.getPositionX() - getPositionX())) / ((targetModel.getPositionZ() - getPositionZ()))))));
	                }
            	}

                setPosition(positionX, positionY, positionZ);
            }
        }
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

    public TransformGroup getTransformGroup()
    {
        return transformGroup;
    }

    public Transform3D getTransform3D()
    {
        return transform3D;
    }
    
    public void changeView()
    {
    	cameraMode++;
    	if(cameraMode > 2)
    	{
    		cameraMode = 0;
    	}
    }
}
