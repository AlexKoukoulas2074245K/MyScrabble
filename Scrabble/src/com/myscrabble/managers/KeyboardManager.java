package com.myscrabble.managers;

import org.lwjgl.input.Keyboard;

/**
 * 
 * @author Alex Koukoulas 2074245k
 *
 * A class that mediates between the user
 * and the keyboard. Key presses, releases
 * and general key state can be accessed
 * from here.
 */
public class KeyboardManager 
{   
    public static final int MAX_KEYS = 2;
    public static final int K_ESCAPE = 0;
    public static final int K_SPACE  = 1;
    
    private static boolean[] keysCurr = new boolean[MAX_KEYS];
    private static boolean[] keysPrev = new boolean[MAX_KEYS];
    
    public static void update()
    {
        for(int i = 0; i < MAX_KEYS; i++)
        {
            keysPrev[i] = keysCurr[i];
        }
    }
    
    public static void listenToInput()
    {
        keysCurr[K_ESCAPE] = Keyboard.isKeyDown(Keyboard.KEY_ESCAPE);
        keysCurr[K_SPACE]  = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
    }
    
    public static boolean isKeyDown(int keyFlag)
    {
        return keysCurr[keyFlag];
    }
    
    public static boolean isKeyPressed(int keyFlag)
    {
        return keysCurr[keyFlag] && !keysPrev[keyFlag];
    }
}
