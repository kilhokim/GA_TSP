import kim.kilho.ga.gene.Path;
import kim.kilho.ga.io.file.FileManager;
import kim.kilho.ga.util.PointUtils;

public class Main {

    /*
    // static void Input(String fileName) {  }

    static void Input() {  }

    static void Initialize() {  }

    static int Selection(int selectionMethod) { return -1; }

    static int Roulette() {  return -1; }

    static int Tournament() { return -1; }

    static void Crossover(int p1, int p2, int crossoverMethod) {  }

    static void ER(int p1, int p2) {  }

    static void OX(int p1, int p2) {  }

    static void Mutation(int mutationMethod) {  }

    static void DM() {  }

    static void IVM() { }

    static void SIM() {  }

    static void Evaluation() { }

    static void Replacement(int p1, int p2) {}

    static void GA(int selectionMethod, int crossoverMethod, int mutationMethod) {  }

    static void Output() {}
    */

    public static final int MAXN = 318; // Maximum value of N
    public static final int PSIZE = 100;  // Size of the population

    // Population of solutions
    Path[] population = new Path[PSIZE];
    // Best (found) solution, eval() updates this.
    Path record;


    public static void main(String[] args) {
        FileManager fm = new FileManager();
        try {
            Path path = fm.read(args[0], MAXN);
            System.out.println("Total length=" + path.getLength());
            for (int i = 0; i < path.getLength(); i++) {
                System.out.println(path.get(i).toString());
            }
            System.out.println("Total available time=" + path.getAvailableTime());
        } catch (Exception e) {
            e.getMessage();
        }

    }
}

