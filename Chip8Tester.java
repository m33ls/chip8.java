public class Chip8Tester
{
	public static void assertEq(int actual, int expected)
	{
		if (actual == expected) {
			System.out.println("[SUCCESS]");
		} 
		else {
			System.out.println("[FAILED] " + actual + " not equal to " + expected);
		}
	}

	public static void main(String[] args)
	{
		Chip8 myChip8 = new Chip8();
		myChip8.loadFontset();
		assertEq(1,2);
	}
}
