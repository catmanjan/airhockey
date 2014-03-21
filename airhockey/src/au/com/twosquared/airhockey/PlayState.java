package au.com.twosquared.airhockey;

import org.flixel.FlxG;
import org.flixel.FlxPoint;
import org.flxbox2d.B2FlxB;
import org.flxbox2d.B2FlxState;
import org.flxbox2d.collision.shapes.B2FlxBox;
import org.flxbox2d.collision.shapes.B2FlxCircle;
import org.flxbox2d.collision.shapes.B2FlxPolygon;
import org.flxbox2d.dynamics.joints.B2FlxMultiTouchJoint;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class PlayState extends B2FlxState {
	B2FlxPolygon board;
	B2FlxCircle puck;
	B2FlxCircle redHandle;
	B2FlxCircle blueHandle;
	B2FlxBox redGoal;
	B2FlxBox blueGoal;

	private final float puckSize = 220;
	private final float handleSize = 280;

	private final short BOARD = 0x0001;
	private final short GOAL = 0x0002;
	private final short PUCK = 0x0004;
	private final short HANDLE = 0x0008;

	private final short BOARD_MASK = (short) (HANDLE | BOARD | GOAL | PUCK);
	private final short GOAL_MASK = HANDLE;
	private final short PUCK_MASK = (short) (HANDLE | BOARD);
	private final short HANDLE_MASK = (short) (HANDLE | BOARD | GOAL | PUCK);

	float[][][] boardVertices = {
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
		//FlxG.visualDebug = true;
		//B2FlxDebug.drawBodies = true;

		float puckX = (FlxG.width - puckSize) / 2;
		float puckY = (FlxG.height - puckSize) / 2;
		float blueX = FlxG.width - handleSize;
		float handleY = (FlxG.height - handleSize) / 2;

		board = new B2FlxPolygon(0, 0, boardVertices);
		board.setCategoryBits(BOARD);
		board.setMaskBits(BOARD_MASK);
		board.loadGraphic("pack:board");
		board.offset = new FlxPoint(-2048 / 2, -1536 / 2);
		board.setType(BodyType.StaticBody);
		board.create();
		add(board);

		puck = circle(puckX, puckY, puckSize / 2, PUCK, PUCK_MASK);
		puck.loadGraphic("pack:puck");
		puck.create();
		add(puck);

		blueHandle = circle(blueX, handleY, handleSize / 2, HANDLE, HANDLE_MASK);
		blueHandle.loadGraphic("pack:blueHandle");
		blueHandle.setLinearDamping(2);
		blueHandle.create();
		add(blueHandle);

		redHandle = circle(0, handleY, handleSize / 2, HANDLE, HANDLE_MASK);
		redHandle.loadGraphic("pack:redHandle");
		redHandle.setLinearDamping(2);
		redHandle.create();
		add(redHandle);

		blueGoal = box(-1, 0, 1, FlxG.height, GOAL, GOAL_MASK);
		redGoal = box(FlxG.width, 0, 1, FlxG.height, GOAL, GOAL_MASK);

		add(new B2FlxMultiTouchJoint(this));
	}

	private B2FlxCircle circle(float x, float y, float radius,
			short categoryBits, short maskBits) {
		B2FlxCircle circle = new B2FlxCircle(x, y, radius);
		circle.setCategoryBits(categoryBits);
		circle.setMaskBits(maskBits);
		circle.setDensity(1);
		circle.setFriction(0);
		circle.setRestitution(0.8f);
		circle.setFixedRotation(true);
		circle.setDraggable(true);

		return circle;
	}

	private B2FlxBox box(float x, float y, float width, float height,
			short categoryBits, short maskBits) {
		B2FlxBox box = new B2FlxBox(x, y, width, height);
		box.setCategoryBits(categoryBits);
		box.setMaskBits(maskBits);
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