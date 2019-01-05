import java.util.Arrays;

public class EightPuzzleTester {

    public static void main(String[] args) throws Exception{
        testSearch("AStar_H1", -1);
        testSearch("AStar_H2", -1);
        testSearch("Beam", 5);
        testSearch("Beam", 30);
        testSearch("Beam", 100);
//        beamSearchKTest();
    }

    private static void testSearch(String method, int k) throws Exception {
        EightPuzzleSolver solver = new EightPuzzleSolver();
        int[] nodes = new int[15];
        int[] steps = new int[15];
        int index = 0;
        for(int i=10; i<=150; i+=10) {
            solver.setState("b12 345 678");
            solver.randomizeState(i);
            System.out.println("~~~~n = " + i + "~~~~");
            if(method.equals("AStar_H1")) {
                solver.solve_Astar("h1");
            }
            else if(method.equals("AStar_H2")) {
                solver.solve_Astar("h2");
            }
            else if(method.equals("Beam")) {
                solver.solve_beam(k);
            }
            else {
                System.err.println("No such search methods");
                return;
            }
            nodes[index] = solver.nodeCtr;
            System.out.println("Nodes: " + solver.nodeCtr);
            steps[index] = solver.currentPuzzle.g_value;
            index++;
        }
        System.out.println("Steps: ");
        System.out.println(Arrays.toString(steps));
        System.out.println("Nodes: ");
        System.out.println(Arrays.toString(nodes));
    }

    //run beam search for k=3, 30, 100 for d=2-24 for 50 times on each even number
    private static void beamSearchKTest() throws Exception {
        EightPuzzleSolver solver = new EightPuzzleSolver();
        int[][] k10values = new int[9][50];
        int[][] k30values = new int[9][50];
        int[][] k50values = new int[9][50];
        //run from d=2 to d=18
        int index = 0;
        for(int i=2; i<=18; i+=2) {
            System.out.println("~~~~~~d = " + i + "~~~~~~");
            //for each d value, run it 10 times
            for (int j=0; j<50; j++) {
                solver.randomizeState(i);
                solver.solve_beam(3);
                k10values[index][j] = solver.nodeCtr;
            }
            for (int j=0; j<50; j++) {
                solver.randomizeState(i);
                solver.solve_beam(30);
                k30values[index][j] = solver.nodeCtr;
            }
            for (int j=0; j<50; j++) {
                solver.randomizeState(i);
                solver.solve_beam(100);
                k50values[index][j] = solver.nodeCtr;
            }
            index++;
        }
        int[] k10average = new int[9];
        int[] k30average = new int[9];
        int[] k50average = new int[9];
        for(int i=0; i<9; i++) {
            k10average[i] = average(k10values[i]);
            k30average[i] = average(k30values[i]);
            k50average[i] = average(k50values[i]);
        }
        System.out.println(Arrays.toString(k10average));
        System.out.println(Arrays.toString(k30average));
        System.out.println(Arrays.toString(k50average));
    }

    private static int average(int[] data) {
        int sum = 0;
        for (int d : data)
            sum += d;
        return sum / data.length;
    }
}
