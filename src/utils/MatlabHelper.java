package utils;

import com.mathworks.engine.MatlabEngine;

public class MatlabHelper {
    MatlabEngine eng;
    public MatlabHelper() {
        try {
            eng = MatlabEngine.startMatlab();
        }catch (Exception e)
        {
            System.out.println("matlab启动失败");
        }
    }
    public MatlabHelper(String name)
    {
        System.out.println("正在连接matlab会话"+name);
        try {
            if(name!=null) {
                eng = MatlabEngine.connectMatlab(name);
            }
            else
            {
                eng = MatlabEngine.connectMatlab();
            }
            System.out.println("连接到会话成功");

        }catch (Exception e)
        {
            System.out.println("matlab连接失败");
        }
    }

    public void execute(String command)
    {
        try {
            eng.eval(command);
        }catch (Exception e)
        {
            System.out.println("matlab执行失败");
        }
    }
}
