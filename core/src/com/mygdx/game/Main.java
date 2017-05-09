package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.seisw.util.geom.Clip;
import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;

public class Main extends ApplicationAdapter {

	World world;
	OrthographicCamera cam;
	ShapeRenderer sr;

	Polygon box1;
	Polygon box2;
	CameraController cc;
	@Override
	public void create() {

		cam = new OrthographicCamera();
		cam.setToOrtho(false);

		cc = new CameraController(cam);
		Gdx.input.setInputProcessor(cc);
		sr = new ShapeRenderer();

		//float[] verts = { 5, 5, 25, 0, 5,10, 0, 0, };
		
		Random rand = new Random();
		float[] verts = new float[40*2];
		
		for(int i = 0; i < 40*2;i++){
			verts[i] = rand.nextInt(500000);
		}
		
		
		
		
		
		
		
		
		box1 = new Polygon();
		box1.scale(500);
		box1.translate(50, 50);
		box1.setVertices(verts);

		int j = 0;

		float[] circleVerts = new float[360 * 2];
		for (int i = 0; i < 360; i++) {
			double rads = Math.toRadians(i);
			double x = Math.sin(rads);
			double y = Math.cos(rads);

			circleVerts[j] = (float) x;
			circleVerts[j + 1] = (float) y;
			j += 2;
		}

		box2 = new Polygon();
		box2.scale(50);
		box2.translate(125, 125);
		box2.setVertices(circleVerts);
		
		
		
		
		Array<Vector2> poly = this.verticesConverter(box1.getTransformedVertices());
		Rectangle bounds = new Rectangle();
		bounds.x = this.findLeftMostPoint(poly);
		bounds.y = this.findLowestPoint(poly);
		bounds.height = this.findHighestPoint(poly) - bounds.y;
		bounds.width = this.findRightMostPoint(poly) - bounds.x;
		

		
		cam.position.set(bounds.x + bounds.width/2, bounds.y + bounds.height /2, 0);
		
	}

	public void update() {
		cam.update();
		cc.update();
	}

	ArrayList<Rectangle> points = new ArrayList<Rectangle>();

	public ArrayList<Rectangle> fillPolygonWithRec(float[] vertices) {
		points.clear();
		
		Array<Vector2> poly = this.verticesConverter(vertices);
		Rectangle rec = new Rectangle();
		rec.x = this.findLeftMostPoint(poly);
		rec.y = this.findLowestPoint(poly);
		rec.height = this.findHighestPoint(poly) - rec.y;
		rec.width = this.findRightMostPoint(poly) - rec.x;
		
		looper(this.verticesConverter(vertices),rec,10);
		
		
		
		return points;
	}

	public float findHighestPoint(Array<Vector2> poly) {
		float highest = poly.get(0).y;
		for(Vector2 point :poly){
			if(point.y > highest){
				highest = point.y;
			}
		}
		return highest;
	}

	public float findLowestPoint(Array<Vector2> poly) {
		float lowest = poly.get(0).y;
		for(Vector2 point :poly){
			if(point.y < lowest){
				lowest = point.y;
			}
		}
		return lowest;
	}
	
	public float findLeftMostPoint(Array<Vector2> poly) {
		float left = poly.get(0).y;
		for(Vector2 point :poly){
			if(point.x < left){
				left = point.x;
			}
		}
		return left;
	}
	
	public float findRightMostPoint(Array<Vector2> poly) {
		float right = poly.get(0).y;
		for(Vector2 point :poly){
			if(point.x > right){
				right = point.x;
			}
		}
		return right;
	}
	

	public void looper(Array<Vector2> poly, Rectangle rec, int seg) {
		if (isRectangleInsidePolygon(poly, rec)) {
			points.add(rec);
			
			return;
		} else if (seg > 0) {
			looper(poly, new Rectangle(rec.x, rec.y, rec.width / 2, rec.height / 2), seg - 1);
			looper(poly, new Rectangle(rec.x + (rec.width / 2), rec.y, rec.width / 2, rec.height / 2), seg - 1);
			looper(poly,
					new Rectangle(rec.x + (rec.width / 2), rec.y + (rec.height / 2), rec.width / 2, rec.height / 2),
					seg - 1);
			looper(poly, new Rectangle(rec.x, rec.y + (rec.height / 2), rec.width / 2, rec.height / 2), seg - 1);
		}

	}

	public boolean isRectangleInsidePolygon(Array<Vector2> poly, Rectangle rec) {
		
		//sr.rect(rec.x, rec.y, rec.width, rec.height);
		
		
		
		Vector2 point1 = new Vector2(rec.x, rec.y + rec.height);
		Vector2 point2 = new Vector2(rec.x + rec.width, rec.y + rec.height);
		Vector2 point3 = new Vector2(rec.x + rec.width, rec.y);
		Vector2 point4 = new Vector2(rec.x, rec.y);

		if (Intersector.isPointInPolygon(poly, point1)) {
			if (Intersector.isPointInPolygon(poly, point2)) {
				if (Intersector.isPointInPolygon(poly, point3)) {
					if (Intersector.isPointInPolygon(poly, point4)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Array<Vector2> verticesConverter(float[] vertices) {
		Array<Vector2> points = new Array<Vector2>();
		for (int i = 0; i < vertices.length; i += 2) {
			points.add(new Vector2(vertices[i], vertices[i + 1]));
		}
		return points;
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		update();

		Array<Vector2> poly = this.verticesConverter(box1.getTransformedVertices());
		Rectangle bounds = new Rectangle();
		bounds.x = this.findLeftMostPoint(poly);
		bounds.y = this.findLowestPoint(poly);
		bounds.height = this.findHighestPoint(poly) - bounds.y;
		bounds.width = this.findRightMostPoint(poly) - bounds.x;
		

		
		
		sr.setAutoShapeType(true);
		sr.setProjectionMatrix(cam.combined);
		sr.begin(ShapeRenderer.ShapeType.Line);
		
		
		fillPolygonWithRec(box1.getTransformedVertices());
		sr.setColor(Color.RED);
		sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		

		
		
		ArrayList<Rectangle> recs = points;
		for(Rectangle rec: recs){
			sr.rect(rec.x, rec.y, rec.width, rec.height);
		}
		
		// sr.polygon(box1.getTransformedVertices());
		// sr.polygon(box2.getTransformedVertices());

		box2.setPosition(cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).x,
				cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).y);
		PolyDefault poly1 = new PolyDefault();

		for (int i = 0; i < box1.getTransformedVertices().length; i += 2) {
			poly1.add(box1.getTransformedVertices()[i], box1.getTransformedVertices()[i + 1]);
		}
		
		

		PolyDefault poly2 = new PolyDefault();

		for (int i = 0; i < box2.getTransformedVertices().length; i += 2) {
			poly2.add(box2.getTransformedVertices()[i], box2.getTransformedVertices()[i + 1]);
		}

		Poly result = Clip.difference(poly1, poly2);

		FloatArray array = new FloatArray();
		for (int i = 0; i < result.getNumPoints(); i++) {
			array.add((float) result.getX(i));
			array.add((float) result.getY(i));
		}
		sr.polygon(array.toArray());
		sr.end();

	}

	@Override
	public void dispose() {
	}
}
