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
	 * The interpreter sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
	 */
	private void op_00ee() {}
	/*
	 * JP addr
	 * Jump to the location nnn.
	 * The interpreter sets the program counter to nnn.
	 */
	private void op_1nnn(int nnn) {}
	/*
	 * CALL addr
	 * Call subroutine at nnn.
	 * The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
	 */
	private void op_2nnn(int nnn) {}
	/*
	 * SE Vx, byte
	 * Skip next instruction if Vx = kk.
	 * The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2.
	 */
	private void op_3xkk(int x, int kk) {}
	/*
	 * SNE Vx, byte
	 * Skip next instruction if Vx != kk.
	 * The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
	 */
	private void op_4xkk(int x, int kk) {}
	/*
	 * SE Vx, Vy
	 * Skip next instruction if Vx = Vy.
	 * The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
	 */
	private void op_5xy0(int x, int y) {}
	/*
	 * LD Vx, byte
	 * Set Vx = kk.
	 * The interpreter puts the value kk into register Vx.
	 */
	private void op_6xkk(int x, int kk) {}
	/*
	 * ADD Vx, byte
	 * Set Vx = Vx + kk.
	 * Adds the value kk to the value of register Vx, then stores the result in Vx.
	 */
	private void op_7xkk(int x, int kk) {}
	/*
	 * LD Vx, Vy
	 */
	private void op_8xy0(int x, int y) {}
	/*
	 * OR Vx, Vy
	 */
	private void op_8xy1(int x, int y) {}
	/*
	 * AND Vx, Vy
	 */
	private void op_8xy2(int x, int y) {}
	/*
	 * XOR Vx, Vy
	 */
	private void op_8xy3(int x, int y) {}
	/*
	 * ADD Vx, Vy
	 */
	private void op_8xy4(int x, int y) {}
	/*
	 * SUB Vx, Vy
	 */
	private void op_8xy5(int x, int y) {}
	/*
	 * SHR Vx {, Vy}
	 */
	private void op_8xy6(int x, int y) {}
	/*
	 * SUBN Vx, Vy
	 */
	private void op_8xy7(int x, int y) {}
	/*
	 * SHL Vx {, Vy}
	 */
	private void op_8xye(int x, int y) {}
	/*
	 * SNE Vx, Vy
	 */
	private void op_9xy0(int x, int y) {}
	/*
	 * LD I, addr
	 */
	private void op_annn(int nnn) {}
	/*
	 * JP V0, addr
	 */
	private void op_bnnn(int nnn) {}
	/*
	 * RND Vx, byte
	 */
	private void op_cxkk(int x, int kk) {}
	/*
	 * DRW Vx, Vy, nibble
	 */
	private void op_dxyn(int x, int y, int n) {}
	/*
	 * SKP Vx
	 */
	private void op_ex9e(int x) {}
	/*
	 * SKNP Vx
	 */
	private void op_exa1(int x) {}
	/*
	 * LD Vx, DT
	 */
	private void op_fx07(int x) {}
	/*
	 * LD Vx, K
	 */
	private void op_fx0a(int x) {}
	/* 
	 * LD DT, Vx
	 */
	private void op_fx15(int x) {}
	/*
	 * LD ST, Vx
	 */
	private void op_fx18(int x) {}
	/*
	 * ADD I, Vx
	 */
	private void op_fx1e(int x) {}
	/*
	 * LD F, Vx
	 */
	private void op_fx29(int x) {}
	/*
	 * LD B, Vx
	 */
	private void op_fx33(int x) {}
	/*
	 * LD [I], Vx
	 */
	private void op_fx55(int x) {}
	/*
	 * LD Vx, [I]
	 */
	private void op_fx65(int x) {}
}
