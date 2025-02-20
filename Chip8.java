import java.util.Arrays;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.*;

/*
 *       __    __       ______ 
 * .----|  |--|__.-----|  __  |
 * |  __|     |  |  _  |  __  |
 * |____|__|__|__|   __|______|
 *               |__|
 * 
 * Emulate Chip8 programs in Java
 *
 * @author m33ls
 * @version 1.0.0
 */
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
	int[] v = new int[16];
	int[] key = new int[16];
	int[] memory = new int[4096];		
	int[][] gfx = new int[32][64]; // gfx[rows][columns] or gfx[x][y]
	int[] stack = new int[16];     // (opposite of rust: gfx[y][x])
	boolean draw_flag;

	Pixels screen;

	boolean logging;

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
	public void loadProgram(String path) {
		try { 
			byte[] bytearray = Files.readAllBytes(Paths.get(path));
			for (int i = 0; i < bytearray.length; i++) {

				memory[i + 512] = bytearray[i];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Set logging option
	 *
	 * @param boolean true or false
	 */
	public void setLogging(boolean bool) {
		logging = bool;
	}

	/*
	 * Load program into memory at 0x200 (512)
	 *
	 * @param Byte array to load (as int[])
	 */
	public void loadProgram(int[] bytearray) {
		for (int i = 0; i < bytearray.length; i++) {
			memory[i + 512] = bytearray[i];
		}
	}

	/*
	 * Initialize display
	 * @param graphics array
	 */
	public void initializeDisplay(int[][] gfx){
        	JFrame f = new JFrame("Chip8");
        	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	screen = new Pixels(gfx);
        	f.add(screen);
        	f.pack();
        	f.setVisible(true);

    	}

	/*
	 * Draw to screen
	 */
	public void draw() {
		screen.drawScreen();
		draw_flag = false;
	}

	private void log(String call) {
		if (logging) {
			System.out.printf("%04x    %04x    %s\n", pc, (opcode & 0xFFFF), call);
		}
	}

	/*
	 * Fetch current opcode
	 * @return opcode
	 */
	private int getOpcode() {
		return (memory[pc] << 8 | (memory[pc + 0x1] & 0x00FF));
	}

	/*
	 * Emulate one CPU cycle of the Chip8 processor
	 * Get opcode, split into the appropriate variables
	 * i.e. x, y, n, kk, and nnn
	 * and then, call the appropriate function
	 */
	public void emulateCycle() {
		opcode = getOpcode();


		int nibble = (opcode & 0xF000) >> 12; // get first byte
		int x = (opcode &  0x0F00) >> 8;      // get byte two
		int y = (opcode & 0x00F0) >> 4;       // get byte three
		int n = opcode & 0x000F;              // get byte four
		int kk = opcode & 0x00FF;             // get last two bytes
		int nnn = opcode & 0x0FFF;            // get last three bytes

		// Debug opcode
		// System.out.printf("Opcode: %#04x\n", opcode);
		// System.out.printf("Nibble: %01x\nX: %01x\nY: %01x\nN: %01x\n", nibble, x, y, n);
		// System.out.printf("KK: %02x\nNNN: %03x\n", kk, nnn);
		
		// Switch statement to match opcode to function
		switch (nibble) {
			case 0x0:
				switch (kk) {
					case 0xE0: op_00e0(); break;
					case 0xEE: op_00ee(); break;
				}
				break;
			case 0x1: op_1nnn(nnn); break;
			case 0x2: op_2nnn(nnn); break;
			case 0x3: op_3xkk(x, kk); break;
			case 0x4: op_4xkk(x, kk); break;
			case 0x5: op_5xy0(x, y); break;
			case 0x6: op_6xkk(x, kk); break;
			case 0x7: op_7xkk(x, kk); break;
			case 0x8:
				switch (n) {
					case 0x0: op_8xy0(x, y); break;
               				case 0x1: op_8xy1(x, y); break;
                			case 0x2: op_8xy2(x, y); break;
                			case 0x3: op_8xy3(x, y); break;
                			case 0x4: op_8xy4(x, y); break;
                			case 0x5: op_8xy5(x, y); break;
                			case 0x6: op_8xy6(x, y); break;
                			case 0x7: op_8xy7(x, y); break;
                			case 0xE: op_8xye(x, y); break;
				}
				break;
			case 0x9: op_9xy0(x, y); break;
			case 0xA: op_annn(nnn); break;
			case 0xB: op_bnnn(nnn); break;
			case 0xC: op_cxkk(x, kk); break;
			case 0xD: op_dxyn(x, y, n); break;
			case 0xE:
				switch (n) {
					case 0xE: op_ex9e(x); break;
					case 0x1: op_exa1(x); break;
				}
				break;
			case 0xF:
				switch (n) {
					case 0x7: op_fx07(x); break;
					case 0xa: op_fx0a(x); break;
					case 0x5: 
						switch (y) {
							case 0x1: op_fx15(x); break;
							case 0x5: op_fx55(x); break;
							case 0x6: op_fx65(x); break;
						}
						break;
					case 0x8: op_fx18(x); break;
					case 0xe: op_fx1e(x); break;
					case 0x9: op_fx29(x); break;
					case 0x3: op_fx33(x); break;
				}
				break;
		}

	}

	/*
	 * CLS
	 * Clear the display.
	 */
	private void op_00e0() {
		for (int i = 0; i < gfx.length; i++) {
			Arrays.fill(gfx[i], 0);
		}
		draw_flag = true;
		pc += 2;
		log("CLS");
	}
	/*
	 * RET
	 * Return from a subroutine.
	 * The interpreter sets the program counter to the address at the top of the 
	 * stack, then subtracts 1 from the stack pointer.
	 */
	private void op_00ee() {
		sp -= 1;
		pc = stack[sp];
		log("RET");
	}
	/*
	 * JP addr
	 * Jump to the location nnn.
	 * The interpreter sets the program counter to nnn.
	 * @param nnn
	 */
	private void op_1nnn(int nnn) {
		pc = nnn;
		log("JP addr");
	}
	/*
	 * CALL addr
	 * Call subroutine at nnn.
	 * The interpreter increments the stack pointer, then puts the current PC on 
	 * the top of the stack. The PC is then set to nnn.
	 * @param nnn
	 */
	private void op_2nnn(int nnn) {
		stack[sp] = pc + 2;
		sp += 1;
		pc = nnn;
		log("CALL addr");
	}
	/*
	 * SE Vx, byte
	 * Skip next instruction if Vx = kk.
	 * The interpreter compares register Vx to kk, and if they are equal, 
	 * increments the program counter by 2.
	 * @param x
	 * @ param kk
	 */
	private void op_3xkk(int x, int kk) {
		if (v[x] == kk) {
			pc += 4;
		} else {
			pc += 2;
		}
		log("SE Vx, byte");
	}
	/*
	 * SNE Vx, byte
	 * Skip next instruction if Vx != kk.
	 * The interpreter compares register Vx to kk, and if they are not 
	 * equal, increments the program counter by 2.
	 * @param x
	 * @param kk
	 */
	private void op_4xkk(int x, int kk) {
		if (v[x] != kk) {
			pc += 4;
		} else {
			pc += 2;
		}
		log("SNE Vx, byte");
	}
	/*
	 * SE Vx, Vy
	 * Skip next instruction if Vx = Vy.
	 * The interpreter compares register Vx to register Vy, and if they are 
	 * equal, increments the program counter by 2.
	 * @param x
	 * @param y
	 */
	private void op_5xy0(int x, int y) {
		if (v[x] == v[y]) {
			pc += 4;
		} else {
			pc += 2;
		}
		log("SE Vx, Vy");
	}
	/*
	 * LD Vx, byte
	 * Set Vx = kk.
	 * The interpreter puts the value kk into register Vx.
	 * @param x
	 * @param kk
	 */
	private void op_6xkk(int x, int kk) {
		v[x] = kk;
		pc += 2;
		log("LD Vx, byte");
	}
	/*
	 * ADD Vx, byte
	 * Set Vx = Vx + kk.
	 * Adds the value kk to the value of register Vx, then stores the 
	 * result in Vx.
	 * @param x
	 * @param kk
	 */
	private void op_7xkk(int x, int kk) {
		v[x] = v[x] + kk;
		pc += 2;
		log("ADD Vx, byte");
	}
	/*
	 * LD Vx, Vy
	 * Set Vx = Vy.
	 * Stores the value of register Vy in register Vx.
	 * @param x
	 * @param y
	 */
	private void op_8xy0(int x, int y) {
		v[x] = v[y];
		pc += 2;
		log("LD Vx, Vy");
	}
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
	private void op_8xy1(int x, int y) {
		v[x] = v[x] | v[y];
		pc += 2;
		log("OR Vx, Vy");
	}
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
	private void op_8xy2(int x, int y) {
		v[x] = v[x] & v[y];
		pc += 2;
		log("AND Vx, Vy");
	}
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
	private void op_8xy3(int x, int y) {
		v[x] = v[x] ^ v[y];
		pc += 2;
		log("XOR Vx, Vy");
	}
	/*
	 * ADD Vx, Vy
	 * Set Vx = Vx + Vy, set VF = carry
	 * The values of Vx and Vy are added together. If the result is greater than 8
	 * bits (i.e. > 255,) VF is set to 1, otherwise 0. Only the lowest 8 bits of
	 * the result are kept, and stored in Vx.
	 * @param x
	 * @param y
	 */
	private void op_8xy4(int x, int y) {
		int result = v[x] + v[y];
		v[x] = result;

		if (result > 0xFF) {
			v[0xF] = 1;
		} else {
			v[0xF] = 0;
		}
		pc += 2;
		log("ADD Vx, Vy");
	}
	/*
	 * SUB Vx, Vy
	 * Set Vx = Vx - Vy, set VF = NOT borrow.
	 * If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx,
	 * and the result is stored in Vx.
	 * @param x
	 * @param y
	 */
	private void op_8xy5(int x, int y) {
		if (v[x] > v[y]) {
			v[0xF] = 1;
		} else {
			v[0xF] = 0;
		}
		v[x] -= v[y];
		pc += 2;
		log("SUB Vx, Vy");
	}
	/*
	 * SHR Vx {, Vy}
	 * Set Vx = Vx SHR 1.
	 * If the least significant bit of Vx is 1, then VF is set to 1, otherwise 0. 
	 * Then Vx is divided by 2
	 * @param x
	 * @param y
	 */
	private void op_8xy6(int x, int y) {
		v[0xF] = v[x] & 1;
		v[x] >>= 1;
		pc += 2;
		log("SHR Vx {, Vy}");
	}
	/*
	 * SUBN Vx, Vy
	 * Set Vx = Vy - Vx, set VF = NOT borrow
	 * If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, 
	 * and the result is stored in Vx.
	 * @param x
	 * @param y
	 */
	private void op_8xy7(int x, int y) {
		if (v[y] > v[x]) {
			v[0xF] = 1;
		} else {
			v[0xF] = 0;
		}
		v[x] = v[y] - v[x];
		pc += 2;
		log("SUBN Vx, Vy");
	}
	/*
	 * SHL Vx {, Vy}
	 * Set Vx = Vx SHL 1.
	 * If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0.
	 * Then Vx is multiplied by 2.
	 * @param x
	 * @param y
	 */
	private void op_8xye(int x, int y) {
		v[0xF] = (v[x] & 0x80) >> 7;
		v[x] <<= 1;
		pc += 2;
		log("SHL VX {, Vy}");
	}
	/*
	 * SNE Vx, Vy
	 * Skip next instruction if Vx != Vy.
	 * The values of Vx and Vy are compared, and if they are not equal, the
	 * program counter is increased by 2.
	 * @param x
	 * @param y
	 */
	private void op_9xy0(int x, int y) {
		if (v[x] != v[y] >> 4) {
			pc += 4;
		} else {
			pc += 2;
		}
		log("SNE Vx, Vy");
	}
	/*
	 * LD I, addr
	 * Set I = nnn.
	 * The value of register I is set to nnn.
	 * @param nnn
	 */
	private void op_annn(int nnn) {
		i = nnn;
		pc += 2;
		log("LD I, addr");
	}
	/*
	 * JP V0, addr
	 * Jump to location nnn + V0
	 * The program counter is set to nnn plus the value of V0.
	 * @param nnn
	 */
	private void op_bnnn(int nnn) {
		pc = nnn + v[0];
		log("JP V0, addr");
	}
	/*
	 * RND Vx, byte
	 * Set Vx = random byte AND kk.
	 * The interpreter generates a random number from 0 to 255, which is then
	 * ANDed with the value kk. The results are stored in Vx.
	 * @param x
	 * @param kk
	 */
	private void op_cxkk(int x, int kk) {
		Random random = new Random();
		v[x] = random.nextInt(256) & kk;
		pc += 2;
		log("RND Vx, byte");
	}
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
	private void op_dxyn(int x, int y, int n) {
		v[0xF] = 0;

		for (int bytevar = 0; bytevar < n; bytevar++) {
			int dxyn_y = (v[y] + bytevar) % 32;
			for (int bit = 0; bit < 8; bit++) {
				int dxyn_x = (v[x] + bit) % 64;
				int color = (memory[(i + bytevar)] >> (7 - bit)) & 1;
				v[0xF] |= color & gfx[dxyn_y][dxyn_x];
				gfx[dxyn_y][dxyn_x] ^= color;
			}
		}
		draw_flag = true;
		pc += 2;
		log("DRW Vx, Vy, nibble");
	}
	/*
	 * SKP Vx
	 * Skip next instruction if key with the value of Vx is pressed.
	 * Checks the keyboard, and if the key corresponding to the value of Vx
	 * is currently in the down position, PC is increased by 2.
	 * @param x
	 */
	private void op_ex9e(int x) {
		if (key[v[x]] == 1) {
			pc += 4;
		} else {
			pc += 2;
		}
		log("SKP Vx");
	}
	/*
	 * SKNP Vx
	 * Skip next instruction if key with the value of Vx is not pressed.
	 * Checks the keyboard, and if the key corresponding to the value of Vx is
	 * currently in the up position, PC is increased by 2.
	 * @param x
	 */
	private void op_exa1(int x) {
		if (key[v[x]] != 1) {
			pc += 4;
		} else {
			pc += 2;
		}
		log("SKNP Vx");
	}
	/*
	 * LD Vx, DT
	 * Set Vx = delay timer value.
	 * The value of DT is placed into Vx.
	 * @param x
	 */
	private void op_fx07(int x) {
		v[x] = delay_timer;
		pc += 2;
		log("LD Vx, DT");
	}
	/*
	 * LD Vx, K
	 * Wait for a key press, store the value of the key in Vx.
	 * All execution stops until a key is pressed, then the value of that key
	 * is stored in Vx.
	 * @param x
	 */
	private void op_fx0a(int x) {
		boolean empty = true;
		for (int i = 0; i < 15; i++) {
			if (key[i] != 0) {
				v[x] = i;
				empty = false;
			}
		}

		if (empty != true) {
			pc += 2;
		}
		log("LD Vx, K");
	}
	/* 
	 * LD DT, Vx
	 * Set delay timer = Vx.
	 * DT is set equal to the value of Vx.
	 * @param x
	 */
	private void op_fx15(int x) {
		delay_timer = v[x];
		pc += 2;
		log("LD DT, Vx");
	}
	/*
	 * LD ST, Vx
	 * Set sound timer = Vx.
	 * ST is set equal to the value of Vx.
	 * @param x
	 */
	private void op_fx18(int x) {
		sound_timer = v[x];
		pc += 2;
		log("LD ST, Vx");
	}
	/*
	 * ADD I, Vx
	 * Set I = I + Vx.
	 * The values of I and Vx are added, and the results are stored in I.
	 * @param x
	 */
	private void op_fx1e(int x) {
		i += v[x];
		pc += 2;
		log("ADD I, Vx");
	}
	/*
	 * LD F, Vx
	 * Set I = location of sprite for digit Vx.
	 * The value of I is set to the location of the hexadecimal sprite corresponding
	 * to the value of Vx.
	 * @param x
	 */
	private void op_fx29(int x) {
		i = v[x] * 5;
		pc += 2;
		log("LD F, Vx");
	}
	/*
	 * LD B, Vx
	 * Store BCD representation of Vx in memory locations I, I+1, and I+2
	 * The interpreter takes the decimal value of Vx, and places the hundreds
	 * digit in memory at location in I, the tens digit at location I+1, and the 
	 * ones digit at location I+2.
	 * @param x
	 */
	private void op_fx33(int x) {
		memory[i] = v[x] / 100;
		memory[(i + 1)] = (v[x] / 10) % 10;
		memory[(i + 2)] = (v[x] % 100) % 100;
		pc += 2;
		log("LD B, Vx");
	}
	/*
	 * LD [I], Vx
	 * Store registers V0 through Vx in memory starting at location I.
	 * The interpreter copies the values of registers V0 through Vx into memory, 
	 * starting at the address in I
	 * @param x
	 */
	private void op_fx55(int x) {
		for (int i = 0; i < x; i++) {
			memory[this.i + i] = v[i];
		}
		pc += 2;
		log("LD [I], Vx");
	}
	/*
	 * LD Vx, [I]
	 * Read registers V0 through Vx from memory starting at location I.
	 * The interpreter reads values from memory starting at location I into
	 * registers V0 through Vx.
	 * @param x
	 */
	private void op_fx65(int x) {
		for (int i = 0; i < x; i++) {
			v[i] = memory[(this.i + i)];
		}
		pc += 2;
		log("LD Vx, [I]");
	}
}
