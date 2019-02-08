import java.util.Random;

public class MinesweeperModel implements MSModel {

	public Cell grid[][];
	private boolean lose;
	private int flags;
	private int mines;

	public MinesweeperModel (int numrows, int numcols, int numMines)
	{
		grid = new Cell[numrows][numcols];
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				grid[i][j] = new Cell (Cell.BLANK);

		// making max mine density 90% because anything close
		// to 100% dense makes the program's while loop in the make method
		// below run nearly infinitely (take a long time return)
		int temp = numMines;
		double density = (double) temp / (numrows * numcols);
		if (density > 0.9)
			temp = (int) (0.9 * numrows * numcols);

		make (temp, (new Random(System.currentTimeMillis())).nextInt(numrows), (new Random(System.currentTimeMillis())).nextInt(numcols));
		flags = temp;
		mines = temp;

		lose = false;
	}

	public class Cell
	{
		public static final int BLANK = 0,
				MINE = 9,
				ONE = 1,
				TWO = 2,
				THREE = 3,
				FOUR = 4,
				FIVE = 5,
				SIX = 6,
				SEVEN = 7,
				EIGHT = 8;
		private boolean revealed;
		private boolean questionMarked;
		private boolean flagged;
		private int state;

		public Cell (int state)
		{
			this.state = state;
			revealed = false;
			flagged = false;
			questionMarked = false;
		}
	}

	public Cell getCellAt (int row, int col)
	{
		return grid[row][col];
	}

	public void setCellAt (int row, int col, Cell cell)
	{
		grid[row][col] = cell;
	}

	public int getStateAt (int row, int col)
	{
		return grid[row][col].state;
	}

	public void setStateAt (int row, int col, int state)
	{
		grid[row][col].state = state;
	}

	@Override
	public boolean isRevealed(int row, int col) {
		return grid[row][col].revealed;
	}

	@Override
	public void setRevealed(int row, int col, boolean rev) {
		grid[row][col].revealed = rev;
	}

	@Override
	public int getNumMines() {
		int cnt = 0;
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				if (grid[i][j].state == Cell.MINE)
					cnt ++;
		return cnt;
	}

	@Override
	public int getNumRows() {
		return grid.length;
	}

	@Override
	public int getNumCols() {
		return grid[0].length;
	}

	@Override
	public int getNeighbouringMines(int row, int col) {
		if (grid[row][col].state == Cell.MINE)
			return -1;

		int cnt = 0;
		for (int i = row - 1; i <= row + 1; i++)
			for (int j = col - 1; j <= col + 1; j++)
				if ((i >= 0 && i <= grid.length-1) && (j >= 0 && j <= grid[i].length-1) && !(i == row && j == col) && isMine(i, j))
					cnt ++;
		return cnt;
	}

	@Override
	public boolean isMine(int row, int col) {
		return grid[row][col].state == Cell.MINE;
	}

	@Override
	public boolean isFlagged(int row, int col) {
		return grid[row][col].flagged;
	}

	@Override
	public void setFlagged(int row, int col, boolean flagged) {
		grid[row][col].flagged = flagged;
		flags--;
	}

	@Override
	public void setQuestionMarked(int row, int col, boolean questionMarked) {
		grid[row][col].questionMarked = questionMarked;
		flags++;
	}

	@Override
	public void make(int numMines, int rloc, int cloc) {
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				setStateAt(i, j, Cell.BLANK);

		Random rnd = new Random (System.currentTimeMillis());
		int cnt = 0;
		while (cnt++ < numMines)
		{
			int row = rnd.nextInt(grid.length);
			int col = rnd.nextInt(grid[0].length);
			while (isMine(row, col) || ((row == rloc && col == cloc) || (row == rloc-1 && col == cloc) 
					|| (row == rloc-1 && col == cloc+1) || (row == rloc && col == cloc+1)
					|| (row == rloc+1 && col == cloc+1) || (row == rloc+1 && col == cloc)
					|| (row == rloc+1 && col == cloc-1) || (row == rloc && col == cloc-1)))
			{
				row = rnd.nextInt(grid.length);
				col = rnd.nextInt(grid[0].length);
			}
			grid[row][col].state = Cell.MINE;
		}

		for (int i = 0; i < grid.length; i++)
		{
			for (int j = 0; j < grid[i].length; j++)
			{
				if (isMine (i, j))
					continue;
				int nei_mine = getNeighbouringMines (i, j);
				if (nei_mine >= 1 && nei_mine <= 8)
					setStateAt (i, j, nei_mine);
			}
		}

		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
			{
				setRevealed(i, j, false);
				setFlagged (i, j, false);
				setQuestionMarked(i, j, false);
			}

		lose = false;
		flags = numMines;
	}

	@Override
	public void reveal(int row, int col) {
		if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length);
		else if (!isRevealed (row, col))
		{   
			setRevealed (row, col, true);
			if (isMine (row, col))
				lose = true;
			else if (grid[row][col].state == Cell.BLANK)
			{
				reveal (row-1, col);
				reveal (row-1, col+1);
				reveal (row, col+1);
				reveal (row+1, col+1);
				reveal (row+1, col);
				reveal (row+1, col-1);
				reveal (row, col-1);
				reveal (row-1, col-1);
			}
		}
	}

	@Override
	public boolean isGameWon() {
		boolean nonmines_rev = true;
		for (int i = 0; i < grid.length; i++)
		{
			for (int j = 0; j < grid[i].length; j++)
			{
				if ((!isMine(i, j) && !isRevealed(i, j)) || (isMine(i, j) && isRevealed(i,j)))
					nonmines_rev = false;
			}
		}

		return nonmines_rev;
	}

	public boolean isGameLost ()
	{
		return lose;
	}

	@Override
	public int getFlags() {
		int cnt = mines;
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				if (isFlagged(i, j))
					cnt --;
		return cnt;
	}

	@Override
	public void setMine(int row, int col) {
		grid[row][col].state = Cell.MINE;
	}

	@Override
	public boolean isQuestionMarked(int row, int col) {
		return grid[row][col].questionMarked;
	}
}