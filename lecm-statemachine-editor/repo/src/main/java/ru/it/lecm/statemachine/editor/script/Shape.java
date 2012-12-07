package ru.it.lecm.statemachine.editor.script;

import java.util.ArrayList;

/**
 * User: PMelnikov
 * Date: 06.12.12
 * Time: 17:08
 */
public class Shape {

	private int step;

	private String id;
	private int x;
	private int y;
	private int width;
	private int height;
	private Shape parent;

	private ArrayList<Shape> shapes = new ArrayList<Shape>();

	public Shape(String id, int x, int y, int width, int height) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		step = 50;
	}

	public Shape getParent() {
		return parent;
	}

	public void setParent(Shape parent) {
		this.parent = parent;
	}

	public String getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
		for (Shape shape : shapes) {
			shape.setX(width + x + step);
		}
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void addShape(Shape shape) {
		shape.setParent(this);
		shapes.add(shape);
		int startX = 0;
		Shape parent = this;
		while ((parent = parent.getParent()) != null) {
			startX += step + parent.width;
		}
		setX(startX);
	}

}