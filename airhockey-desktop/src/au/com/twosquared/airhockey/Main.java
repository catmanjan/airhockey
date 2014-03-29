package au.com.twosquared.airhockey;

import org.flixel.FlxDesktopApplication;

public class Main {
	public static void main(String[] args) {
		new FlxDesktopApplication(new FlixelGame(), "Air Hockey", 1900/2, 1200/2,
				false);
	}
}
