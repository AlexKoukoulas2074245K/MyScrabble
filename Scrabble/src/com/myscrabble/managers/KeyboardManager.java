package com.myscrabble.managers;

import java.util.HashMap;

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
    
    private static final HashMap<Integer, Character> keyCharacters;
    
    static
    {
    	keyCharacters = new HashMap<Integer, Character>();
    	
    	keyCharacters.put(Keyboard.KEY_0, '0');  keyCharacters.put(Keyboard.KEY_1, '1');
    	keyCharacters.put(Keyboard.KEY_2, '2');  keyCharacters.put(Keyboard.KEY_3, '3');
    	keyCharacters.put(Keyboard.KEY_4, '4');  keyCharacters.put(Keyboard.KEY_5, '5');
    	keyCharacters.put(Keyboard.KEY_6, '6');  keyCharacters.put(Keyboard.KEY_7, '7');
    	keyCharacters.put(Keyboard.KEY_8, '8');  keyCharacters.put(Keyboard.KEY_9, '9');
    	
    	keyCharacters.put(Keyboard.KEY_A, 'A');  keyCharacters.put(Keyboard.KEY_B, 'B');
    	keyCharacters.put(Keyboard.KEY_C, 'C');  keyCharacters.put(Keyboard.KEY_D, 'D');
    	keyCharacters.put(Keyboard.KEY_E, 'E');  keyCharacters.put(Keyboard.KEY_F, 'F');
    	keyCharacters.put(Keyboard.KEY_G, 'G');  keyCharacters.put(Keyboard.KEY_H, 'H');
    	keyCharacters.put(Keyboard.KEY_I, 'I');  keyCharacters.put(Keyboard.KEY_J, 'J');
    	keyCharacters.put(Keyboard.KEY_K, 'K');  keyCharacters.put(Keyboard.KEY_L, 'L');
    	keyCharacters.put(Keyboard.KEY_M, 'M');  keyCharacters.put(Keyboard.KEY_N, 'N');
    	keyCharacters.put(Keyboard.KEY_O, 'O');  keyCharacters.put(Keyboard.KEY_P, 'P');
    	keyCharacters.put(Keyboard.KEY_Q, 'Q');  keyCharacters.put(Keyboard.KEY_R, 'R');
    	keyCharacters.put(Keyboard.KEY_S, 'S');  keyCharacters.put(Keyboard.KEY_T, 'T');
    	keyCharacters.put(Keyboard.KEY_U, 'U');  keyCharacters.put(Keyboard.KEY_V, 'V');
    	keyCharacters.put(Keyboard.KEY_W, 'W');  keyCharacters.put(Keyboard.KEY_T, 'X');
    	keyCharacters.put(Keyboard.KEY_Y, 'Y');  keyCharacters.put(Keyboard.KEY_V, 'Z');
    }
    
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
    
    public static String getKeyChar(int key)
    {
    	return keyCharacters.get(key).toString();
    }
}
