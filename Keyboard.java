import java.awt.*;
import java.awt.event.KeyEvent;

/*
 * Keyboard library for chip8 emulator
 *
 * @author m33ls
 * @version 1.0.0
 *
 * Keybinds
 * +-+-+-+-+    +-+-+-+-+
 * |1|2|3|C|    |1|2|3|4|
 * +-+-+-+-+    +-+-+-+-+
 * |4|5|6|D|    |Q|W|E|R|
 * +-+-+-+-+ => +-+-+-+-+
 * |7|8|9|E|    |A|S|D|F|
 * +-+-+-+-+    +-+-+-+-+
 * |A|0|B|F|    |Z|X|C|V|
 * +-+-+-+-+    +-+-+-+-+
 * COSMAC VIP II  QWERTY
 */
public class Keyboard
{
    boolean[] keys;

    public Keyboard(){
        keys = new boolean[16];
        prepareInput();
    }

    private void prepareInput(){

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                synchronized (Keyboard.class) {
                    switch (ke.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            setKey(true,ke.getKeyCode());
                            break;

                        case KeyEvent.KEY_RELEASED:
                            setKey(false,ke.getKeyCode());
                            break;
                    }
                    return false;
                }
            }
        });
    }

    public boolean[] getKeys(){
        return keys;
    }

    private void setKey(boolean value, int keycode){

        switch(keycode){
            case KeyEvent.VK_1: keys[0x1] = value; break;
            case KeyEvent.VK_2: keys[0x2] = value; break;
            case KeyEvent.VK_3: keys[0x3] = value; break;
            case KeyEvent.VK_4: keys[0xC] = value; break;
            case KeyEvent.VK_Q: keys[0x4] = value; break;
            case KeyEvent.VK_W: keys[0x5] = value; break;
            case KeyEvent.VK_E: keys[0x6] = value; break;
            case KeyEvent.VK_R: keys[0xD] = value; break;
            case KeyEvent.VK_A: keys[0x7] = value; break;
            case KeyEvent.VK_S: keys[0x8] = value; break;
            case KeyEvent.VK_D: keys[0x9] = value; break;
            case KeyEvent.VK_F: keys[0xE] = value; break;
            case KeyEvent.VK_Z: keys[0xA] = value; break;
            case KeyEvent.VK_X: keys[0x0] = value; break;
            case KeyEvent.VK_C: keys[0xB] = value; break;
            case KeyEvent.VK_V: keys[0xF] = value; break;
        }
    }
}


