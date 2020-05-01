package com.code.LinkedList;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/26 21:10
 * <p>
 * 将两个升序链表合并为一个新的升序链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。 
 * <p>
 * 示例：
 * <p>
 * 输入：1->2->4, 1->3->4
 * 输出：1->1->2->3->4->4
 * <p>
 * 其实思路还是蛮简单的，就是2个单向链表，每次去比大小，取一个小的，添加到我们的结果链表中，然后一直到最后，完成合并
 */
public class MergeTwoLists {

    //这边定义的是链表的Node
    static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        //定一个全局node;
        ListNode allNode = new ListNode(0);
        //用来移动的node
        ListNode temp = allNode;
        //如果l1 或者l2其中一个不为null
        while (l1 != null && l2 != null) {
            //比较大小
            if (l1.val > l2.val) {
                temp.next = new ListNode(l2.val);
                //自身后移
                l2 = l2.next;
            } else {
                temp.next = new ListNode(l1.val);
                l1 = l1.next;
            }
            temp = temp.next;
        }
        if (null == l1) {
            temp.next = l2;
        }
        if (null == l2) {
            temp.next = l1;
        }
        return allNode.next;
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
       ListNode l1=new ListNode(1);
       l1.next=new ListNode(3);
       l1.next.next=new ListNode(5);
        ListNode l2 = new ListNode(1);
        l2.next=new ListNode(2);
        l2.next.next=new ListNode(3);
        ListNode lists = mergeTwoLists(l1, l2);
        System.out.println(print(lists));
    }
}
