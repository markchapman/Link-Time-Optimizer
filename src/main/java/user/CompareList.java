package user;

import java.io.IOException;

public class CompareList {

    public static void main(String[] args) throws IOException
    {
        Compare.main(new String[] {"size10.txt", "4"});
        Compare.main(new String[] {"size100.txt", "4"});
        Compare.main(new String[] {"size200.txt", "4"});
        Compare.main(new String[] {"size300.txt", "4"});
        Compare.main(new String[] {"size400.txt", "4"});
        Compare.main(new String[] {"size500.txt", "4"});
        Compare.main(new String[] {"size600.txt", "4"});
        Compare.main(new String[] {"size700.txt", "4"});
        Compare.main(new String[] {"size800.txt", "4"});
        Compare.main(new String[] {"size900.txt", "4"});
        Compare.main(new String[] {"size1000.txt", "4"});
    }

}
