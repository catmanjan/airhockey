package au.com.twosquared.airhockey;

import org.flixel.FlxDesktopApplication;

public class Main {
	public static void main(String[] args) {
		new FlxDesktopApplication(new FlixelGame(), "air hockey", 512, 384,
				false);
	}
}
