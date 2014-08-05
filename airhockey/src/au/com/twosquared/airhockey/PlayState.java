package au.com.twosquared.airhockey;

import org.flixel.FlxG;
import org.flixel.FlxPoint;
import org.flixel.FlxU;
import org.flxbox2d.B2FlxB;
import org.flxbox2d.B2FlxState;
import org.flxbox2d.collision.shapes.B2FlxBox;
import org.flxbox2d.collision.shapes.B2FlxCircle;
import org.flxbox2d.collision.shapes.B2FlxPolygon;
import org.flxbox2d.collision.shapes.B2FlxShape;
import org.flxbox2d.dynamics.joints.B2FlxDistanceJoint;
import org.flxbox2d.dynamics.joints.B2FlxMouseJoint;
import org.flxbox2d.dynamics.joints.B2FlxMultiTouchJoint;
import org.flxbox2d.events.IB2FlxListener;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

public class PlayState extends B2FlxState {

	B2FlxBox redGoalBorder;
	B2FlxBox blueGoalBorder;
	B2FlxBox redGoal;
	B2FlxBox blueGoal;
	B2FlxCircle puck;
	B2FlxCircle redHandle;
	B2FlxCircle blueHandle;
	B2FlxCircle ai;
	B2FlxDistanceJoint aiJoint;
	B2FlxPolygon board;

	// For AI, 0 is least aggressive, 1 is most aggressive
	private float aggroModifier;
	// For AI, 0 is closest to own goal, 1 is closest to enemy goal
	private float rangeModifier;

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

		ai = new B2FlxCircle(0, handleY, handleSize / 2);
		ai.setMaskBits((short) 0);
		ai.setType(BodyType.StaticBody);
		ai.create();
		ai.visible = false;
		add(ai);

		aiJoint = new B2FlxDistanceJoint(redHandle, ai);
		aiJoint.setCollideConnected(false);
		aiJoint.setDampingRatio(0.9f);
		aiJoint.setFrequencyHz(1f);
		aiJoint.create();
		aiJoint.visible = false;
		add(aiJoint);

		blueGoalBorder = box(-1, 0, 1, FlxG.height, BORDER, BORDER_MASK);
		redGoalBorder = box(FlxG.width, 0, 1, FlxG.height, BORDER, BORDER_MASK);

		blueGoal = box(FlxG.width + handleSize, 0, 1, FlxG.height, RED_GOAL,
				GOAL_MASK);
		redGoal = box(-handleSize, 0, 1, FlxG.height, BLUE_GOAL, GOAL_MASK);

		B2FlxB.contact.onBeginContact(PUCK, BLUE_GOAL, hitBlueGoal);
		B2FlxB.contact.onBeginContact(PUCK, RED_GOAL, hitRedGoal);

		add(new B2FlxMultiTouchJoint(this));
	}

	@Override
	public void update() {
		super.update();

		updateAI();
	}

	private void updateAI() {
		AIState state = AIState.IDLE;
		float x = redHandle.position.x;
		float y = redHandle.position.y;
		float stageWidth = 76;
		float aggro = stageWidth * (0.4f + aggroModifier * 0.1f);
		float range = stageWidth * (0.25f + rangeModifier * 0.25f);

		if (puck.position.x <= aggro) {
			state = AIState.ATTACKING;

			if (puck.position.y >= 40 || puck.position.y <= 8) {
				state = AIState.CORNER;
			}
		} else if (puck.position.x > aggro) {
			state = AIState.DEFENDING;
		}

		switch (state) {
		case IDLE:
			break;
		case ATTACKING:
			x = puck.position.x - 3;
			y = puck.position.y;

			ai.setPosition(x, y);
			break;
		case CORNER:
			x = puck.position.x - 3;
			y = puck.position.y;

			ai.setPosition(x, y);
			break;
		case DEFENDING:
			FlxPoint a = new FlxPoint(puck.position.x, puck.position.y);
			FlxPoint b = new FlxPoint(redGoal.position.x, redGoal.position.y);

			double angle = Math.toRadians(FlxU.getAngle(b, a));

			x = b.x + (float) (Math.sin(angle) * range);
			y = b.y - (float) (Math.cos(angle) * 16);

			ai.setPosition(x, y);
			break;
		}
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

		aggroModifier = (float) Math.random();
		rangeModifier = (float) Math.random();

		System.out.println(aggroModifier + ", " + rangeModifier);
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