package net.ropelato.compactcarrace.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

public class MyComponent extends Component
{
    
    public void paint(Graphics g)
    {
        System.out.println("paint");
        g.setColor(Color.RED);
        g.fillRect(0, 0, 320, 240);
        super.paint(g);
    }

}
