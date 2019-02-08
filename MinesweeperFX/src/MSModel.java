public interface MSModel {
	public boolean isRevealed (int row, int col);
	public void setRevealed (int row, int col, boolean rev);
	public int getNumMines ();
	public int getNumRows ();
	public int getNumCols ();
	public int getNeighbouringMines (int row, int col);
	public boolean isMine (int row, int col);
	public boolean isFlagged (int row, int col);
	public void setFlagged (int row, int col, boolean flagged);
	public boolean isQuestionMarked (int row, int col);
	public void setQuestionMarked (int row, int col, boolean questionMarked);
	public void make (int numMines, int row, int col);
	public void reveal (int row, int col);
	public boolean isGameWon ();
	public int getFlags ();
	public void setMine (int row, int col);
}