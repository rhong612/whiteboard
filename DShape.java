import java.awt.*;
import java.util.ArrayList;

public abstract class DShape implements ModelListener
{
	private DShapeModel shapeModel;
	private Canvas view;
	
	public DShape() {
		shapeModel = null;
	}
	
	public abstract void draw(Graphics g);
	
	public int getX() {
		return shapeModel.getX();
	}

	public int getY() {
		return shapeModel.getY();
	}

	public int getWidth() {
		return shapeModel.getWidth();
	}

	public int getHeight() {
		return shapeModel.getHeight();
	}
	
	public Color getColor() {
		return shapeModel.getColor();
	}
	
	public Rectangle getBounds()
	{
		return shapeModel.getBounds();
	}
	
	public void attachView(Canvas view)
	{
		this.view = view;
	}
	
	@Override
	public void modelChanged(DShapeModel model) {
		view.repaint();
	}
	
	public void setModel(DShapeModel newModel) {
		shapeModel = newModel;
		shapeModel.addModelListener(this);
	}
	
	public DShapeModel getModel()
	{
		return shapeModel;
	}
	
	public ArrayList<Point> getKnobs() {
		ArrayList<Point> knobs = new ArrayList<>();
		Point upperLeft = new Point(getX(), getY());
		Point upperRight = new Point(getX() + getWidth(), getY());
		Point bottomLeft = new Point(getX(), getY() + getHeight());
		Point bottomRight = new Point(getX() + getWidth(), getY() + getHeight());
		knobs.add(upperLeft);
		knobs.add(upperRight);
		knobs.add(bottomLeft);
		knobs.add(bottomRight);
		return knobs;
	}
	
	public Rectangle getBiggerBounds()
	{
		Rectangle smallBounds = this.getBounds();
		int knobSize = 9;
		return new Rectangle (smallBounds.x - knobSize/2, smallBounds.y - knobSize/2, smallBounds.width + knobSize, smallBounds.height+knobSize);
	}
}
