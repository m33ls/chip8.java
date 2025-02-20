import javax.swing.*;
import java.awt.*;

public class Chip8Runner
{
	public static void main(String[] args)
	{
		Chip8 myChip8 = new Chip8();

		myChip8.setLogging(true);
		myChip8.loadFontset();
		myChip8.loadProgram(args[0]);
		myChip8.initializeDisplay(myChip8.gfx);

		long taskTime;
		long sleepTime = 1000/500; // run at 1000 / n instructions per second
					   // for example: 1000 / 500 => 500 ips
		while (true) {
			taskTime = System.currentTimeMillis();
			myChip8.emulateCycle();
			taskTime = System.currentTimeMillis() - taskTime;

			if (sleepTime-taskTime > 0 ) {
    				try {
					Thread.sleep(sleepTime-taskTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
  			}

			if (myChip8.draw_flag) {
				myChip8.draw();
			}
		}
	}
}
