import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * TungHo Lin
 * txl429
 * EECS391 PA1
 * class EightPuzzleSolver
 */

public class EightPuzzleSolver {


    //storing the current puzzle state
    EightPuzzle currentPuzzle;

    //storing the maximum number of nodes generated
    private int maxNodes = 99999;

    //node limit for beam search
    private int nodeLimit = 1000000;

    //storing the number of nodes processed
    int nodeCtr = 0;

    //storing the goal puzzle state
    private static final EightPuzzle goalState = createGoalState();
    private static EightPuzzle createGoalState() {
        try {
            return new EightPuzzle("b12 345 678");
        }
        catch (Exception e) {
            throw new Error("Goal state is not initialized correctly, please check!");
        }
    }

    /**
     * Main method that will parse the commands from a text file given the file path and run the commands
     * @param args the file path
     * @throws Exception if any input is invalid
     */
    public static void main(String[] args) throws Exception {
        if(args.length < 1) {
            System.err.println("Please input the file path!");
            return;
        }
        String filepath = args[0];
        File file = new File(filepath);
        try {
            Scanner sc = new Scanner(file);
            EightPuzzleSolver solver = new EightPuzzleSolver();
            while (sc.hasNextLine()) {
                //retrieving the method of the current line of command
                String method = sc.next();
                System.out.print(method + " ");
                switch (method) {
                    case("setState"):
                        String temp = sc.nextLine();
                        checkParamExist(temp);
                        //retrieving the parameters and trimming the front space
                        String combination = temp.substring(1,temp.length());
                        System.out.println(combination);
                        solver.setState(combination);
                        break;
                    case("randomizeState") :
                        String moves_str = sc.nextLine().replaceAll("\\s", "");
                        checkParamExist(moves_str);
                        int moves = Integer.parseInt(moves_str);
                        System.out.println(moves);
                        solver.randomizeState(moves);
                        break;
                    case("printState") :
                        System.out.print("\n");
                        solver.printState();
                        break;
                    case("move") :
                        String direction = sc.nextLine().replaceAll("\\s", "");
                        checkParamExist(direction);
                        System.out.println(direction);
                        solver.move(direction);
                        break;
                    case("solve") :
                        String solveMethod = sc.next();
                        if(solveMethod.equals("A-star")) {
                            String heuristic = sc.nextLine().replaceAll("\\s", "");
                            checkParamExist(heuristic);
                            System.out.println(solveMethod + " " + heuristic);
                            solver.solve_Astar(heuristic);
                        }
                        else if(solveMethod.equals("beam")) {
                            String k_limit_str = sc.nextLine().replaceAll("\\s", "");
                            checkParamExist(k_limit_str);
                            int k_limit = Integer.parseInt(k_limit_str);
                            System.out.println(solveMethod + " " + k_limit_str);
                            solver.solve_beam(k_limit);
                        }
                        else {
                            System.out.print("\n");
                            System.err.println("Invalid solve methods");
                        }
                        break;
                    case("maxNodes") :
                        String maxNodes_str = sc.nextLine().replaceAll("\\s", "");
                        checkParamExist(maxNodes_str);
                        int maxNodes = Integer.parseInt(maxNodes_str);
                        System.out.println(maxNodes);
                        solver.maxNodes(maxNodes);
                        break;
                    default:
                        System.out.print("\n");
                        System.err.println("No matching command found!" + method);
                        break;
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
        }
    }

    /**
     * Checks if the parameter is given for each method in the test text file
     * @param param parameter of each method
     * @throws Exception if there is not parameter found
     */
    private static void checkParamExist(String param) throws Exception {
        if(param.replaceAll("\\s", "").equals("")) {
            throw new Exception("There is no parameter for this method!");
        }
    }

    /**
     * Set the puzzle state.
     * @param combination a String representation of the puzzle in the form of "XXX XbX XXX" where "b" is the blank tile
     */
    public void setState(String combination) throws Exception {
        this.currentPuzzle = new EightPuzzle(combination);
    }

    /**
     * Make n random moves from the goal state.
     * @param n n random moves
     * @throws Exception when the EightPuzzle has invalid input/when the direction is invalid
     */
    public void randomizeState(int n) throws Exception {
        Random rand = new Random(50);
        this.currentPuzzle = new EightPuzzle(goalState.toStringRep());
        List<String> dir = possibleMoves();
        for(int i=0; i<n; i++) {
            String direction = dir.get(rand.nextInt(dir.size()));
            move(direction);
            dir = possibleMoves();
        }
    }

    /**
     * return a list of directions that a certain puzzle state can move towards.
     * @return a list of String that represents all the possible moves a specific puzzle state can move towards
     */
    private List<String> possibleMoves() {
        List<String> directions = new ArrayList<>();
        int i = -1;
        int j = -1;
        //finding the blank tile's i and j coordinates
        for (int x=0; x<3; x++) {
            for(int y=0; y<3; y++) {
                if(currentPuzzle.board[x][y].equals("b")) {
                    i = x;
                    j = y;
                }
            }
        }
        if(i >= 1)
            directions.add("up");
        if(i <= 1)
            directions.add("down");
        if(j >= 1)
            directions.add("left");
        if(j <= 1)
            directions.add("right");
        return directions;
    }

    /**
     * Print the current puzzle state.
     */
    public void printState() {
        String str = currentPuzzle.toStringRep();
        System.out.println(str.split(" ")[0].replaceAll("b", " "));
        System.out.println(str.split(" ")[1].replaceAll("b", " "));
        System.out.println(str.split(" ")[2].replaceAll("b", " "));
    }

    /**
     * Move the blank tile 'up', 'down', 'left', or 'right'.
     * @param dir directions represented as String
     * @throws Exception when the input direction is invalid
     */
    public void move(String dir) throws Exception {
        int i = -1;
        int j = -1;

        String[][] currentBoard = currentPuzzle.board;

        //finding the blank tile in the puzzle
        for (int x=0; x<3; x++) {
            for(int y=0; y<3; y++) {
                if(currentBoard[x][y].equals("b")) {
                    i = x;
                    j = y;
                }
            }
        }
        //switch statement for each direction
        switch (dir) {
            case "up":
                i-=1;
                //check if
                if(i >= 0) {
                    String temp = currentBoard[i][j];
                    currentBoard[i][j] = "b";
                    currentBoard[i+1][j] = temp;
                    break;
                }
            case "down":
                i+=1;
                if(i <= 2) {
                    String temp = currentBoard[i][j];
                    currentBoard[i][j] = "b";
                    currentBoard[i-1][j] = temp;
                    break;
                }
            case "left":
                j-=1;
                if(j >= 0) {
                    String temp = currentBoard[i][j];
                    currentBoard[i][j] = "b";
                    currentBoard[i][j+1] = temp;
                    break;
                }
            case "right":
                j+=1;
                if(j <= 2) {
                    String temp = currentBoard[i][j];
                    currentBoard[i][j] = "b";
                    currentBoard[i][j-1] = temp;
                    break;
                }
            default:
                throw new Exception("Invalid movement!");
        }
    }

    /**
     * Solve the puzzle from its current state using A-star search with h1 or h2
     * @param heuristic the heuristic function that A* is going to use, for here it's either "h1" or "h2"
     * @throws Exception if the direction is invalid
     */
    public void solve_Astar (String heuristic) throws Exception {
        //a counter that counts the number of already-processed nodes
        nodeCtr = 1;
        List<EightPuzzle> openList = new ArrayList<>();
        List<EightPuzzle> closedList = new ArrayList<>();
        openList.add(this.currentPuzzle);
        while(!openList.isEmpty()) {
            //pop the first node and assign it to be the current puzzle
            this.currentPuzzle = openList.remove(0);

            //check if the number of processed nodes exceeds the limit
            if (nodeCtr > this.maxNodes) {
                System.err.println("The system has exceeded the maximum number of nodes to be considered!");
                return;
            }

            //if a solution is found, print the solution
            if (isComplete()) {
                printSolution();
                return;
            }

            //generate all the possible successors from the current puzzle
            List<EightPuzzle> possibleSuccessors = generatePossibleSuccessors();
            //for each successor, assign their field values
            for (EightPuzzle successor : possibleSuccessors) {
                nodeCtr++;
                successor.g_value = currentPuzzle.g_value + 1;
                successor.parent = currentPuzzle;
                successor.h_value = calcHeuristic(successor, heuristic);
                //calculate f(n) = g(n) + h(n)
                successor.f_value = successor.g_value + successor.h_value;

                //if the successor does not exist in openList and closedList, add the successor to openList
                if (indexOfSuccessor(openList, successor) == -1 && indexOfSuccessor(closedList, successor) == -1) {
                    openList.add(successor);
                }

                //if the successor exists in openList, compare their f value
                else if (indexOfSuccessor(openList, successor) != -1) {
                    EightPuzzle identical = openList.get(indexOfSuccessor(openList, successor));
                    //if successor has a better f value then the already-existed puzzle state, copy the field values over
                    if (successor.f_value < identical.f_value) {
                        identical.g_value = successor.g_value;
                        identical.parent = successor.parent;
                        identical.previousMove = successor.previousMove;
                    }
                }

                //if the successor exists in closedList
                else if (indexOfSuccessor(closedList, successor) != -1) {
                    EightPuzzle identical = closedList.get(indexOfSuccessor(closedList, successor));
                    //if successor has a better f value then the already-existed puzzle state, add the successor to the openList
                    //and remove the already-existed puzzle state
                    if (identical.toStringRep().equals(successor.toStringRep())) {
                        if (successor.f_value < identical.f_value) {
                            openList.add(successor);
                            closedList.remove(identical);
                        }
                    }
                }
            }
            //move the current puzzle to the closedList
            closedList.add(currentPuzzle);

            //sort the newly-created list
            openList.sort((o1, o2) -> o1.f_value - o2.f_value);
        }
        //if there are no more successors
        System.err.println("The given state is unsolvable!");
    }

    /**
     * Tracing back from the current puzzle and printing the solution.
     */
    private void printSolution() {
        List<String> moves = new ArrayList<>();
        EightPuzzle curNode = currentPuzzle;
        while(curNode.parent != null) {
            moves.add(0, curNode.previousMove);
            curNode = curNode.parent;
        }
        System.out.println("Solved!");
        System.out.println("Number of Steps: " + currentPuzzle.g_value);
        System.out.println(moves);
    }

    /**
     * search for the successor in the given list and return the index of it.
     * @param list list of EightPuzzle
     * @param successor a specific EightPuzzle
     * @return index of the successor if found; -1 if not found.
     */
    private int indexOfSuccessor(List<EightPuzzle> list, EightPuzzle successor) {
        for(EightPuzzle puzzle : list) {
            if(puzzle.toStringRep().equals(successor.toStringRep()))
                return list.indexOf(puzzle);
        }
        //if not found, return -1
        return -1;
    }

    /**
     * Calculate the heuristic values based on h1 or h2 function
     * @param puzzle a given puzzle state
     * @param heuristic a given heuristic function
     * @return heuristic value of the given puzzle state; -1 if the function name is invalid
     */
    private int calcHeuristic(EightPuzzle puzzle, String heuristic) {
        if (heuristic.equals("h1")) {
            return calcH1(puzzle);
        }
        else if(heuristic.equals("h2")) {
            return calcH2(puzzle);
        }
        else {
            System.err.println("No such heuristic function!");
            return -1;
        }
    }

    /**
     * Calculating the heuristic values using h1 functions: number of misplaced tiles
     * and assigning it to the given puzzle's field
     * @param puzzle a given puzzle state
     * @return the heuristic value
     */
    private int calcH1(EightPuzzle puzzle) {
        int ctr = 0;
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                if(!(puzzle.board[i][j].equals("b")) && !(puzzle.board[i][j].equals(goalState.board[i][j]))) {
                    ctr += 1;
                }
            }
        }
        puzzle.h_value = ctr;
        return ctr;
    }

    /**
     * Calculating the heuristic values using h2 functions: sum of the distances of the tiles from their goal positions (aka Manhattan)
     * and assigning it to the given puzzle's field
     * @param puzzle a given puzzle state
     * @return the heuristic value
     */
    private int calcH2(EightPuzzle puzzle) {
        int value = 0;
        for(int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                if (!(puzzle.board[i][j].equals("b"))) {
                    int xDiff = Integer.parseInt(puzzle.board[i][j]) / 3;
                    int yDiff = Integer.parseInt(puzzle.board[i][j]) % 3;
                    value += Math.abs(i - xDiff) + Math.abs(j - yDiff);
                }
            }
        }
        puzzle.h_value = value;
        return value;
    }

    /**
     * Solve the puzzle from its current state using using local beam search with k states.
     * @param k limitation of k states
     * @throws Exception if the direction is invalid
     */
    public void solve_beam (int k) throws Exception {
        //check if the given state is already solved
        if (isComplete()) {
            System.out.println("The given state is already solved!");
            return;
        }
        nodeCtr = 1;
        List<EightPuzzle> openList = new ArrayList<>();
        openList.add(this.currentPuzzle);
        //although local beam search is not limited by maxNodes, nodeLimit acts as a upper limit because the solution
        //might not be found and the program has to terminate then
        while(nodeCtr < nodeLimit) {
            List<EightPuzzle> allNewSuccessors = new ArrayList<>();
            //while the openList still has nodes
            while(!openList.isEmpty()) {
                //pop the first one
                this.currentPuzzle = openList.remove(0);
                //generate all possible successors off the current puzzle
                List<EightPuzzle> temp = generatePossibleSuccessors();
                //for each successor, assign its field values
                for (EightPuzzle successor : temp) {
                    successor.g_value = currentPuzzle.g_value + 1;
                    //using Manhattan distance as the heuristic
                    successor.h_value = calcH2(successor);
                    successor.parent = currentPuzzle;
                    successor.f_value = successor.g_value + successor.h_value;
                    //if one of the successor has reached the goal state, halt the search and output solution
                    if (successor.equals(goalState)) {
                        currentPuzzle = successor;
                        printSolution();
                        return;
                    }
                    //if a successor already exists in the new successors list, check their f values
                    if(indexOfSuccessor(allNewSuccessors, successor) != -1) {
                        EightPuzzle identical = allNewSuccessors.get(indexOfSuccessor(allNewSuccessors, successor));
                        //if the new successor has a better f value, copy the field values to the old puzzle state
                        if(successor.f_value < identical.f_value) {
                            identical.g_value = successor.g_value;
                            identical.parent = successor.parent;
                            identical.previousMove = successor.previousMove;
                        }
                    }
                    //if the successor does not exist, add it to the new successor list
                    else {
                        allNewSuccessors.add(successor);
                        nodeCtr++;
                    }
                }
            }

            //sorting the successor list according to their f values
            allNewSuccessors.sort((o1, o2) -> o1.f_value - o2.f_value);
            List<EightPuzzle> k_bestList = allNewSuccessors;

            //selecting k-best successors if the list size is larger than k
            if(k_bestList.size() > k) {
                k_bestList = k_bestList.subList(0, k);
            }
            //assign the k-best list back to the openList
            openList = k_bestList;
        }
        System.err.println("Solution not found!");
    }

    /**
     * Specifies the maximum number of nodes to be considered during a search.
     * @param n maximum number of nodes to be considered during a search.
     */
    public void maxNodes (int n) {
        this.maxNodes = n;
    }

    /**
     * Returns if the goal state is reached
     * @return if the goal state is reached
     */
    private boolean isComplete() {
        return currentPuzzle.equals(goalState);
    }

    /**
     * Generate all the possible successors from the current puzzle
     * @return a list of all possible successors generated from the current puzzle
     * @throws Exception if the direction is invalid
     */
    private List<EightPuzzle> generatePossibleSuccessors() throws Exception {
        List<EightPuzzle> possibleSuccessors = new ArrayList<>();
        List<String> directions = possibleMoves();
        for (String dir : directions) {
            possibleSuccessors.add(generateMovedPuzzle(dir));
        }
        return possibleSuccessors;
    }

    /**
     * Generating a new EightPuzzle from the current puzzle by moving towards the input direction
     * @param dir the direction to move towards
     * @return a new EightPuzzle with the blank tile moved
     * @throws Exception if the direction is invalid
     */
    private EightPuzzle generateMovedPuzzle(String dir) throws Exception {
        int i = -1;
        int j = -1;

        String[][] newBoard = new String[3][3];
        //deepcopy of the 2d array
        for(int r=0; r<3;r++) {
            newBoard[r] = currentPuzzle.board[r].clone();
        }

        //finding the blank tile in the puzzle
        for (int x=0; x<3; x++) {
            for(int y=0; y<3; y++) {
                if(newBoard[x][y].equals("b")) {
                    i = x;
                    j = y;
                }
            }
        }
        //switch statement for each direction
        switch (dir.toLowerCase()) {
            case "up":
                i-=1;
                //check if
                if(i >= 0) {
                    String temp = newBoard[i][j];
                    newBoard[i][j] = "b";
                    newBoard[i+1][j] = temp;
                    EightPuzzle newPuzzle = new EightPuzzle(newBoard);
                    //set the new puzzle's previous move to be "up", same for the rest of the directions
                    newPuzzle.previousMove = "up";
                    return newPuzzle;
                }

            case "down":
                i+=1;
                if(i <= 2) {
                    String temp = newBoard[i][j];
                    newBoard[i][j] = "b";
                    newBoard[i-1][j] = temp;
                    EightPuzzle newPuzzle = new EightPuzzle(newBoard);
                    newPuzzle.previousMove = "down";
                    return newPuzzle;
                }

            case "left":
                j-=1;
                if(j >= 0) {
                    String temp = newBoard[i][j];
                    newBoard[i][j] = "b";
                    newBoard[i][j+1] = temp;
                    EightPuzzle newPuzzle = new EightPuzzle(newBoard);
                    newPuzzle.previousMove = "left";
                    return newPuzzle;
                }

            case "right":
                j+=1;
                if(j <= 2) {
                    String temp = newBoard[i][j];
                    newBoard[i][j] = "b";
                    newBoard[i][j-1] = temp;
                    EightPuzzle newPuzzle = new EightPuzzle(newBoard);
                    newPuzzle.previousMove = "right";
                    return newPuzzle;
                }
            default:
                throw new Exception("Invalid movement!");
        }
    }

}
