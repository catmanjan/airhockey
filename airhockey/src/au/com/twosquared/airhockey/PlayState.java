package au.com.twosquared.airhockey;

import org.flixel.FlxG;
import org.flixel.FlxSprite;
import org.flxbox2d.B2FlxB;
import org.flxbox2d.B2FlxState;
import org.flxbox2d.collision.shapes.B2FlxBox;
import org.flxbox2d.collision.shapes.B2FlxCircle;
import org.flxbox2d.dynamics.joints.B2FlxMouseJoint;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class PlayState extends B2FlxState {
	FlxSprite board;

	B2FlxCircle puck;
	B2FlxCircle redHandle;
	B2FlxCircle blueHandle;

	@Override
	public void create() {
		super.create();

		B2FlxB.setGravity(0, 0);
		// FlxG.visualDebug = true;

		add(new B2FlxMouseJoint());

		board = new FlxSprite().loadGraphic("pack:board");
		add(board);

		box(0, 0, 128, 16);
		box(256, 0, 128, 16);
		box(0, -16, FlxG.width, 16);
		box(0, FlxG.height - 16, 128, 16);
		box(256, FlxG.height - 16, 128, 16);
		box(0, FlxG.height, FlxG.width, 16);
		box(0, 0, 30, FlxG.height);
		box(FlxG.width - 30, 0, 28, FlxG.height);

		float puckR = 28;
		float puckX = (FlxG.width - puckR * 2) / 2;
		float puckY = (FlxG.height - puckR * 2) / 2;

		puck = circle(puckX, puckY, puckR);
		puck.loadGraphic("pack:puck");

		float handleR = 42;
		float handleX = (FlxG.width - handleR * 2) / 2;
		float blueHandleY = FlxG.height - handleR * 2;

		blueHandle = circle(handleX, blueHandleY, handleR);
		blueHandle.loadGraphic("pack:blueHandle");
		blueHandle.setDraggable(true);

		redHandle = circle(handleX, 0, handleR);
		redHandle.loadGraphic("pack:redHandle");
		redHandle.setDraggable(true);
	}

	private B2FlxCircle circle(float x, float y, float radius) {
		B2FlxCircle circle = new B2FlxCircle(x, y, radius);
		circle.setDensity(1);
		circle.setFriction(0.5f);
		circle.setRestitution(0.75f);
		circle.setFixedRotation(true);
		circle.create();
		add(circle);

		return circle;
	}

	private B2FlxBox box(float x, float y, float width, float height) {
		B2FlxBox box = new B2FlxBox(x, y, width, height);
		box.setType(BodyType.StaticBody);
		box.create();
		box.visible = false;
		add(box);

		return box;
	}

	@Override
	public void update() {
		super.update();
	}
}