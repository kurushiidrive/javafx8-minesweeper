public class MinesweeperController {
	MinesweeperModel mm;
	private boolean completeFirstClick;
	private boolean stopped_lose;
	private boolean stopped_win;

	public MinesweeperController (MinesweeperModel mm)
	{
		this.mm = mm;
		completeFirstClick = false;
		stopped_lose = false;
		stopped_win = false;
	}

	public void beg_reset ()
	{
		mm = new MinesweeperModel (8, 8, 10);
		completeFirstClick = false;
		stopped_lose = false;
		stopped_win = false;
	}

	public void inter_reset ()
	{
		mm = new MinesweeperModel (16, 16, 40);
		completeFirstClick = false;
		stopped_lose = false;
		stopped_win = false;
	}

	public void expert_reset ()
	{
		mm = new MinesweeperModel (16, 31, 99);
		completeFirstClick = false;
		stopped_lose = false;
		stopped_win = false;	
	}

	public void custom_reset (int rows, int cols, int numMines)
	{
		mm = new MinesweeperModel (rows, cols, numMines);
		completeFirstClick = false;
		stopped_lose = false;
		stopped_win = false;
	}

	public void mine_reset (int numMines)
	{
		mm = new MinesweeperModel (mm.getNumRows(), mm.getNumCols(), numMines);
		completeFirstClick = false;
		stopped_lose = false;
		stopped_win = false;
	}

	public void size_reset (int rows, int cols)
	{
		mm = new MinesweeperModel (rows, cols, mm.getNumMines());
		completeFirstClick = false;
		stopped_lose = false;
		stopped_win = false;
	}

	public MinesweeperModel getModel ()
	{
		return mm;
	}

	public void setModel (MinesweeperModel mm)
	{
		this.mm = mm;
		completeFirstClick = false;
		stopped_lose = false;
		stopped_win = false;
	}

	public void action (int row, int col, boolean primaryClick)
	{
		if (!stopped_lose && !stopped_win)
		{
			if (primaryClick)
			{
				if (!completeFirstClick) {
					if (mm.getStateAt(row, col) != MinesweeperModel.Cell.BLANK)
					{
						mm.make (mm.getNumMines(), row, col);
						completeFirstClick = true;
					}
				}
				mm.reveal (row, col);
			}
			else
			{
				if (!mm.isRevealed(row, col)){
					if (mm.isFlagged(row, col)) {
						mm.setQuestionMarked(row, col, true);
						mm.setFlagged(row, col, false);
					}
					else if (mm.isQuestionMarked(row, col))
						mm.setQuestionMarked(row, col, false);
					else
						mm.setFlagged(row, col, true);
				}
			}

			if (mm.isGameLost()) {
				System.out.println("Lose :\'c");
				stopped_lose = true;
			}
			if (mm.isGameWon()) {
				System.out.println("Victory!!");
				stopped_win = true;
			}
		}
	}

	public boolean win ()
	{
		return mm.isGameWon();
	}

	public boolean lose ()
	{
		return mm.isGameLost();
	}

	public boolean first_click ()
	{
		return completeFirstClick;	
	}

}