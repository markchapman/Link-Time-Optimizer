package optimizer.instrument;

public class App {

    public static void main(String[] args) {
        method1();
        Lib.method1();
        System.out.println("Work complete.");
    }

    @Cost(500)
    public static void method1() {
    }

}
