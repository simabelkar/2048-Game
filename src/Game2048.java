
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
* @author Sima Dahan, ID: 300249943
*/
public class Game2048 extends JPanel {
	
	private static final Color BG_COLOR = new Color(0xbbada0);
	private static final String FONT_NAME = "Arial";
	private static final int TILE_SIZE = 64;
	private static final int TILES_MARGIN = 16;
	private static final int TILES_ROW = 4;
	private static final int TARGET_SUM = 2048;
	
	private Tile[] myTiles;
	boolean myWin = false;
	boolean myLose = false;
	int myScore = 0;

	
	public Game2048() {
		
		setFocusable(true);
		
		// get the key pressed
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					resetGame();
				}
				if (!canMove()) {
					myLose = true;
				}

				if (!myWin && !myLose) {//ongoing game, capture the key pressed
					switch (e.getKeyCode()) {
						case KeyEvent.VK_LEFT:
							left();
							break;
						case KeyEvent.VK_RIGHT:
							right();
							break;
						case KeyEvent.VK_DOWN:
							down();
							break;
						case KeyEvent.VK_UP:
							up();
							break;
					}
				}

				if (!myWin && !canMove()) {// no winning, no moves available => lose
					myLose = true;
				}
				repaint();
			}
		});

		resetGame();
	}

	public void resetGame() {
		myScore = 0;
		myWin = false;
		myLose = false;
		myTiles = new Tile[TILES_ROW * TILES_ROW];
		for (int i = 0; i < myTiles.length; i++) {
			myTiles[i] = new Tile();
		}
		//2 tiles for beginning a new game:
		addTile();
		addTile();
	}

	
	// ******* Key Pressed - Directions: ******* //
	
	//left()- main function of directions,
	//all other moves will use this after a rotation
	public void left() {
		boolean needAddTile = false;
		
		for (int i = 0; i < TILES_ROW; i++) {
			
			Tile[] line = getLine(i);
			Tile[] merged = mergeLine(moveLine(line));//new line after merging to the left
			setLine(i, merged);//set the merged line in the right index
			
			if (!needAddTile && !compare(line, merged)) {
				needAddTile = true;
			}
			
		}
		if (needAddTile) {
			addTile();
		}
	}

	public void right() {
		myTiles = rotate(180);//Adjust the board to left move
		left();
		myTiles = rotate(180);//revert to correct state of the board
	}

	public void up() {
		myTiles = rotate(270);
		left();
		myTiles = rotate(90);//revert to correct state of the board
	}

	public void down() {
		myTiles = rotate(90);
		left();
		myTiles = rotate(270);//revert to correct state of the board
	}

	// ******* End of Key Pressed - Directions ******* //
	
	
	private Tile tileAt(int x, int y) {
		return myTiles[x + y * TILES_ROW];
	}

	private void addTile() {
		
		List<Tile> list = availableSpace();
		
		if (!availableSpace().isEmpty()) {// there's values in the list=> some tiles available in the board
			//add randomly a tile in 1 of the available tiles from the list:  
			int index = (int) (Math.random() * list.size()) % list.size();
			Tile emptyTime = list.get(index); 
			//new tile with either 2 or 4 content
			emptyTime.value = Math.random() < 0.9 ? 2 : 4;
			// return;
			// JOptionPane.showMessageDialog(null, emptyTime.toString(),
			//		 	"message",		 
			//	        JOptionPane.ERROR_MESSAGE);
		}
	}

	//returns a list with all available spaces in the board
	private List<Tile> availableSpace() {
		final List<Tile> list = new ArrayList<Tile>(16);
		for (Tile t : myTiles) {
			if (t.isEmpty()) {
				list.add(t);
			}
		}
		return list;
	}
	
	private boolean isFull() {
		return availableSpace().size() == 0;//no tiles in the list
	}

	boolean canMove() {
		if (!isFull()) {//board isn't full => can make a move
			return true;
		}
		for (int x = 0; x < TILES_ROW; x++) {
			for (int y = 0; y < TILES_ROW; y++) {
				Tile t = tileAt(x, y);
				//(TILES_ROW-1) => except last row or col
				if ((x < (TILES_ROW-1) && t.value == tileAt(x + 1, y).value)
					|| ((y < (TILES_ROW-1)) && t.value == tileAt(x, y + 1).value)) {
						return true;
				}
			}
		}
		return false;//board is full => cannot make a move
	}

	//comparison between 2 lines: length & value
	private boolean compare(Tile[] line1, Tile[] line2) {
		if (line1 == line2) {
			return true;
		} else if (line1.length != line2.length) {
			return false;
		}

		for (int i = 0; i < line1.length; i++) {
			if (line1[i].value != line2[i].value) {
				return false;
			}
		}
		return true;
	}

	//rotation of the board, in order to allow movement using the left() func
	private Tile[] rotate(int angle) {
		Tile[] newTiles = new Tile[TILES_ROW * TILES_ROW];
		int offsetX = 3, offsetY = 3;
		
		if (angle == 90) {
			offsetY = 0;
		} else if (angle == 270) {
			offsetX = 0;
		}

		double rad = Math.toRadians(angle);
		int cos = (int) Math.cos(rad);
		int sin = (int) Math.sin(rad);
		
		for (int x = 0; x < TILES_ROW; x++) {
			for (int y = 0; y < TILES_ROW; y++) {
				int newX = (x * cos) - (y * sin) + offsetX;
				int newY = (x * sin) + (y * cos) + offsetY;
				newTiles[(newX) + (newY) * TILES_ROW] = tileAt(x, y);
			}
		}
		return newTiles;
	}

	// movement of a line
	private Tile[] moveLine(Tile[] oldLine) {
		LinkedList<Tile> l = new LinkedList<Tile>();
		
		for (int i = 0; i < TILES_ROW; i++) {
			if (!oldLine[i].isEmpty())
			l.addLast(oldLine[i]);
		}
		
		if (l.size() == 0) {//no tiles in the line
			return oldLine;
		} 
		else {
			Tile[] newLine = new Tile[TILES_ROW];
			ensureSize(l, TILES_ROW);
			
			for (int i = 0; i < TILES_ROW; i++) {
				newLine[i] = l.removeFirst();
			}
			
			return newLine;
		}
	}

	//merge lines into 1 line, after making a move
	private Tile[] mergeLine(Tile[] oldLine) {
		LinkedList<Tile> list = new LinkedList<Tile>();
		
		for (int i = 0; i < TILES_ROW && !oldLine[i].isEmpty(); i++) {
			int num = oldLine[i].value;
			if (i < (TILES_ROW-1) && oldLine[i].value == oldLine[i + 1].value) {//equals nums on tiles
				num *= 2;//twice the num- merged into 1 tile 
				myScore += num;
				int ourTarget = TARGET_SUM;
				if (num == ourTarget) {
					myWin = true;
				}
				i++;
			}
			list.add(new Tile(num));
		}
		if (list.size() == 0) {
			return oldLine;
		} 
		else {
			ensureSize(list, TILES_ROW);
			return list.toArray(new Tile[TILES_ROW]);
		}
	}

	//keep board to be in size it should
	private static void ensureSize(java.util.List<Tile> l, int s) {
		while (l.size() != s) {
			l.add(new Tile());
		}
	}

	//returns the line requested
	private Tile[] getLine(int index) {
		Tile[] result = new Tile[TILES_ROW];
		
		for (int i = 0; i < TILES_ROW; i++) {
			result[i] = tileAt(i, index);
		}
		return result;
	}

	private void setLine(int index, Tile[] src) {
		// arraycopy(Object src, int srcPos, Object dest, int destPos, int length))
		System.arraycopy(src, 0, myTiles, index * TILES_ROW, TILES_ROW);
	}

	
	//paint board and tiles
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, this.getSize().width, this.getSize().height);
		
		for (int y = 0; y < TILES_ROW; y++) {
			for (int x = 0; x < TILES_ROW; x++) {
				drawTile(g, myTiles[x + y * TILES_ROW], x, y);
			}
		}
	}

	//draw each tile
	private void drawTile(Graphics g2, Tile tile, int x, int y) {
		Graphics2D g = ((Graphics2D) g2);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		
		int value = tile.value;
		int xOffset = offsetCoors(x);
		int yOffset = offsetCoors(y);
		//color
		g.setColor(tile.getBackground());
		g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14);
		g.setColor(tile.getForeground());
		//size
		final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
		final Font font = new Font(FONT_NAME, Font.BOLD, size);
		g.setFont(font);

		String s = String.valueOf(value);
		final FontMetrics fm = getFontMetrics(font);

		final int w = fm.stringWidth(s);
		final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

		if (value != 0)
			g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);

		if (myWin || myLose) { //drawing white square over the board
			g.setColor(new Color(255, 255, 255, 30));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(new Color(78, 139, 202));
			g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
			
			//message win/ lose:
			if (myWin) {
				g.drawString("You won!", 68, 150);
			}
			if (myLose) {
				g.drawString("Game over!", 50, 130);
				g.drawString("You lose!", 64, 200);
			}
			
			if (myWin || myLose) {
				g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
				g.setColor(new Color(128, 128, 128, 128));
				g.drawString("Press ESC to play again", 80, getHeight() - 40);
			}
		}
		g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
		g.drawString("Score: " + myScore, 200, 365);

	}

	private static int offsetCoors(int arg) {
		return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
	}

	
	
	//static class => inner class
	// this class describes each Tiles drawn
	static class Tile {
		int value;

		public Tile() {
			this(0);
		}

		public Tile(int num) {
			value = num;
		}

		public boolean isEmpty() {
			return value == 0;
		}

		public Color getForeground() {
			//
			return value < 16 ? new Color(0x776e65) : new Color(0xf9f6f2);
		}

		public Color getBackground() {
			switch (value) {
				case 2: return new Color(0xeee4da);
				case 4: return new Color(0xede0c8);
				case 8: return new Color(0xf2b179);
				case 16: return new Color(0xf59563);
				case 32: return new Color(0xf67c5f);
				case 64: return new Color(0xf65e3b);
				case 128: return new Color(0xedcf72);
				case 256: return new Color(0xedcc61);
				case 512: return new Color(0xedc850);
				case 1024: return new Color(0xedc53f);
				case 2048: return new Color(0xedc22e);
			}
			return new Color(0xcdc1b4);
		}
	}
}

