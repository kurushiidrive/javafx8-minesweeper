import java.util.Scanner;
import java.util.Random;

/*
 * Godfried Boateng; 16 March 2017
 */

public class MinesweeperConsoleController {
    MinesweeperModel mm;
    private Scanner input;
    private boolean quit, completeFirstClick;
    private Random rnd;

    public MinesweeperConsoleController (int numrows, int numcols, int nummines)
    {
        mm = new MinesweeperModel (numrows, numcols, nummines);
        input = new Scanner (System.in);
        quit = false;
        completeFirstClick = false;
        rnd = new Random (System.currentTimeMillis());
    }

    public void run ()
    {
        while (!quit)
        {
            printGame ();

            if (mm.isGameWon()) {
                System.out.println ("Victory!!\n");
                System.out.println ("Would you like to replay? (Enter 'y' or 'n')");
                String yOrN = input.next();
                while (!yOrN.equals("y") && !yOrN.equals("n"))
                {
                    System.out.println ("Invalid input. Please retry.\n");
                    System.out.println ("Would you like to replay? (Enter 'y' or 'n')");
                    yOrN = input.next ();
                }
                if (yOrN.equals ("y"))
                {
                    mm.make (mm.getNumMines(), (new Random()).nextInt(mm.getNumRows()), (new Random()).nextInt(mm.getNumCols()));
                    completeFirstClick = false; 
                    continue;
                }
                else if (yOrN.equals ("n"))
                { 
                    System.out.println ("Farewell. Thanks for playing.");
                    break;
                }
            }
            if (mm.isGameLost())
            {
                System.out.println ("Sorry, you have lost. :\'c\n");
                System.out.println ("Would you like to replay? (Enter 'y' or 'n')");
                String yOrN = input.next();
                while (!yOrN.equals("y") && !yOrN.equals("n"))
                {
                    System.out.println ("Invalid input. Please retry.\n");
                    System.out.println ("Would you like to replay? (Enter 'y' or 'n')");
                    yOrN = input.next ();
                }
                if (yOrN.equals ("y"))
                {
                	mm.make (mm.getNumMines(), (new Random()).nextInt(mm.getNumRows()), (new Random()).nextInt(mm.getNumCols()));
                    completeFirstClick = false;
                    continue;
                }
                else if (yOrN.equals ("n"))
                {   
                    System.out.println ("Farewell. Thanks for playing.");
                    break;
                }
            }
            
            move ();
        }
    }

    public void move ()
    {
        System.out.print("Enter the type of move you would like to make\n"
            + "(\'r\' for reveal, \'f\' for flag, \'q\' for quit): ");
        String move = input.next(); move = move.toLowerCase();
        while (!move.equals("r") && !move.equals("f") && !move.equals("q"))
        {
            System.out.println("Invalid move. Try again.\n");
            System.out.print("Enter the type of move you would like to make\n"
                + "(\'r\' for reveal, \'f\' for flag, \'q\' for quit): ");
            move = input.next();
        }
        if (move.equals ("q"))
        {
            quit = true; 
            System.out.println ("Farewell. Thanks for playing.\n");
            return;
        }

        System.out.print("Enter the row number: ");
        int row = input.nextInt();
        System.out.print("Enter the column number: ");
        int col = input.nextInt();

        while (row < 0 || row >= mm.getNumRows() || col < 0 || col >= mm.getNumCols()) {
            System.out.println ("Location out of boundaries. Try again.\n");
            System.out.print("Enter the row number: ");
            row = input.nextInt();
            System.out.print("Enter the column number: ");
            col = input.nextInt();
        }

        if (move.equals("r")){
            if (!completeFirstClick)
                if (mm.getStateAt(row, col) != MinesweeperModel.Cell.BLANK)
                {
                    mm.make(mm.getNumMines(), row, col);
                    completeFirstClick = true;
                }
            mm.reveal (row, col);
            return;
        }

        if (move.equals("f")) {
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
    }

    public void printGame ()
    {
        System.out.println("");

        System.out.println("What the player sees:\n");
        printPlayerSide ();

        System.out.println();

        System.out.println("What is under the board:\n");
        printUnderSide ();

        System.out.println ("There are " + mm.getFlags() + " flags left.\n");
    }

    private void printUnderSide() {
        System.out.print("  ");
        for (int i = 0; i < mm.getNumRows(); i++)
            System.out.print(i + " ");

        System.out.println();

        for (int i = 0; i < mm.getNumRows(); i++)
        {
            System.out.print(i + " ");
            for (int j = 0; j < mm.getNumCols(); j++)
            {
                if (mm.getStateAt(i, j) >= 1 && mm.getStateAt(i, j) <= 8)
                    System.out.print("" + mm.getStateAt(i, j));
                else if (mm.getStateAt(i, j) == 9)
                    System.out.print("*");
                else if (mm.getStateAt(i, j) == 0)
                    System.out.print(" ");

                if (j == mm.getNumCols() - 1)
                    System.out.println();
                else
                    System.out.print(" ");
            }
        }       
    }

    public void printPlayerSide ()
    {
        System.out.print("  ");
        for (int i = 0; i < mm.getNumRows(); i++)
            System.out.print(i + " ");

        System.out.println();

        for (int i = 0; i < mm.getNumRows(); i++)
        {
            System.out.print(i + " ");
            for (int j = 0; j < mm.getNumCols(); j++)
            {
                if (!mm.isRevealed(i, j))
                {
                    if (mm.isFlagged(i, j))
                        System.out.print("!");
                    else if (mm.isQuestionMarked(i, j))
                        System.out.print("?");
                    else
                        System.out.print("-");
                }
                else
                {
                    if (mm.getStateAt(i, j) >= 1 && mm.getStateAt(i, j) <= 8)
                        System.out.print("" + mm.getStateAt(i, j));
                    else if (mm.getStateAt(i, j) == 9)
                        System.out.print("*");
                    else if (mm.getStateAt(i, j) == 0)
                        System.out.print(" ");
                }
                if (j == mm.getNumCols() - 1)
                    System.out.println();
                else
                    System.out.print(" ");
            }
        }
    }
}