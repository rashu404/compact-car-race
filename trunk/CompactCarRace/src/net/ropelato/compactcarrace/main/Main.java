package net.ropelato.compactcarrace.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.media.j3d.BranchGroup;
import javax.swing.JFrame;

import net.ropelato.compactcarrace.cars.Car;
import net.ropelato.compactcarrace.controls.Controller;
import net.ropelato.compactcarrace.graphics2d.ChatReport;
import net.ropelato.compactcarrace.graphics3d.Camera;
import net.ropelato.compactcarrace.graphics3d.Model;
import net.ropelato.compactcarrace.graphics3d.MyPointLight;
import net.ropelato.compactcarrace.graphics3d.Terrain;
import net.ropelato.compactcarrace.util.Util;
import net.ropelato.compactcarrace.view.View;
import net.ropelato.compactcarrace.world.World;

public class Main extends Thread
{
    View view = null;
    ChatReport chatReport = null;
    Car myCar = null;
    Controller controller = null;
    World world = null;
    int delay = 15;

    public static JFrame frame = null;

    private Main()
    {
        // create frame
        frame = new JFrame();
        frame.setTitle("Compact Car Race");
        frame.setSize(640, 480);
        frame.setLocation(100, 100);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout(1, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create view
        view = new View();

        // add view to frame
        frame.getContentPane().setBackground(Color.BLACK);
        view.getCanvas3D().setVisible(false);
        frame.getContentPane().add(view.getCanvas3D(), BorderLayout.CENTER);
        view.getCanvas3D().addKeyListener(new CanvasKeyListener());

        // create chat report
        chatReport = new ChatReport();

        // show frame
        frame.setVisible(true);

        // create world
        String worldDescriptor = selectWorld();
        world = new World(worldDescriptor);

        ArrayList worldModels = world.getModels();
        for (int i = 0; i < worldModels.size(); i++)
        {
            Model model = (Model) worldModels.get(i);
            view.addBranchGroup(model);
        }

        BranchGroup ambientLight = world.getAmbientLightBG();
        view.addBranchGroup(ambientLight);

        ArrayList worldPointLights = world.getPointLights();
        for (int i = 0; i < worldPointLights.size(); i++)
        {
            MyPointLight pointLight = (MyPointLight) worldPointLights.get(i);
            view.addBranchGroup(pointLight);
        }

        ArrayList worldTerrains = world.getTerrains();
        for (int i = 0; i < worldTerrains.size(); i++)
        {
            Terrain terrain = (Terrain) worldTerrains.get(i);
            view.addBranchGroup(terrain);
        }

        // create cars
        Model myCarModel = new Model("./cars/minicooper/minicooper1.ms3d");
        // Model myCarModel = new Model("./cars/vespa/vespa01b.ms3d");
        myCar = new Car(myCarModel);
        myCar.getModel().setCollidable(true);
        view.addBranchGroup(myCar.getModel());

        // setup camera
        view.getCamera().setCameraMode(Camera.THIRD_PERSON);
        view.getCanvas3D().getGraphicsContext3D().setBufferOverride(false);

        // define controls
        controller = new Controller(view.getCanvas3D());

        controller.addCommand("turnLeft", Controller.KEYBOARD, KeyEvent.VK_LEFT);
        controller.addCommand("turnRight", Controller.KEYBOARD, KeyEvent.VK_RIGHT);
        controller.addCommand("forward", Controller.KEYBOARD, KeyEvent.VK_UP);
        controller.addCommand("backward", Controller.KEYBOARD, KeyEvent.VK_DOWN);
        controller.addCommand("changeCamera", Controller.KEYBOARD, KeyEvent.VK_C, true);

        // start main thread
        this.start();
    }

    public void run()
    {
        view.getCanvas3D().setVisible(true);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setVisible(true);
        view.getCanvas3D().requestFocus();

        myCar.update();
        myCar.getModel().setCollision(false);

        Util.startFPSCounter();

        while (true)
        {

            // control car
            if (!myCar.isCollision())
            {
                if (controller.getCommand("turnLeft") > 0)
                    myCar.turnLeft(myCar.getMaxTurn() * controller.getCommand("turnLeft"));

                if (controller.getCommand("turnRight") > 0)
                    myCar.turnRight(myCar.getMaxTurn() * controller.getCommand("turnRight"));

                if (controller.getCommand("forward") > 0)
                    myCar.accelerate(myCar.getMaxAcceleration() * controller.getCommand("forward"));

                if (controller.getCommand("backward") > 0)
                    myCar.decelerate(myCar.getMaxDeceleration() * controller.getCommand("backward"));

            }
            else
            {
                myCar.resetCollision();
            }

            Terrain activeTerrain = world.getActiveTerrain(myCar.getPositionX(), myCar.getPositionZ());

            myCar.adaptToTerrain(activeTerrain);

            // change camera view
            if (controller.getCommand("changeCamera") == 1)
            {
                view.getCamera().changeView();
            }

            view.getCamera().setTargetModel(myCar.getModel());
            view.getCamera().update(true);

            view.getCanvas3D().getGraphicsContext3D().flush(true);
            Util.delay(delay);
            myCar.update();

            // System.out.println(Util.getFPSAveraage());
        }
    }

    public String selectWorld()
    {
        String worldDescriptor = "world/world3.xml";
        return worldDescriptor;
    }

    protected class CanvasKeyListener extends KeyAdapter
    {
        public void keyTyped(KeyEvent e)
        {
            // ...
        }
    }

    public static void main(String[] args)
    {
        new Main();
    }
}
