package com.code;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/22 11:03
 * <p>
 * 实现 int sqrt(int x) 函数。
 * <p>
 * 计算并返回 x 的平方根，其中 x 是非负整数。
 * <p>
 * 由于返回类型是整数，结果只保留整数的部分，小数部分将被舍去。
 * <p>
 * 示例 1:
 * <p>
 * 输入: 4
 * 输出: 2
 */
public class MySqrt {

    public static void main(String[] args) {

        int i = mySqrt(3);
        System.out.println(i);

        System.out.println("\b小刘\n大");
    }


    public  static  int mySqrt(int x) {
        int l = 0, r = x, ans = -1;
        while (l <= r) {
            int mid = 1 + (r - 1) / 2;
            if ((long) mid * mid <= x) {
                ans = mid;
                l = mid + 1;
            } else {
                r = mid - 1;
            }

        }

        return ans;

    }

}
