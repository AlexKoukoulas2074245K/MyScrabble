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
    public static final int MAX_KEYS = 40;
    
    public static final int K_ESCAPE = 0x00;
    public static final int K_SPACE  = 0x01;
    public static final int K_A      = 0x02;
    public static final int K_B      = 0x03;
    public static final int K_C      = 0x04;
    public static final int K_D      = 0x05;
    public static final int K_E      = 0x06;
    public static final int K_F      = 0x07;
    public static final int K_G      = 0x08;
    public static final int K_H      = 0x09;
    public static final int K_I      = 0x0a;
    public static final int K_J      = 0x0b;
    public static final int K_K      = 0x0c;
    public static final int K_L      = 0x0d;
    public static final int K_M      = 0x0e;
    public static final int K_N      = 0x0f;
    public static final int K_O      = 0x10;
    public static final int K_P      = 0x11;
    public static final int K_Q      = 0x12;
    public static final int K_R      = 0x13;
    public static final int K_S      = 0x14;
    public static final int K_T      = 0x15;
    public static final int K_U      = 0x16;
    public static final int K_V      = 0x17;
    public static final int K_W      = 0x18;
    public static final int K_X      = 0x19;
    public static final int K_Y      = 0x1a;
    public static final int K_Z      = 0x1b;
    public static final int K_0      = 0x1c;
    public static final int K_1      = 0x1d;
    public static final int K_2      = 0x1e;
    public static final int K_3      = 0x1f;
    public static final int K_4      = 0x20;
    public static final int K_5      = 0x21;
    public static final int K_6      = 0x22;
    public static final int K_7      = 0x23;
    public static final int K_8      = 0x24;
    public static final int K_9      = 0x25;
    
    public static final int K_BACKSPACE = 0x26;
    public static final int K_ENTER     = 0x27;
    
    private static boolean[] keysCurr = new boolean[MAX_KEYS];
    private static boolean[] keysPrev = new boolean[MAX_KEYS];
    
    private static final HashMap<Integer, Character> keyCharacters;
    
    static
    {
    	keyCharacters = new HashMap<Integer, Character>();
    	
    	keyCharacters.put(KeyboardManager.K_SPACE, ' ');
    	keyCharacters.put(KeyboardManager.K_BACKSPACE, '-');
    	keyCharacters.put(KeyboardManager.K_ENTER, '@');
    	
    	keyCharacters.put(KeyboardManager.K_0, '0');  keyCharacters.put(KeyboardManager.K_1, '1');
    	keyCharacters.put(KeyboardManager.K_2, '2');  keyCharacters.put(KeyboardManager.K_3, '3');
    	keyCharacters.put(KeyboardManager.K_4, '4');  keyCharacters.put(KeyboardManager.K_5, '5');
    	keyCharacters.put(KeyboardManager.K_6, '6');  keyCharacters.put(KeyboardManager.K_7, '7');
    	keyCharacters.put(KeyboardManager.K_8, '8');  keyCharacters.put(KeyboardManager.K_9, '9');
    	
    	keyCharacters.put(KeyboardManager.K_A, 'A');  keyCharacters.put(KeyboardManager.K_B, 'B');
    	keyCharacters.put(KeyboardManager.K_C, 'C');  keyCharacters.put(KeyboardManager.K_D, 'D');
    	keyCharacters.put(KeyboardManager.K_E, 'E');  keyCharacters.put(KeyboardManager.K_F, 'F');
    	keyCharacters.put(KeyboardManager.K_G, 'G');  keyCharacters.put(KeyboardManager.K_H, 'H');
    	keyCharacters.put(KeyboardManager.K_I, 'I');  keyCharacters.put(KeyboardManager.K_J, 'J');
    	keyCharacters.put(KeyboardManager.K_K, 'K');  keyCharacters.put(KeyboardManager.K_L, 'L');
    	keyCharacters.put(KeyboardManager.K_M, 'M');  keyCharacters.put(KeyboardManager.K_N, 'N');
    	keyCharacters.put(KeyboardManager.K_O, 'O');  keyCharacters.put(KeyboardManager.K_P, 'P');
    	keyCharacters.put(KeyboardManager.K_Q, 'Q');  keyCharacters.put(KeyboardManager.K_R, 'R');
    	keyCharacters.put(KeyboardManager.K_S, 'S');  keyCharacters.put(KeyboardManager.K_T, 'T');
    	keyCharacters.put(KeyboardManager.K_U, 'U');  keyCharacters.put(KeyboardManager.K_V, 'V');
    	keyCharacters.put(KeyboardManager.K_W, 'W');  keyCharacters.put(KeyboardManager.K_X, 'X');
    	keyCharacters.put(KeyboardManager.K_Y, 'Y');  keyCharacters.put(KeyboardManager.K_Z, 'Z');
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
        keysCurr[K_ESCAPE]    = Keyboard.isKeyDown(Keyboard.KEY_ESCAPE);
        keysCurr[K_SPACE]     = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        keysCurr[K_BACKSPACE] = Keyboard.isKeyDown(Keyboard.KEY_BACK);
        keysCurr[K_ENTER]     = Keyboard.isKeyDown(Keyboard.KEY_RETURN);
        keysCurr[K_A]         = Keyboard.isKeyDown(Keyboard.KEY_A);
        keysCurr[K_B]         = Keyboard.isKeyDown(Keyboard.KEY_B);
        keysCurr[K_C]         = Keyboard.isKeyDown(Keyboard.KEY_C);
        keysCurr[K_D]         = Keyboard.isKeyDown(Keyboard.KEY_D);
        keysCurr[K_E]         = Keyboard.isKeyDown(Keyboard.KEY_E);
        keysCurr[K_F]         = Keyboard.isKeyDown(Keyboard.KEY_F);
        keysCurr[K_G]         = Keyboard.isKeyDown(Keyboard.KEY_G);
        keysCurr[K_H]         = Keyboard.isKeyDown(Keyboard.KEY_H);
        keysCurr[K_I]         = Keyboard.isKeyDown(Keyboard.KEY_I);
        keysCurr[K_J]         = Keyboard.isKeyDown(Keyboard.KEY_J);
        keysCurr[K_K]         = Keyboard.isKeyDown(Keyboard.KEY_K);
        keysCurr[K_L]         = Keyboard.isKeyDown(Keyboard.KEY_L);
        keysCurr[K_M]         = Keyboard.isKeyDown(Keyboard.KEY_M);
        keysCurr[K_N]         = Keyboard.isKeyDown(Keyboard.KEY_N);
        keysCurr[K_O]         = Keyboard.isKeyDown(Keyboard.KEY_O);
        keysCurr[K_P]         = Keyboard.isKeyDown(Keyboard.KEY_P);
        keysCurr[K_Q]         = Keyboard.isKeyDown(Keyboard.KEY_Q);
        keysCurr[K_R]         = Keyboard.isKeyDown(Keyboard.KEY_R);
        keysCurr[K_S]         = Keyboard.isKeyDown(Keyboard.KEY_S);
        keysCurr[K_T]         = Keyboard.isKeyDown(Keyboard.KEY_T);
        keysCurr[K_U]         = Keyboard.isKeyDown(Keyboard.KEY_U);
        keysCurr[K_V]         = Keyboard.isKeyDown(Keyboard.KEY_V);
        keysCurr[K_W]         = Keyboard.isKeyDown(Keyboard.KEY_W);
        keysCurr[K_X]         = Keyboard.isKeyDown(Keyboard.KEY_X);
        keysCurr[K_Y]         = Keyboard.isKeyDown(Keyboard.KEY_Y);
        keysCurr[K_Z]         = Keyboard.isKeyDown(Keyboard.KEY_Z);
        keysCurr[K_0]         = Keyboard.isKeyDown(Keyboard.KEY_0);
        keysCurr[K_1]         = Keyboard.isKeyDown(Keyboard.KEY_1);
        keysCurr[K_2]         = Keyboard.isKeyDown(Keyboard.KEY_2);
        keysCurr[K_3]         = Keyboard.isKeyDown(Keyboard.KEY_3);
        keysCurr[K_4]         = Keyboard.isKeyDown(Keyboard.KEY_4);
        keysCurr[K_5]         = Keyboard.isKeyDown(Keyboard.KEY_5);
        keysCurr[K_6]         = Keyboard.isKeyDown(Keyboard.KEY_6);
        keysCurr[K_7]         = Keyboard.isKeyDown(Keyboard.KEY_7);
        keysCurr[K_8]         = Keyboard.isKeyDown(Keyboard.KEY_8);
        keysCurr[K_9]         = Keyboard.isKeyDown(Keyboard.KEY_9);
    }
    
    public static boolean isKeyDown(int keyFlag)
    {
        return keysCurr[keyFlag];
    }
    
    public static boolean isKeyPressed(int keyFlag)
    {
        return keysCurr[keyFlag] && !keysPrev[keyFlag];
    }
    
    public static char getKeyChar(int key)
    {
    	return keyCharacters.get(key);
    }
    
    public static char getTyped()
    {
    	/* Search starts from A and ends to 9 ignoring spaces and enters */
    	for(int i = 0; i < MAX_KEYS; i++)
    	{
    		if(isKeyPressed(i) && i != K_ESCAPE && i != K_ENTER)
    		{
    			return keyCharacters.get(i);
    		}
    	}
    	
    	return '\0';
    }
}
