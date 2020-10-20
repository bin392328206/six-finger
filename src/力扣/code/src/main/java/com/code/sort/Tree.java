package com.code.sort;



/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/20 10:40
 *
 * 二叉树的前序遍历
 */
public class Tree {

    public static void main(String[] args) {

        TreeNode[] node = new TreeNode[10];//以数组形式生成一棵完全二叉树
        for (int i = 0; i < 10; i++) {
            node[i] = new TreeNode(i);
        }

        for(int i = 0; i < 10; i++) {
            if(i*2+1 < 10){
                node[i].left = node[i*2+1];
            }
            if(i*2+2 < 10){
                node[i].right = node[i*2+2];
            }
        }

        //前序遍历
        preOrderRe(node[0]);

    }

    private static void preOrderRe(TreeNode biTree) {
            //递归的实现
        System.out.println(biTree.value);

        TreeNode leftTree = biTree.left;
        if(leftTree != null) {
            preOrderRe(leftTree);}

        TreeNode rightTree = biTree.right;
        if(rightTree != null) {
            preOrderRe(rightTree);
        }



        }

}
