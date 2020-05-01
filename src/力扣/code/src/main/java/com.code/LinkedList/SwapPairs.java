package com.code.LinkedList;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/27 20:21
 *
 *

给定一个链表，两两交换其中相邻的节点，并返回交换后的链表。

你不能只是单纯的改变节点内部的值，而是需要实际的进行节点交换。
示例:

给定 1->2->3->4, 你应该返回 2->1->4->3. */
public class SwapPairs {

    //这边定义的是链表的Node
    static class ListNode {
        int val;
       ListNode next;

        ListNode(int x) {
            val = x;
        }
    }


    public  static ListNode swapPairs(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode next = head.next;
        head.next = swapPairs(next.next);
        next.next = head;
        return next;
    }


    //非递归的写法
    public static ListNode swapPairs1(ListNode head){
        //定一个要返回的链表结构
        ListNode pre = new ListNode(0);
        pre.next=head;
        //定义一个要去移动的链表
        ListNode temp=pre;

        //while循环
        while (temp.next!=null&&temp.next.next!=null){
            //想想循环退出的条件为啥是这个。因为就是只要不是连续成一组节点，就可以直接不用把临时节点，继续往下移动了
            //定义每组的第一和第二
            ListNode start = temp.next;
            ListNode end = temp.next.next;
            temp.next = end;
            start.next = end.next;
            end.next = start;
            temp = start;

        }
        return pre.next;
    }

    //写一个打印的方法看看
    public static String print(ListNode listNode){
        StringBuffer stringBuffer=new StringBuffer();
        while (listNode!=null){
            stringBuffer.append(listNode.val);
            listNode=listNode.next;
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
        ListNode listNode=new ListNode(1);
        listNode.next=new ListNode(2);
        listNode.next.next=new ListNode(3);
        System.out.println(print(swapPairs1(listNode)));
    }

}
