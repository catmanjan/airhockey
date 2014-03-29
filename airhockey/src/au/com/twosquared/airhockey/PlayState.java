package au.com.twosquared.airhockey;

import org.flixel.FlxG;
import org.flixel.FlxPoint;
import org.flxbox2d.B2FlxB;
import org.flxbox2d.B2FlxState;
import org.flxbox2d.collision.shapes.B2FlxBox;
import org.flxbox2d.collision.shapes.B2FlxCircle;
import org.flxbox2d.collision.shapes.B2FlxPolygon;
import org.flxbox2d.collision.shapes.B2FlxShape;
import org.flxbox2d.dynamics.joints.B2FlxMouseJoint;
import org.flxbox2d.dynamics.joints.B2FlxMultiTouchJoint;
import org.flxbox2d.events.IB2FlxListener;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

public class PlayState extends B2FlxState {
	B2FlxPolygon board;
	B2FlxCircle puck;
	B2FlxCircle redHandle;
	B2FlxCircle blueHandle;
	B2FlxBox redGoalBorder;
	B2FlxBox blueGoalBorder;
	B2FlxBox redGoal;
	B2FlxBox blueGoal;

	private final float puckSize = 220;
	private final float handleSize = 280;

	private final float boardWidth = 2432;
	private final float boardHeight = 1536;

	private final short BOARD = 0x0001;
	private final short BORDER = 0x0002;
	private final short PUCK = 0x0004;
	private final short HANDLE = 0x0008;
	private final short BLUE_GOAL = 0x0016;
	private final short RED_GOAL = 0x0032;

	private final short BOARD_MASK = (short) (HANDLE | BOARD | BORDER | PUCK);
	private final short BORDER_MASK = HANDLE;
	private final short PUCK_MASK = (short) (HANDLE | BOARD | BLUE_GOAL | BLUE_GOAL);
	private final short HANDLE_MASK = (short) (HANDLE | BOARD | BORDER | PUCK);
	private final short GOAL_MASK = PUCK;

	float[][][] boardVertices = {
			{ { 0, 0 }, { 0, 503 }, { 54, 503 }, { 54, 190 } },
			{ { 0, 0 }, { 54, 190 }, { 134, 110 } },
			{ { 0, 0 }, { 134, 110 }, { boardWidth - 134, 110 },
					{ boardWidth, 0 } },
			{ { boardWidth, 0 }, { boardWidth - 54, 190 },
					{ boardWidth - 134, 110 } },
			{ { boardWidth, 0 }, { boardWidth, 503 }, { boardWidth - 54, 503 },
					{ boardWidth - 54, 190 } },
			//
			{ { 0, 1536 }, { 0, 1033 }, { 54, 1033 }, { 54, 1346 } },
			{ { 0, 1536 }, { 54, 1346 }, { 134, 1426 } },
			{ { 0, 1536 }, { 134, 1426 }, { boardWidth - 134, 1426 },
					{ boardWidth, 1536 } },
			{ { boardWidth, 1536 }, { boardWidth - 54, 1346 },
					{ boardWidth - 134, 1426 } },
			{ { boardWidth, 1536 }, { boardWidth, 1033 },
					{ boardWidth - 54, 1033 }, { boardWidth - 54, 1346 } } };

	@Override
	public void create() {
		super.create();
		B2FlxB.setGravity(0, 0);
		B2FlxMouseJoint.maxForce = 10000000f;
		// FlxG.visualDebug = true;
		// B2FlxDebug.drawBodies = true;

		float puckX = (FlxG.width - puckSize) / 2;
		float puckY = (FlxG.height - puckSize) / 2;
		float blueX = FlxG.width - handleSize;
		float handleY = (FlxG.height - handleSize) / 2;

		board = new B2FlxPolygon(0, 0, boardVertices);
		board.setCategoryBits(BOARD);
		board.setMaskBits(BOARD_MASK);
		board.loadGraphic("pack:board2");
		board.offset = new FlxPoint(-boardWidth / 2, -boardHeight / 2);
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

		blueGoalBorder = box(-1, 0, 1, FlxG.height, BORDER, BORDER_MASK);
		redGoalBorder = box(FlxG.width, 0, 1, FlxG.height, BORDER, BORDER_MASK);

		blueGoal = box(FlxG.width + handleSize, 0, 1, FlxG.height, RED_GOAL,
				GOAL_MASK);
		redGoal = box(-handleSize, 0, 1, FlxG.height, BLUE_GOAL, GOAL_MASK);

		B2FlxB.contact.onBeginContact(PUCK, BLUE_GOAL, hitBlueGoal);
		B2FlxB.contact.onBeginContact(PUCK, RED_GOAL, hitRedGoal);

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

	private void reset() {
		float puckX = FlxG.width / 2;
		float puckY = FlxG.height / 2;
		float blueX = FlxG.width - handleSize / 2;
		float handleY = FlxG.height / 2;

		puck.reset(puckX, puckY);
		blueHandle.reset(blueX, handleY);
		redHandle.reset(0, handleY);
	}

	IB2FlxListener hitBlueGoal = new IB2FlxListener() {
		@Override
		public void onContact(B2FlxShape sprite1, B2FlxShape sprite2,
				Contact contact, Manifold oldManifold, ContactImpulse impulse) {
			reset();
		}
	};

	IB2FlxListener hitRedGoal = new IB2FlxListener() {
		@Override
		public void onContact(B2FlxShape sprite1, B2FlxShape sprite2,
				Contact contact, Manifold oldManifold, ContactImpulse impulse) {
			reset();
		}
	};
}