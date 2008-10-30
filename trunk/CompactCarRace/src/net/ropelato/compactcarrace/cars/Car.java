package net.ropelato.compactcarrace.cars;

import javax.media.j3d.BoundingSphere;
import javax.vecmath.Point3d;

import net.ropelato.compactcarrace.graphics3d.CollisionEntryDetector;
import net.ropelato.compactcarrace.graphics3d.CollisionExitDetector;
import net.ropelato.compactcarrace.graphics3d.Model;
import net.ropelato.compactcarrace.graphics3d.Terrain;
import net.ropelato.compactcarrace.world.World;

public class Car
{
    Model model = null;
    Tacho tacho = null;

    float positionX = 0f;
    float positionY = 0f;
    float positionZ = 0f;

    float rotationX = 0f;
    float rotationY = 0f;
    float rotationZ = 0f;

    float length = 2.5f;
    float width = 2f;
    float smoothMoves = 10f;
    float targetX = 0f;
    float targetY = 0f;
    float targetZ = 0f;
    float speed = 0f;
    float maxTurn = 3f;
    float maxSpeed = 0.5f;
    float minSpeed = -0.3f;
    float maxAcceleration = 0.004f;
    float maxDeceleration = 0.01f;
    float stdDeceleration = 0.002f;
    float pitchInfluence = 0.00018f;
    float acceleration = 0f;

    float steer = 0f;
    float pitch = 0f;
    float roll = 0f;
    
    boolean reverse = false;

    public Car(Model model)
    {
        super();
        this.model = model;

        model.setScale(0.1f);
        model.setCollidable(true);

        // setup collision detector
        BoundingSphere collisionBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);

        CollisionEntryDetector collisionEntryDetector = new CollisionEntryDetector(model);
        collisionEntryDetector.setSchedulingBounds(collisionBounds);
        model.addChild(collisionEntryDetector);

        CollisionExitDetector collisionExitDetector = new CollisionExitDetector(model);
        collisionExitDetector.setSchedulingBounds(collisionBounds);
        model.addChild(collisionExitDetector);
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
        model.setPosition(positionX, positionY, positionZ);
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
        model.setRotationX(rotationX);
    }

    public void setRotationY(float rotationY)
    {
        model.setRotationY(rotationY);
    }

    public void setRotationZ(float rotationZ)
    {
        model.setRotationZ(rotationZ);
    }

    public void setRotation(float rotationX, float rotationY, float rotationZ)
    {
        model.setRotation(rotationX, rotationY, rotationZ);
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

        centerY = Math.max(centerY, (leftY + rightY) / 2);

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
            if(acceleration < 0 && speed < 0)
            {
                reverse = true;
            }
            if(speed > 0)
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

        this.move(1f / (float) Math.sqrt(1 + Math.pow(Math.tan(Math.toRadians(Math.abs(pitch))), 2d)) * speed);

        if (tacho != null)
        {
            tacho.rotatePointer(Math.abs(speed) * 100);
        }
    }

    public void updatePhysics()
    {
        setPosition(positionX, positionY, positionZ);
        setRotation(rotationX, rotationY, rotationZ);
        model.update();
    }

    public Model getModel()
    {
        return model;
    }

    public void setModel(Model model)
    {
        this.model = model;
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
        return model.isCollision();
    }

    public void resetCollision()
    {
        model.getCollidingObjects().clear();
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
}
