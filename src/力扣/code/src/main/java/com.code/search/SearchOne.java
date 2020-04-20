package com.code.search;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/20 21:10
 *## 题目
 * 给你一个由 '1'（陆地）和 '0'（水）组成的的二维网格，请你计算网格中岛屿的数量。
 *
 * 岛屿总是被水包围，并且每座岛屿只能由水平方向和/或竖直方向上相邻的陆地连接形成。
 *
 * 此外，你可以假设该网格的四条边均被水包围。
 *
 * 示例 1:
 *
 * ```
 * 输入:
 * 11110
 * 11010
 * 11000
 * 00000
 * 输出: 1
 * ```
 * 示例 2:
 *
 * ```
 * 输入:
 * 11000
 * 11000
 * 00100
 * 00011
 * 输出: 3
 * ```
 * 解释: 每座岛屿只能由水平和/或竖直方向上相邻的陆地连接而成。
 *
 * 以上就是题目了，小六六跟大家一起来读读题，其实他的意思就是，每一个位置的前后左右能够用1连起来就是一个岛屿，所以小六六画了2个图帮大家理解题目的意思
 *
 *
 */
public class SearchOne {


    //定义打印岛屿个数的方法

    public static int printNum(char[][] grid) {
        //校验一下这个数组
        if (null==grid||grid.length==0)return 0;
        int row=grid.length;
        int column=grid[0].length;

        //定义一个变量来统计岛屿的个数
        int num=0;

        //遍历这个2维数组
        for (int i=0;i<row;i++){
            for (int j=0;j<column;j++){
                //如果等于0就继续循环
                if (grid[i][j]=='0')continue;
                //不然就进行深度搜索
                num++;
                dfs(grid,i,j);
            }
        }
        return num;
    }

    private static void dfs(char[][] grid, int row, int column) {
        int nr = grid.length;
        int nc = grid[0].length;
        //首先确定搜索的范围，超过这个四个最大范围点的，或者是深度遍历的时候本身是0，就直接不搜索了,说明到达最大深度
        if (row<0||column<0||row>=nr||column>=nc||grid[row][column]=='0') return;
        //如果不是达到最大深度，那么我们就继续搜，但是要把本身搜的点记录，也就是题目的变成0
        grid[row][column]='0';
        //然后往上下左右4个点，进行深度搜索
        dfs(grid,row-1,column);
        dfs(grid,row+1,column);
        dfs(grid,row,column-1);
        dfs(grid,row,column+1);

    }

    public static void main(String[] args) {
        //测试类
        char[][] grid= {
                {'1', '1', '0'},
                {'0','1','1'},
                {'1','0','1'}
        };
        int i = printNum(grid);
        System.out.println(i);

    }

}
