/*
 * @author m33ls
 * @version 1.0.0
 */

import java.util.Arrays;

public class Chip8
{
	/*
	 * Establish variables to emulate memory, stack, etc.
	 * without allocating memory
	 */
	int opcode; // why are there no u8s, u16s in java
	int i;      // everything is an integer now wtf
	int pc;     // surely this will not cause future bugs . . .
	int sp;
	int delay_timer;
	int sound_timer;
	boolean draw_flag;
	int[] v = new int[16];
	int[] key = new int[16];
	int[] memory = new int[4096];		
	int[][] gfx = new int[32][64]; // gfx[rows][columns] or gfx[x][y]
	int[] stack = new int[16];     // (opposite of rust: gfx[y][x])

	/*
	 * Create a new Chip8 instance with default values
	 *
	 * Program counter (pc) should start at 0x200,
	 * Everything else should be 0
	 */
	public Chip8()
	{
		opcode = 0;
		Arrays.fill(memory, 0);
		Arrays.fill(v, 0);
		i = 0;
		pc = 0x200;
		delay_timer = 0;
		sound_timer = 0;
		Arrays.fill(stack, 0);
		sp = 0;
		Arrays.fill(key, 0);
		draw_flag = false;
		
		// iterate over rows and fill
		for (int i = 0; i < gfx.length; i++) {
			Arrays.fill(gfx[i], 0);
		}
	}

	/*
	 * Load fontset to memory
	 *
	 * Chip8 Fonts
         * 
         * Hex Bin   Description
         *                                                                             
         * F0  1111  Each hexadecimal value represents a row in binary,
         * 90  1001  each character is 5 rows tall, and 4 columns wide,
         * 90  1001  or simply 5 bytes long                
         * 90  1001                                        
         * F0  1111  [fig. 1] Character 0 in binary 
         *                                                                  
         * F0  1111  using an XOR, 1s are drawn, and 0s are not,
         * 90  1  1  leaving this                      
         * 90  1  1                                    
         * 90  1  1
	 * F0  1111  [fig. 2] Character 0 with 0s removed
	 */
	public void loadFontset() 
	{
		int[] fontset = {
			0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            		0x20, 0x60, 0x20, 0x20, 0x70, // 1
            		0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            		0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            		0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            		0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            		0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            		0xF0, 0x10, 0x20, 0x40, 0x40, // 7
           		0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            		0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            		0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            		0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            		0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            		0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            		0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            		0xF0, 0x80, 0xF0, 0x80, 0x80  // F
		};
		for (int i = 0; i < 80; i++) {
			memory[i] = fontset[i];
		}
	}

	/*
	 * Load program into memory at 0x200 (512)
	 * 
	 * @param Path to rom
	 */
	private void loadProgram(String path) {}

	/*
	 * Load program into memory at 0x200 (512)
	 *
	 * @param Byte array to load (as int[])
	 */
	private void loadProgram(int[] bytes) {}

	public void draw() {}

	private void log() {}

	public void emulateCycle() {}

	/*
	 * CLS
	 * Clear the display.
	 */
	private void op_00e0() {}
	/*
	 * RET
	 * Return from a subroutine.
	 * The interpreter sets the program counter to the address at the top of the 
	 * stack, then subtracts 1 from the stack pointer.
	 */
	private void op_00ee() {}
	/*
	 * JP addr
	 * Jump to the location nnn.
	 * The interpreter sets the program counter to nnn.
	 * @param nnn
	 */
	private void op_1nnn(int nnn) {}
	/*
	 * CALL addr
	 * Call subroutine at nnn.
	 * The interpreter increments the stack pointer, then puts the current PC on 
	 * the top of the stack. The PC is then set to nnn.
	 * @param nnn
	 */
	private void op_2nnn(int nnn) {}
	/*
	 * SE Vx, byte
	 * Skip next instruction if Vx = kk.
	 * The interpreter compares register Vx to kk, and if they are equal, 
	 * increments the program counter by 2.
	 * @param x
	 * @ param kk
	 */
	private void op_3xkk(int x, int kk) {}
	/*
	 * SNE Vx, byte
	 * Skip next instruction if Vx != kk.
	 * The interpreter compares register Vx to kk, and if they are not 
	 * equal, increments the program counter by 2.
	 * @param x
	 * @param kk
	 */
	private void op_4xkk(int x, int kk) {}
	/*
	 * SE Vx, Vy
	 * Skip next instruction if Vx = Vy.
	 * The interpreter compares register Vx to register Vy, and if they are 
	 * equal, increments the program counter by 2.
	 * @param x
	 * @param y
	 */
	private void op_5xy0(int x, int y) {}
	/*
	 * LD Vx, byte
	 * Set Vx = kk.
	 * The interpreter puts the value kk into register Vx.
	 * @param x
	 * @param kk
	 */
	private void op_6xkk(int x, int kk) {}
	/*
	 * ADD Vx, byte
	 * Set Vx = Vx + kk.
	 * Adds the value kk to the value of register Vx, then stores the 
	 * result in Vx.
	 * @param x
	 * @param kk
	 */
	private void op_7xkk(int x, int kk) {}
	/*
	 * LD Vx, Vy
	 * Set Vx = Vy.
	 * Stores the value of register Vy in register Vx.
	 * @param x
	 * @param y
	 */
	private void op_8xy0(int x, int y) {}
	/*
	 * OR Vx, Vy
	 * Set Vx = Vx OR Vy.
	 * Performs a bitwise OR on the values of Vx and Vy, then stores the result 
	 * in Vx. A bitwise OR compares the corresponding bits from two values, and if
	 * either bit is 1, then the same bit in the result is also 1. Otherwise it is 
	 * 0.
	 * @param x
	 * @param y
	 */
	private void op_8xy1(int x, int y) {}
	/*
	 * AND Vx, Vy
	 * Set Vx = Vx AND Vy.
	 * Performs a bitwise AND on the values of Vx and Vy, then stores the result
	 * in Vx. A bitwise AND compares the corresponding bits from two values, and
	 * if both bits are 1, then the same bit in the result is also 1. Otherwise,
	 * it is 0.
	 * @param x
	 * @param y
	 */
	private void op_8xy2(int x, int y) {}
	/*
	 * XOR Vx, Vy
	 * Set Vx = Vx XOR Vy.
	 * Performs a bitwise exclusive OR on the values of Vx and Vy, then stores
	 * the result in Vx. An exclusive OR compares the corresponding bits from two
	 * values, and if the bits are not both the same, then the corresponding bit
	 * in the result, then the corresponding bit in the result is set to 1. 
	 * Otherwise, it is 0.
	 * @param x
	 * @param y
	 */
	private void op_8xy3(int x, int y) {}
	/*
	 * ADD Vx, Vy
	 * Set Vx = Vx + Vy, set VF = carry
	 * The values of Vx and Vy are added together. If the result is greater than 8
	 * bits (i.e. > 255,) VF is set to 1, otherwise 0. Only the lowest 8 bits of
	 * the result are kept, and stored in Vx.
	 * @param x
	 * @param y
	 */
	private void op_8xy4(int x, int y) {}
	/*
	 * SUB Vx, Vy
	 * Set Vx = Vx - Vy, set VF = NOT borrow.
	 * If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx,
	 * and the result is stored in Vx.
	 * @param x
	 * @param y
	 */
	private void op_8xy5(int x, int y) {}
	/*
	 * SHR Vx {, Vy}
	 * Set Vx = Vx SHR 1.
	 * If the least significant bit of Vx is 1, then VF is set to 1, otherwise 0. 
	 * Then Vx is divided by 2
	 * @param x
	 * @param y
	 */
	private void op_8xy6(int x, int y) {}
	/*
	 * SUBN Vx, Vy
	 * Set Vx = Vy - Vx, set VF = NOT borrow
	 * If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, 
	 * and the result is stored in Vx.
	 * @param x
	 * @param y
	 */
	private void op_8xy7(int x, int y) {}
	/*
	 * SHL Vx {, Vy}
	 * Set Vx = Vx SHL 1.
	 * If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0.
	 * Then Vx is multiplied by 2.
	 * @param x
	 * @param y
	 */
	private void op_8xye(int x, int y) {}
	/*
	 * SNE Vx, Vy
	 * Skip next instruction if Vx != Vy.
	 * The values of Vx and Vy are compared, and if they are not equal, the
	 * program counter is increased by 2.
	 * @param x
	 * @param y
	 */
	private void op_9xy0(int x, int y) {}
	/*
	 * LD I, addr
	 * Set I = nnn.
	 * The value of register I is set to nnn.
	 * @param nnn
	 */
	private void op_annn(int nnn) {}
	/*
	 * JP V0, addr
	 * Jump to location nnn + V0
	 * The program counter is set to nnn plus the value of V0.
	 * @param nnn
	 */
	private void op_bnnn(int nnn) {}
	/*
	 * RND Vx, byte
	 * Set Vx = random byte AND kk.
	 * The interpreter generates a random number from 0 to 255, which is then
	 * ANDed with the value kk. The results are stored in Vx.
	 * @param x
	 * @param kk
	 */
	private void op_cxkk(int x, int kk) {}
	/*
	 * DRW Vx, Vy, nibble
	 * Display n-byte sprite starting at memory location I at (Vx, Vy), 
	 * set VF = collision.
	 *
	 * The interpreter reads n bytes from memory, starting at the address stored 
	 * in I. These bytes are then displayed as sprites on screen at coordinates 
	 * (Vx, Vy). Sprites are XORed onto the existing screen. If this causes any 
	 * pixels to be erased, VF is set to 1, otherwise it is set to 0. If the 
	 * sprite is positioned so part of it is outside the coordinates of the 
	 * display, it wraps around to the opposite side of the screen.
	 * @param x
	 * @param y
	 * @param n
	 */
	private void op_dxyn(int x, int y, int n) {}
	/*
	 * SKP Vx
	 * Skip next instruction if key with the value of Vx is pressed.
	 * Checks the keyboard, and if the key corresponding to the value of Vx
	 * is currently in the down position, PC is increased by 2.
	 * @param x
	 */
	private void op_ex9e(int x) {}
	/*
	 * SKNP Vx
	 * Skip next instruction if key with the value of Vx is not pressed.
	 * Checks the keyboard, and if the key corresponding to the value of Vx is
	 * currently in the up position, PC is increased by 2.
	 * @param x
	 */
	private void op_exa1(int x) {}
	/*
	 * LD Vx, DT
	 * Set Vx = delay timer value.
	 * The value of DT is placed into Vx.
	 * @param x
	 */
	private void op_fx07(int x) {}
	/*
	 * LD Vx, K
	 * Wait for a key press, store the value of the key in Vx.
	 * All execution stops until a key is pressed, then the value of that key
	 * is stored in Vx.
	 * @param x
	 */
	private void op_fx0a(int x) {}
	/* 
	 * LD DT, Vx
	 * Set delay timer = Vx.
	 * DT is set equal to the value of Vx.
	 * @param x
	 */
	private void op_fx15(int x) {}
	/*
	 * LD ST, Vx
	 * Set sound timer = Vx.
	 * ST is set equal to the value of Vx.
	 * @param x
	 */
	private void op_fx18(int x) {}
	/*
	 * ADD I, Vx
	 * Set I = I + Vx.
	 * The values of I and Vx are added, and the results are stored in I.
	 * @param x
	 */
	private void op_fx1e(int x) {}
	/*
	 * LD F, Vx
	 * Set I = location of sprite for digit Vx.
	 * The value of I is set to the location of the hexadecimal sprite corresponding
	 * to the value of Vx.
	 * @param x
	 */
	private void op_fx29(int x) {}
	/*
	 * LD B, Vx
	 * Store BCD representation of Vx in memory locations I, I+1, and I+2
	 * The interpreter takes the decimal value of Vx, and places the hundreds
	 * digit in memory at location in I, the tens digit at location I+1, and the 
	 * ones digit at location I+2.
	 * @param x
	 */
	private void op_fx33(int x) {}
	/*
	 * LD [I], Vx
	 * Store registers V0 through Vx in memory starting at location I.
	 * The interpreter copies the values of registers V0 through Vx into memory, 
	 * starting at the address in I
	 * @param x
	 */
	private void op_fx55(int x) {}
	/*
	 * LD Vx, [I]
	 * Read registers V0 through Vx from memory starting at location I.
	 * The interpreter reads values from memory starting at location I into
	 * registers V0 through Vx.
	 * @param x
	 */
	private void op_fx65(int x) {}
}
