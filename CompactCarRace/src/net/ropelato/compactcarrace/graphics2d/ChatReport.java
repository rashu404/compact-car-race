package net.ropelato.compactcarrace.graphics2d;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextArea;

import net.ropelato.compactcarrace.util.Util;

public class ChatReport
{
    JTextArea textArea = null;

    String[] messages = new String[6];

    RemoveThread removeThread = null;

    long keepMessages = 2000;

    int maxMessageId = 0;

    long loginTime = 0;

    public ChatReport()
    {
        textArea = new JTextArea();
        textArea.setAutoscrolls(true);
        textArea.setEditable(false);
        textArea.setRows(messages.length);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("Arial", Font.PLAIN, 11));

        // removeThread = new RemoveThread(keepMessages);
        // removeThread.start();
    }

    public void addMessage(int id, String message)
    {
        maxMessageId = id;

        for (int i = messages.length - 1; i > 0; i--)
        {
            messages[i] = messages[i - 1];
        }
        messages[0] = message;

        updateTextArea();
    }

    public void removeOldest()
    {

        boolean removed = false;
        for (int i = messages.length - 1; i >= 0 && !removed; i--)
        {
            if (messages[i] != null)
            {
                messages[i] = null;
                removed = true;
            }
        }
        updateTextArea();
    }

    private void updateTextArea()
    {
        String text = "";
        for (int i = messages.length - 1; i >= 0; i--)
        {
            if (messages[i] != null)
            {
                text += messages[i];
                if (i > 0)
                    text += "\n";
            }
        }
        // removeThread.resetCountDown();
        textArea.setText(text);
    }

    public JTextArea getTextArea()
    {
        return textArea;
    }

    public int getMaxMessageId()
    {
        return maxMessageId;
    }

    public long getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(long loginTime)
    {
        this.loginTime = loginTime;
    }

    protected class RemoveThread extends Thread
    {
        long duration = 1000;

        boolean reset = false;

        public RemoveThread(long duration)
        {
            this.duration = duration;
        }

        public void resetCountDown()
        {
            reset = true;
        }

        public void run()
        {
            while (true)
            {
                Util.delay(2000);
                if (reset)
                {
                    reset = false;
                }
                else
                {
                    removeOldest();
                }
            }
        }
    }

}
