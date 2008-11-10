package net.ropelato.compactcarrace.cars;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

import com.jlindamood.MS3D.*;

import net.ropelato.compactcarrace.graphics3d.CollisionEntryDetector;
import net.ropelato.compactcarrace.graphics3d.CollisionExitDetector;
import net.ropelato.compactcarrace.graphics3d.Model;
import net.ropelato.compactcarrace.graphics3d.Terrain;
import net.ropelato.compactcarrace.world.World;

public class Car
{
    Model model = null;
    Model collisionModel = null;
    Tacho tacho = null;

    Transform3D[] jointMovements = null;

    float positionX = 0f;
    float positionY = 0f;
    float positionZ = 0f;

    float rotationX = 0f;
    float rotationY = 0f;
    float rotationZ = 0f;

    float oldPositionX = 0f;
    float oldPositionY = 0f;
    float oldPositionZ = 0f;

    float oldRotationX = 0f;
    float oldRotationY = 0f;
    float oldRotationZ = 0f;

    float length = 2.5f;
    float width = 2f;
    float scale = 0.12f;
    float smoothMoves = 10f;
    float targetX = 0f;
    float targetY = 0f;
    float targetZ = 0f;
    float speed = 0f;
    float maxTurn = 2f;
    float maxSpeed = 0.5f;
    float minSpeed = -0.3f;
    float maxAcceleration = 0.004f;
    float maxDeceleration = 0.025f;
    float stdDeceleration = 0.002f;
    float pitchInfluence = 0.00021f;
    float acceleration = 0f;

    Transform3D wheelsTransform = null;
    Transform3D frontWheelsTransform = null;

    float steer = 0f;
    float pitch = 0f;
    float roll = 0f;

    boolean reverse = false;

    float wheelsRotation = 0f;

    int frontLeftWheel = 2;
    int frontRightWheel = 3;
    int backLeftWheel = 5;
    int backRightWheel = 6;

    public Car(Model model)
    {
        this(model, null);
    }

    public Car(Model model, Model collisionModel)
    {
        super();
        this.model = model;
        this.collisionModel = collisionModel;

        // setup collision detector
        BoundingSphere collisionBounds = World.INFINITE_BOUNDINGSPHERE;

        model.setScale(scale);

        if (collisionModel == null)
        {
            model.setCollidable(true);

            CollisionEntryDetector collisionEntryDetector = new CollisionEntryDetector(model);
            collisionEntryDetector.setSchedulingBounds(collisionBounds);
            model.addChild(collisionEntryDetector);

            CollisionExitDetector collisionExitDetector = new CollisionExitDetector(model);
            collisionExitDetector.setSchedulingBounds(collisionBounds);
            model.addChild(collisionExitDetector);

        }
        else
        {
            collisionModel.setScale(scale);
            model.setCollidable(false);
            collisionModel.setCollidable(false);

            CollisionEntryDetector collisionEntryDetector = new CollisionEntryDetector(collisionModel);
            collisionEntryDetector.setSchedulingBounds(collisionBounds);
            collisionModel.addChild(collisionEntryDetector);

            CollisionExitDetector collisionExitDetector = new CollisionExitDetector(collisionModel);
            collisionExitDetector.setSchedulingBounds(collisionBounds);
            collisionModel.addChild(collisionExitDetector);
        }
    }

    public Tacho getTacho()
    {
        return tacho;
    }

    public void setTacho(Tacho tacho)
    {
        this.tacho = tacho;
    }

    public void setPositionX(float positionX)
    {
        model.setPositionX(positionX);
    }

    public void setPositionY(float positionY)
    {
        model.setPositionY(positionY);
    }

    public void setPositionZ(float positionZ)
    {
        model.setPositionZ(positionZ);
    }

    public void setPosition(float positionX, float positionY, float positionZ)
    {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
    }

    public float getPositionX()
    {
        return model.getPositionX();
    }

    public float getPositionY()
    {
        return model.getPositionY();
    }

    public float getPositionZ()
    {
        return model.getPositionZ();
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
    }

    public void adaptToTerrain(World world)
    {
        Terrain activeTerrain;

        float xLength = this.getWidth() * (float) (Math.cos(Math.toRadians(rotationY)) + 1) * 0.5f + this.getLength() * (float) (Math.sin(Math.toRadians(rotationY)) + 1) * 0.5f;
        float zLength = this.getLength() * (float) (Math.cos(Math.toRadians(rotationY)) + 1) * 0.5f + this.getWidth() * (float) (Math.sin(Math.toRadians(rotationY)) + 1) * 0.5f;

        float frontX = positionX;
        float frontZ = positionZ - zLength / 2;
        float frontY = 0f;
        activeTerrain = world.getActiveTerrain(frontX, frontZ);
        if (activeTerrain != null)
        {
            frontY = activeTerrain.getPositionY(frontX, frontZ);
        }

        float backX = positionX;
        float backZ = positionZ + zLength / 2;
        float backY = 0f;
        activeTerrain = world.getActiveTerrain(backX, backZ);
        if (activeTerrain != null)
        {
            backY = activeTerrain.getPositionY(backX, backZ);
        }

        float leftX = positionX - xLength / 2;
        float leftZ = positionZ;
        float leftY = 0f;
        activeTerrain = world.getActiveTerrain(leftX, leftZ);
        if (activeTerrain != null)
        {
            leftY = activeTerrain.getPositionY(leftX, leftZ);
        }

        float rightX = positionX + xLength / 2;
        float rightZ = positionZ;
        float rightY = 0f;
        activeTerrain = world.getActiveTerrain(rightX, rightZ);
        if (activeTerrain != null)
        {
            rightY = activeTerrain.getPositionY(rightX, rightZ);
        }

        float centerX = positionX;
        float centerZ = positionZ;
        float centerY = 0f;
        activeTerrain = world.getActiveTerrain(centerX, centerZ);
        if (activeTerrain != null)
        {
            centerY = activeTerrain.getPositionY(centerX, centerZ);
        }

        // centerY = Math.max(centerY, (leftY + rightY) / 2);

        rotationX = (float) Math.toDegrees(Math.atan((frontY - backY) / zLength));
        rotationZ = (float) Math.toDegrees(Math.atan((rightY - leftY) / xLength));

        pitch = rotationX * (float) Math.cos(Math.toRadians(rotationY)) - rotationZ * (float) Math.sin(Math.toRadians(rotationY));
        roll = -rotationZ * (float) Math.cos(Math.toRadians(rotationY)) - rotationX * (float) Math.sin(Math.toRadians(rotationY));

        positionY = centerY;

    }

    public float getRotationX()
    {
        return model.getRotationX();
    }

    public float getRotationY()
    {
        return model.getRotationY();
    }

    public float getRotationZ()
    {
        return model.getRotationZ();
    }

    public void update()
    {
        updateValues();
        updatePhysics();
    }

    public void updateValues()
    {
        speed -= pitch * pitchInfluence;

        if ((acceleration > 0 && speed < maxSpeed) || (acceleration < 0 && speed > minSpeed))
        {
            if (acceleration < 0 && speed < 0)
            {
                reverse = true;
            }
            if (acceleration > 0 && speed > 0)
            {
                reverse = false;
            }
            speed += acceleration;
            acceleration = 0f;
        }
        else
        {
            if (speed > stdDeceleration)
            {
                speed -= stdDeceleration;
            }
            else if (speed < stdDeceleration * -1)
            {
                speed += stdDeceleration;
            }
            else
            {
                speed = 0f;
            }
        }

        if (!isCollision())
        {
            oldPositionX = positionX;
            oldPositionY = positionY;
            oldPositionZ = positionZ;

            oldRotationX = rotationX;
            oldRotationY = rotationY;
            oldRotationZ = rotationZ;
        }

        this.move(1f / (float) Math.sqrt(1 + Math.pow(Math.tan(Math.toRadians(Math.abs(pitch))), 2d)) * speed);

        if (tacho != null)
        {
            tacho.rotatePointer(Math.abs(speed) * 100);
        }
    }

    public void updatePhysics()
    {
        model.setRotation(rotationX, rotationY, rotationZ);
        model.setPosition(positionX, positionY, positionZ);
        model.update();

        if (collisionModel != null)
        {
            collisionModel.setRotation(rotationX, rotationY, rotationZ);
            collisionModel.setPosition(positionX, positionY, positionZ);
            collisionModel.update();
        }

        // turn wheels
        if (frontWheelsTransform != null)
        {
            model.getJoint(frontLeftWheel).getLocalRefMatrix().mulInverse(frontWheelsTransform);
            model.getJoint(frontRightWheel).getLocalRefMatrix().mulInverse(frontWheelsTransform);
        }
        if (wheelsTransform != null)
        {
            model.getJoint(backLeftWheel).getLocalRefMatrix().mulInverse(wheelsTransform);
            model.getJoint(backRightWheel).getLocalRefMatrix().mulInverse(wheelsTransform);
        }

        wheelsRotation += speed * -50f;

        wheelsTransform = new Transform3D();
        wheelsTransform.rotX(Math.toRadians(wheelsRotation));

        frontWheelsTransform = new Transform3D();
        frontWheelsTransform.rotY(Math.toRadians(steer * 10));

        frontWheelsTransform.mul(wheelsTransform);

        model.getJoint(frontLeftWheel).getLocalRefMatrix().mul(frontWheelsTransform);
        model.getJoint(frontRightWheel).getLocalRefMatrix().mul(frontWheelsTransform);

        model.getJoint(backLeftWheel).getLocalRefMatrix().mul(wheelsTransform);
        model.getJoint(backRightWheel).getLocalRefMatrix().mul(wheelsTransform);

    }

    public void restore()
    {
        positionX = oldPositionX;
        positionY = oldPositionY;
        positionZ = oldPositionZ;

        rotationX = oldRotationX;
        rotationY = oldRotationY;
        rotationZ = oldRotationZ;

        resetCollision();
    }

    public Model getModel()
    {
        return model;
    }

    public void setModel(Model model)
    {
        this.model = model;
    }

    public Model getCollisionModel()
    {
        return collisionModel;
    }

    public void setCollisionModel(Model collisionModel)
    {
        this.collisionModel = collisionModel;
    }

    public void turnY(float turnY)
    {
        rotationY += turnY;
    }

    public void move(float speed)
    {
        positionX -= (float) Math.sin(Math.toRadians(rotationY)) * speed;
        positionZ -= (float) Math.cos(Math.toRadians(rotationY)) * speed;
    }

    public boolean isCollision()
    {
        if (collisionModel != null)
        {
            return collisionModel.isCollision();
        }
        else
        {
            return model.isCollision();
        }
    }

    public void resetCollision()
    {
        if (collisionModel != null)
        {
            collisionModel.getCollidingObjects().clear();
        }
        else
        {
            model.getCollidingObjects().clear();
        }
    }

    public float getSpeed()
    {
        return speed;
    }

    public float getMaxTurn()
    {
        return maxTurn;
    }

    public float getMaxAcceleration()
    {
        return maxAcceleration;
    }

    public float getMaxDeceleration()
    {
        return maxDeceleration;
    }

    public void setMaxDeceleration(float maxDeceleration)
    {
        this.maxDeceleration = maxDeceleration;
    }

    public void setSmoothMoves(float smoothMoves)
    {
        this.smoothMoves = smoothMoves;
    }

    public float getTargetX()
    {
        return targetX;
    }

    public void setTargetX(float targetX)
    {
        this.targetX = targetX;
    }

    public float getTargetY()
    {
        return targetY;
    }

    public void setTargetY(float targetY)
    {
        this.targetY = targetY;
    }

    public float getTargetZ()
    {
        return targetZ;
    }

    public void setTargetZ(float targetZ)
    {
        this.targetZ = targetZ;
    }

    public void setTargetPosition(float targetX, float targetY, float targetZ)
    {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    public void accelerate(float acceleration)
    {
        this.acceleration = acceleration;
    }

    public void steer(float angle)
    {
        this.steer = angle;
        this.turnY(angle * speed / maxSpeed);
    }

    public float getLength()
    {
        return length;
    }

    public void setLength(float length)
    {
        this.length = length;
    }

    public float getWidth()
    {
        return width;
    }

    public void setWidth(float width)
    {
        this.width = width;
    }

    public float getSteer()
    {
        return steer;
    }

    public boolean isReverse()
    {
        return reverse;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

}
