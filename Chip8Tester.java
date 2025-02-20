/*
 * Test the Chip8 class
 *
 * @author m33ls
 * @version 1.0.0
 */

public class Chip8Tester
{
	// If quiet mode enabled, only print on fail
	private static final boolean QUIET_MODE = true;

	public static void assertEq(String testName, int actual, int expected)
	{
		if (actual == expected) {
			if (QUIET_MODE == false) {
				System.out.println("[SUCCESS]    Test." + testName + "    " + actual + " == " + expected);
			}
		} 
		else {
			System.out.println("[FAILED]    Test." + testName + "    " + actual + " != " + expected);
		}
	}

	public static void main(String[] args)
	{
		Chip8 myChip8 = new Chip8();

		// Test loading fontset
		myChip8.loadFontset();
		int[] fontsetTest = {
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
		for (int i = 0; i < fontsetTest.length; i++) {
			assertEq("myChip8.loadFontset()", myChip8.memory[i], fontsetTest[i]);
		}

		// Test loading program from bytes
		int[] loadProgramBytesTest = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
		myChip8.loadProgram(loadProgramBytesTest);
		for (int i = 0; i < loadProgramBytesTest.length; i++) {
			assertEq("myChip8.loadProgram(int[] bytearray)", myChip8.memory[i+512], loadProgramBytesTest[i]);
		}

		// Test loading program from path
		myChip8.loadProgram("ibm-logo.ch8");
		myChip8.emulateCycle();
		myChip8.emulateCycle();
		myChip8.emulateCycle();

	}
}
