package com.code;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/23 11:24
 * 给你不同面值的硬币数组coins和总金额amount。 编写一个函数来计算您需要对amount金额的进行找零的最少数量的硬币。 如果这些金额不能由硬币的任何组合来找零，则返回'-1'。
 *例 1:
 * coins = [1, 2, 5], amount = 11
 * return 3 (11 = 5 + 5 + 1)
 */
public class CoinChange {

    public static void main(String[] args) {
        int [] a={1,2,3};
        int i = coinChange(a, 4);
    }


    public static int coinChange(int[] coins, int amount){
        if(coins.length < 1 || amount < 1) return -1;
        int[] dp = new int[amount + 1];
        for(int i = 1; i <= amount; ++i){
            dp[i] = amount;
            for(int j = 0; j < coins.length; ++j){
                if(coins[j] <= i){
                    dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1:dp[amount];
    }

}
