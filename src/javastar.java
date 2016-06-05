import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.geom.*;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.GridLayout;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.util.*;
import java.awt.event.*;

class Config {
  public static int CELL_SIZE = 25;
}

enum CellType {
  FREE, WALL, START, END
}

class Cell extends JComponent {
  private Rectangle2D rect;
  private CellType type;
  private Graphics2D graphics;
  private int row;
  private int col;
  private Grid grid;
  private Color color;
  private boolean isHiglighted;

  public Cell(int col, int row, CellType type, Grid grid) {
    super();
    this.isHiglighted = false;
    this.type = CellType.FREE;
    this.col = col;
    this.row = row;
    this.grid = grid;
    setType(type);
    int x = col * Config.CELL_SIZE;
    int y = row * Config.CELL_SIZE;
    setBounds(x, y, Config.CELL_SIZE - 2, Config.CELL_SIZE - 2);
  }

  public void onClick(MouseEvent ev) {
    setType(grid.getSelectedType());
    grid.repaint();
  }

  public void onDrag(MouseEvent ev) {
    setType(grid.getSelectedType());
    grid.repaint();
  }

  public int getCol() {
    return this.col;
  }

  public int getRow() {
    return this.row;
  }

  public void setType(CellType type) {
    this.type = type;
    color = Color.white;
    if (type == CellType.FREE) {
      color = Color.white;
    } else if (type == CellType.WALL) {
      color = Color.darkGray;
    } else if (type == CellType.START) {
      color = Color.green;
      if (grid.getStartNode() != null) {
        grid.getStartNode().setType(CellType.FREE);
      }
      grid.setStartNode(this);
    } else if (type == CellType.END) {
      color = Color.red;
      if (grid.getEndNode() != null) {
        grid.getEndNode().setType(CellType.FREE);
      }
      grid.setEndNode(this);
    }
  }

  public CellType getType() {
    return this.type;
  }

  private void drawMe() {
    graphics.setColor(color);
    double w = Config.CELL_SIZE;
    graphics.fill(new Rectangle2D.Double(0, 0, w, w));
    if (isHiglighted) {
      graphics.setColor(Color.orange);
      double radius = 3.0;
      graphics.draw(new Ellipse2D.Double(Config.CELL_SIZE / 2.0 - radius, Config.CELL_SIZE / 2.0 - radius, 2.0 * radius, 2.0 * radius));
    }
  }

  public void highlightMe() {
    this.isHiglighted = true;
  }

  public void dehighlightMe() {
    this.isHiglighted = false;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    graphics = (Graphics2D)g;
    graphics.setStroke(new BasicStroke(3));
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    drawMe();
  }
}

class Grid extends JPanel {
  private int rows;
  private int columns;
  private List<Cell> cells;
  private Cell startNode;
  private Cell endNode;
  private CellType selectedType = CellType.FREE;
  private PathFinder pathfinder;

  public Grid(int columns, int rows) {
    super();
    this.columns = columns;
    this.rows = rows;
    this.cells = new ArrayList<Cell>();
    this.pathfinder = new PathFinder(this);
    setLayout(null);

    for(int row = 0; row < rows; ++row) {
      for (int col = 0; col < columns; ++col) {
        Cell cell = new Cell(col, row, CellType.FREE, this);
        cells.add(cell);
        add(cell);
      }
    }

    Grid grid = this;
    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent ev) {
        int x = ev.getX();
        int y = ev.getY();
        int col = (int) Math.floor(x / Config.CELL_SIZE);
        int row = (int) Math.floor(y / Config.CELL_SIZE);
        grid.getCell(col, row).onDrag(ev);
        grid.showPath();
      }
    });

    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent ev) {
        int x = ev.getX();
        int y = ev.getY();
        int col = (int) Math.floor(x / Config.CELL_SIZE);
        int row = (int) Math.floor(y / Config.CELL_SIZE);
        grid.getCell(col, row).onClick(ev);
        grid.showPath();
      }
    });
  }

  public int getColumns() {
    return this.columns;
  }

  public int getRows() {
    return this.rows;
  }

  public List<Cell> getNeighbours(Cell c) {
    int row = c.getRow();
    int col = c.getCol();
    List<Cell> out = new ArrayList<Cell>();
    int numCells = cells.size();

    // north
    int curRow = row - 1;
    int curCol = col;
    if (curRow >= 0 && curRow < rows && curCol >= 0 && curCol < columns) {
      Cell cell = getCell(curCol, curRow);
      if (cell.getType() != CellType.WALL)
        out.add(cell);
    }

    // south
    curRow = row + 1;
    curCol = col;
    if (curRow >= 0 && curRow < rows && curCol >= 0 && curCol < columns) {
      Cell cell = getCell(curCol, curRow);
      if (cell.getType() != CellType.WALL)
        out.add(cell);
    }

    // west
    curRow = row;
    curCol = col - 1;
    if (curRow >= 0 && curRow < rows && curCol >= 0 && curCol < columns) {
      Cell cell = getCell(curCol, curRow);
      if (cell.getType() != CellType.WALL)
        out.add(cell);
    }

    // east
    curCol = col + 1;
    curRow = row;
    if (curRow >= 0 && curRow < rows && curCol >= 0 && curCol < columns) {
      Cell cell = getCell(curCol, curRow);
      if (cell.getType() != CellType.WALL)
        out.add(cell);
    }

    // north east
    curCol = col + 1;
    curRow = row - 1;
    if (curRow >= 0 && curRow < rows && curCol >= 0 && curCol < columns) {
      Cell cell = getCell(curCol, curRow);
      if (cell.getType() != CellType.WALL)
        out.add(cell);
    }
    // north west
    curCol = col - 1;
    curRow = row - 1;
    if (curRow >= 0 && curRow < rows && curCol >= 0 && curCol < columns) {
      Cell cell = getCell(curCol, curRow);
      if (cell.getType() != CellType.WALL)
        out.add(cell);
    }
    // south east
    curCol = col + 1;
    curRow = row + 1;
    if (curRow >= 0 && curRow < rows && curCol >= 0 && curCol < columns) {
      Cell cell = getCell(curCol, curRow);
      if (cell.getType() != CellType.WALL)
        out.add(cell);
    }
    // south west
    curCol = col - 1;
    curRow = row + 1;
    if (curRow >= 0 && curRow < rows && curCol >= 0 && curCol < columns) {
      Cell cell = getCell(curCol, curRow);
      if (cell.getType() != CellType.WALL)
        out.add(cell);
    }
    return out;
  }

  public List<Cell> getCells() {
    return cells;
  }

  public Cell getCell(int col, int row) {
    int index = col + row * columns;
    return cells.get(index);
  }

  public Cell getEndNode() {
    return this.endNode;
  }

  public void setEndNode(Cell endNode) {
    this.endNode = endNode;
  }

  public Cell getStartNode() {
    return this.startNode;
  }

  public void setStartNode(Cell startNode) {
    this.startNode = startNode;
  }

  public CellType getSelectedType() {
    return this.selectedType;
  }

  public void setSelectedType(CellType type) {
    this.selectedType = type;
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(Config.CELL_SIZE * columns, Config.CELL_SIZE * rows);
  }

  @Override
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  public void showPath() {
    for (Cell c : getCells()) {
      c.dehighlightMe();
    }
    if (getStartNode() == null || getEndNode() == null)
      return;
    List<Cell> path = pathfinder.find(getStartNode(), getEndNode());
    for (Cell c : path) {
      c.highlightMe();
    }
    repaint();
  }
}

class PathFinder {
  private Set<Cell> closed;
  private HashMap<Cell, Node> open;
  private PriorityQueue<PathFinder.Node> queue;
  private Grid grid;

  private final double DIAGONAL_COST = 14.0f;
  private final double STRAIGHT_COST = 10.0f;

  static class Node implements Comparable<Node> {
    public Node parent;
    public Cell cell;
    public double g, h, f;
    public Node(Cell cell, Node parent, double g, double h) {
      this.cell = cell;
      this.g = g;
      this.h = h;
      this.parent = parent;
      this.f = g + h;
    }
    @Override
    public boolean equals(Object other) {
      if (other == this)
        return true;
      if (other == null || other.getClass() != this.getClass())
        return false;
      Node n = (Node) other;
      return n.f == this.f;
    }
    @Override
    public int compareTo(Node other) {
      return (other.f < this.f) ? 1 : (other.f > this.f) ? -1 : 0;
    }
  }

  public PathFinder(Grid grid) {
    this.grid = grid;
    closed = new HashSet<Cell>();
    open = new HashMap<Cell, Node>();
    queue = new PriorityQueue<PathFinder.Node>();
  };

  public List<Cell> find(Cell from, Cell to) {
    closed.clear();
    open.clear();
    queue.clear();
    List<Cell> out = new ArrayList<Cell>();
    Node node = new Node(from, null, 0.0, 0.0);
    open.put(from, node);
    queue.add(node);
    while(queue.size() > 0) {
      Node curNode = queue.poll();
      Cell curCell = curNode.cell;
      if (curCell == to) {
        Node parent = curNode;
        while (parent != null) {
          out.add(parent.cell);
          parent = parent.parent;
        }
        break;
      }
      closed.add(curCell);
      List<Cell> neighbours = grid.getNeighbours(curCell);
      for (Cell neigh : neighbours) {
        double g = DIAGONAL_COST;
        if ((curCell.getRow() == neigh.getRow()) || (curCell.getCol() == neigh.getCol()))
          g = STRAIGHT_COST;
        if (closed.contains(neigh))
          continue;
        if (!open.containsKey(neigh)) {
          int xDiff = Math.abs(neigh.getCol() - curCell.getCol());
          int yDiff = Math.abs(neigh.getRow() - curCell.getRow());
          double h = STRAIGHT_COST * (xDiff + yDiff) + (DIAGONAL_COST - 2 * STRAIGHT_COST) * Math.min(xDiff, yDiff);
          node = new Node(neigh, curNode, curNode.g + g, h);
          queue.add(node);
          open.put(neigh, node);
        } else {
          Node n = open.get(neigh);
          if ((curNode.g + g) < n.g) {
            n.g = curNode.g + g;
            n.f = n.h + n.g;
            n.parent = curNode;
          }
        }
      }
    }
    return out;
  }
}

public class javastar extends JFrame {
  public javastar() {
    initUI();
  }

  private void initUI() {
    setTitle("A* pathfinding");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel container = new JPanel();
    Grid grid = new Grid(25, 20);

    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    container.add(grid);
    add(container);

    JPanel btnContainer = new JPanel();
    btnContainer.setLayout(new BoxLayout(btnContainer, BoxLayout.X_AXIS));
    container.add(btnContainer);

    ActionListener myListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        switch (((JButton)e.getSource()).getText()) {
          case "S": {
            grid.setSelectedType(CellType.START);
            break;
          }
          case "E": {
            grid.setSelectedType(CellType.END);
            break;
          }
          case "F": {
            grid.setSelectedType(CellType.FREE);
            break;
          }
          case "B": {
            grid.setSelectedType(CellType.WALL);
            break;
          }
          default: {
            grid.setSelectedType(CellType.FREE);
          }
        }
      }
    };

    JButton start = new JButton("S");
    JButton end = new JButton("E");
    JButton free = new JButton("F");
    JButton wall = new JButton("B");
    JButton fpath = new JButton("Find path!");

    fpath.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        grid.showPath();
      }
    });

    start.addActionListener(myListener);
    end.addActionListener(myListener);
    free.addActionListener(myListener);
    wall.addActionListener(myListener);

    start.setBackground(Color.green);
    end.setBackground(Color.red);
    free.setBackground(Color.white);
    wall.setBackground(Color.darkGray);

    Dimension pfDim = new Dimension(Config.CELL_SIZE, Config.CELL_SIZE);
    start.setPreferredSize(pfDim);
    end.setPreferredSize(pfDim);
    free.setPreferredSize(pfDim);
    wall.setPreferredSize(pfDim);
    fpath.setPreferredSize(pfDim);

    btnContainer.add(start);
    btnContainer.add(end);
    btnContainer.add(free);
    btnContainer.add(wall);
    btnContainer.add(fpath);

    container.add(btnContainer);

    Dimension dims = grid.getPreferredSize();
    setSize(dims.width, dims.height + 2 * Config.CELL_SIZE);
    setLocationRelativeTo(null);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        javastar ps = new javastar();
        ps.setVisible(true);
      }
    });
  }
}