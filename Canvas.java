import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class Canvas extends JPanel
{
	private ArrayList<DShape> shapes;
	private DShape selected;

	public static final int KNOB_SIZE = 9;
	
	private class CanvasMouseListener implements MouseListener, MouseMotionListener
	{	
		
		private int preXGap; 
		private int preYGap;
		private boolean moving = false;
		private boolean resizingHorizontally = false;
		private boolean resizingVertically = false;
		private boolean cursorChange = false;
	
		@Override
		public void mouseDragged(MouseEvent e) {
			if (selected != null) {
				DShapeModel selectedModel = selected.getModel();
				Point anchorPoint = selectedModel.getAnchor();
				if (moving) {
					selectedModel.setX(e.getX() - preXGap);
					selectedModel.setY(e.getY() - preYGap);
				} else if (resizingHorizontally || resizingVertically) {
					
					int newWidth = e.getX() - anchorPoint.x;
					int newHeight = e.getY() - anchorPoint.y;
					if(resizingHorizontally)
					{
						selectedModel.setWidth(newWidth);
						if (preXGap < 0) // coming from the left
						{
							selectedModel.setX(e.getX());
							if (e.getX() > anchorPoint.x)
							{
								selectedModel.setX(anchorPoint.x);
							}
						}
						else if (preXGap > 0 && e.getX() < anchorPoint.x) // coming from the right
						{
							selectedModel.setX(e.getX());
						}
					}
					if (resizingVertically)
					{
						selectedModel.setHeight(newHeight);
						if (preYGap < 0) //coming from the top
						{
							selectedModel.setY(e.getY());
							if (e.getY() > anchorPoint.y)
							{
								selectedModel.setY(anchorPoint.y);
							}
						}
						else if(preYGap > 0 && e.getY() < anchorPoint.y)
						{
							selectedModel.setY(e.getY());
						}
					}
					preXGap = e.getX() - anchorPoint.x;
					preYGap = e.getY() - anchorPoint.y;
				} 
			}
		}
		
		
		@Override
		public void mousePressed(MouseEvent e) {
			//Allows user to select shape in background by double clicking
			if (e.getClickCount() > 1) {
				for (DShape s : shapes) {
					Rectangle bounds = s.getBounds();
					if (bounds.contains(e.getPoint()) && s != selected) {
						setSelected(s);
						break;
					}
				}
			}
			//Else select front-most shape
			else {
				for (DShape s : shapes) {
					Rectangle bounds = s.getBounds();
					if (bounds.contains(e.getPoint())) {
						setSelected(s);
					} 
				}
			}
			
			if (selected != null && selected.getBiggerBounds().contains(e.getPoint())) {
				
				if (selected.getBounds().contains(e.getPoint())) {
					moving = true;
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
					cursorChange = true;
					preXGap = e.getX() - selected.getX();
					preYGap = e.getY() - selected.getY();
				}
				else
				{
					Point resizePoint = e.getPoint();
					Point anchorPoint = new Point();
					boolean left = resizePoint.x > selected.getX() - KNOB_SIZE/2 && resizePoint.x < selected.getX() + KNOB_SIZE/2;
					boolean right = resizePoint.x > selected.getX() + selected.getWidth() - KNOB_SIZE/2 && resizePoint.x < selected.getX() + selected.getWidth() + KNOB_SIZE/2;
					boolean top = resizePoint.y > selected.getY() - KNOB_SIZE/2 && resizePoint.y < selected.getY() + KNOB_SIZE/2;
					boolean bottom = resizePoint.y > selected.getY() + selected.getHeight() - KNOB_SIZE/2 && resizePoint.y < selected.getY() + selected.getHeight() + KNOB_SIZE/2;
					resizingHorizontally = left || right;
					resizingVertically = top || bottom;
					if (left)
					{
						anchorPoint.x = selected.getX() + selected.getWidth();
					}
					else if (right)
					{
						anchorPoint.x = selected.getX();
					}
					if (top)
					{
						anchorPoint.y = selected.getY() + selected.getHeight();
					}
					else if (bottom)
					{
						anchorPoint.y = selected.getY();
					}
					selected.getModel().setAnchor(anchorPoint);
					preXGap = (left) ?  selected.getWidth() * -1 : selected.getWidth();
					preYGap = (top)? selected.getHeight() * -1 : selected.getHeight();
				}
			}
		}
			
		
		@Override
		public void mouseReleased(MouseEvent e) {
			moving = false;
			resizingHorizontally = false;
			resizingVertically = false;
			if (cursorChange) {setCursor(new Cursor(Cursor.DEFAULT_CURSOR));}
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {	
			
			if (selected != null) {
				Rectangle smallBounds = selected.getBounds();
				Rectangle bigBounds = selected.getBiggerBounds();
				if (bigBounds.contains(e.getPoint()) && !smallBounds.contains(e.getPoint())) {
					setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
					cursorChange = true;
				} else if (cursorChange) {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					cursorChange = false;
				} 
			}
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
		}
		
		
		@Override
		public void mouseClicked(MouseEvent e) {	
		}
		
	}
	
	public Canvas() {
		int INITIAL_WIDTH = 400;
		int INITIAL_HEIGHT = 400;
		setPreferredSize(new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT));
		setBackground(Color.WHITE);
		CanvasMouseListener mouseListener = new CanvasMouseListener();
		this.addMouseMotionListener(mouseListener);
		this.addMouseListener(mouseListener);
		shapes = new ArrayList<>();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (DShape shape : shapes) {
			shape.draw(g);
			if (shape.equals(selected)) {
				ArrayList<Point> knobs = shape.getKnobs();
				g.setColor(Color.BLACK);
				for (int i = 0; i < knobs.size(); i++) {
					g.fillRect(knobs.get(i).x - KNOB_SIZE / 2, knobs.get(i).y - KNOB_SIZE / 2, KNOB_SIZE, KNOB_SIZE);	
				}
				Rectangle biggerBounds = shape.getBiggerBounds();
				g.drawRect(biggerBounds.x, biggerBounds.y, biggerBounds.width, biggerBounds.height);
			}
		}
	}
	
	public void addShape(DShapeModel shapeModel) {
		DShape theShape;
		if (shapeModel instanceof DRectModel) {
			theShape = new DRect();
		}
		else if (shapeModel instanceof DOvalModel) {
			theShape = new DOVal();
		}
		else if (shapeModel instanceof DTextModel) {
			theShape = new DText(); 
		}
		else{
			theShape = new DLine();
		}
		theShape.setModel(shapeModel);
		theShape.attachView(this);
		shapes.add(theShape);
		selected = theShape;
		repaint();
	}
	
	public DShape getSelected()
	{
		return selected;
	}
	
	private void setSelected(DShape shape)
	{
		selected = shape;
		repaint();
	}
	
	public void removeShape()
	{
		if (selected != null) {
			shapes.remove(selected);
			if (shapes.size() > 0)
				selected = shapes.get(shapes.size() -1);
			else
				selected = null;
			repaint();	
		}
	}
	
	public void moveToFront()
	{
		if (selected != null) {
			shapes.remove(selected);
			shapes.add(shapes.size(), selected);
			repaint();	
		}
	}

	public void moveToBack()
	{
		if (selected != null) {
			shapes.remove(selected);
			shapes.add(0, selected);
			repaint();
		}
	}
	
	public ArrayList<DShape> getShapes() {
		return shapes;
	}
}
