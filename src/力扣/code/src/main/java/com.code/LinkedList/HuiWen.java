package com.code.linkedList;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/16 14:48
 */
public class HuiWen {

    //单链表数据结构
    public static class LinkNode<T> {
        LinkNode<String> next;

        String data;

        public LinkNode<String> getNext() {
            return next;
        }

        public void setNext(LinkNode<String> next) {
            this.next = next;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }


    public static void main(String[] args) {
        LinkNode<String> stringLinkNode = new LinkNode<String>();
        stringLinkNode.setData("a");
        LinkNode<String> stringLinkNode1 = new LinkNode<String>();
        stringLinkNode1.setData("b");
        stringLinkNode.setNext(stringLinkNode1);
        LinkNode<String> stringLinkNode2 = new LinkNode<String>();
        stringLinkNode2.setData("c");
        stringLinkNode1.setNext(stringLinkNode2);
        LinkNode<String> stringLinkNode3 = new LinkNode<String>();
        stringLinkNode3.setData("b");
        stringLinkNode2.setNext(stringLinkNode3);
        LinkNode<String> stringLinkNode4 = new LinkNode<String>();
        stringLinkNode4.setData("a");
        stringLinkNode3.setNext(stringLinkNode4);

        Boolean huiWen = isHuiWen(stringLinkNode);
        System.out.println(huiWen);


    }

    public static Boolean isHuiWen(LinkNode head) {

        if (head == null) {
            return false;
        }
        //慢指针
        LinkNode slow = head;
        //快指针
        LinkNode fast = head;
        //这个节点记录 前半部分 反转的链表节点
        //每次slow节点移动 prev节点跟着移动 将slow的下一个节点指向prev
        LinkNode prev = null;

        //三个节点的形式是prev追slow,slow追fast,fast赶到终点
        while (fast != null && fast.next != null) {

            fast = fast.next.next;
            //定义一个节点 记录当前节点的下一个节点的值 最终赋值给slow
            LinkNode next = slow.next;


            slow.next = prev;

            prev = slow;

            slow = next;
        }

        //如果字符串是奇数个字符，如ABCDCBA，
        if (fast != null) {
            slow = slow.next;
        }

        while(slow!=null){
            if(slow.data!=prev.data){
                return false;
            }
            slow=slow.next;
            prev=prev.next;
        }
        return true;
    }
}
