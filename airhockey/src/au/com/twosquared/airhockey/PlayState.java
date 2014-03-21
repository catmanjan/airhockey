package au.com.twosquared.airhockey;

import org.flixel.FlxG;
import org.flixel.FlxPoint;
import org.flixel.FlxSprite;
import org.flxbox2d.B2FlxB;
import org.flxbox2d.B2FlxState;
import org.flxbox2d.collision.shapes.B2FlxBox;
import org.flxbox2d.collision.shapes.B2FlxCircle;
import org.flxbox2d.collision.shapes.B2FlxPolygon;
import org.flxbox2d.dynamics.joints.B2FlxMouseJoint;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class PlayState extends B2FlxState {
	FlxSprite board;

	B2FlxCircle puck;
	B2FlxCircle redHandle;
	B2FlxCircle blueHandle;

	float puckDiameter = 220;
	float handleDiameter = 280;

	float[][][] gutterVertices = {
			{ { 0, 0 }, { 0, 503 }, { 54, 503 }, { 54, 190 } },
			{ { 0, 0 }, { 54, 190 }, { 134, 110 } },
			{ { 0, 0 }, { 134, 110 }, { 1914, 110 }, { 2048, 0 } },
			{ { 2048, 0 }, { 1994, 190 }, { 1914, 110 } },
			{ { 2048, 0 }, { 2048, 503 }, { 1994, 503 }, { 1994, 190 } },
			//
			{ { 0, 1536 }, { 0, 1033 }, { 54, 1033 }, { 54, 1346 } },
			{ { 0, 1536 }, { 54, 1346 }, { 134, 1426 } },
			{ { 0, 1536 }, { 134, 1426 }, { 1914, 1426 }, { 2048, 1536 } },
			{ { 2048, 1536 }, { 1994, 1346 }, { 1914, 1426 } },
			{ { 2048, 1536 }, { 2048, 1033 }, { 1994, 1033 }, { 1994, 1346 } } };

	@Override
	public void create() {
		super.create();
		B2FlxB.setGravity(0, 0);
		// FlxG.visualDebug = true;
		// B2FlxDebug.drawBodies = true;

		float puckX = (FlxG.width - puckDiameter) / 2;
		float puckY = (FlxG.height - puckDiameter) / 2;
		float blueHandleX = FlxG.width - handleDiameter;
		float handleY = (FlxG.height - handleDiameter) / 2;

		B2FlxPolygon gutter = new B2FlxPolygon(0, 0, gutterVertices);
		gutter.loadGraphic("pack:board");
		gutter.offset = new FlxPoint(-2048 / 2, -1536 / 2);
		gutter.setType(BodyType.StaticBody);
		gutter.create();
		add(gutter);

		box(-1, 0, 1, FlxG.height);
		box(FlxG.width, 0, 1, FlxG.height);

		puck = circle(puckX, puckY, puckDiameter / 2);
		puck.loadGraphic("pack:puck");

		blueHandle = circle(blueHandleX, handleY, handleDiameter / 2);
		blueHandle.loadGraphic("pack:blueHandle");

		redHandle = circle(0, handleY, handleDiameter / 2);
		redHandle.loadGraphic("pack:redHandle");

		add(new B2FlxMouseJoint());
	}

	private B2FlxCircle circle(float x, float y, float radius) {
		B2FlxCircle circle = new B2FlxCircle(x, y, radius);
		circle.setDensity(1);
		circle.setFriction(0.1f);
		circle.setRestitution(0.9f);
		circle.setFixedRotation(true);
		circle.setDraggable(true);
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