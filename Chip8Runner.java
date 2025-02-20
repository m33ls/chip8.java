import javax.swing.*;
import java.awt.*;

public class Chip8Runner
{
	public static void main(String[] args)
	{
		Chip8 myChip8 = new Chip8();

		myChip8.setLogging(false);
		myChip8.loadFontset();
		myChip8.loadProgram(args[0]);
		myChip8.initializeDisplay(myChip8.gfx);

		while (true) {
			myChip8.emulateCycle();

			if (myChip8.draw_flag) {
				myChip8.draw();
			}
		}
	}
}
