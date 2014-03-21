package au.com.twosquared.airhockey;

import org.flixel.FlxGame;

public class FlixelGame extends FlxGame {
	public FlixelGame() {
		super(2048, 1536, PlayState.class, 1, 60, 60, true);
	}
}
