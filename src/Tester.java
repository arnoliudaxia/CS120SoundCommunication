import utils.DebugHelper;

import java.io.IOException;

public class Tester {
    public static void main(final String[] args) throws IOException {
//        System.out.print("\33[31m 文字"+"\33[m\n");
        DebugHelper.logColorful("文字", DebugHelper.printColor.RED);
        DebugHelper.log("文字123");
    }
}