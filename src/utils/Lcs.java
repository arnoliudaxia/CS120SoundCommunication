package utils;

public class Lcs {
    public static String s="";
    public static void LcsLength(char[] x,char[] y){
        //初始化
        int [][]c=new int[x.length+1][y.length+1];
        int [][]Rec=new int[x.length+1][y.length+1];
        for(int i=0;i<=x.length;i++){
            c[i][0]=0;
            Rec[i][0]=0;
        }
        for(int j=0;j<=y.length;j++){
            c[0][j]=0;
            Rec[0][j]=0;
        }
        //动态规划
        for(int i=1;i<=x.length;i++) {
            for (int j = 1; j <= y.length; j++) {
                if (x[i - 1] == y[j - 1]) {
                    c[i][j] = c[i - 1][j - 1] + 1;
                    //当Rec为1时，表示Xi和Yi的最长公共子序列是由Xi-1和Yi-1的最长公共子序列在尾部加上Xi所得的子序列。
                    Rec[i][j] = 1;
                } else if (c[i - 1][j] >= c[i][j - 1]) {
                    c[i][j] = c[i - 1][j];
                    //当Rec为2时，表示Xi和Yi的最长公共子序列与Xi-1和Yi的最长公共子序列相同。
                    Rec[i][j] = 2;
                } else {
                    c[i][j] = c[i][j - 1];
                    //当Rec为3时，表示Xi和Yi的最长公共子序列与Xi和Yi-1的最长公共子序列相同。
                    Rec[i][j] = 3;
                }
            }
        }
        if(s!="")s="";
        lcs(x.length,y.length,x,Rec);
    }
    public static void lcs(int i, int j, char[] x, int[][] Rec){
        if(i==0||j==0) return;
        if(Rec[i][j]==1){
            lcs(i-1,j-1,x,Rec);
            s+=x[i-1];
        }else if(Rec[i][j]==2) lcs(i-1,j,x,Rec);
        else lcs(i,j-1,x,Rec);
    }
}
