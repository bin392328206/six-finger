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

        if (null == head) {
            return false;
        }

        //定义几个链表
        LinkNode fast = head;

        LinkNode slow = head;

        LinkNode pre = null;

        while (null != fast && fast.getNext() != null) {
            //快的走2步
            fast = fast.next.next;
            //定义一个节点 记录当前节点的下一个节点的值 最终赋值给slow
            LinkNode next = slow.next;

            //把slow  其实就是把pre倒转
            slow.next = pre;

            pre = slow;
            slow = next;
        }

        //如果字符串是奇数个字符，如ABCDCBA，
        if (fast != null) {
            slow = slow.next;
        }

        while (slow != null) {
            if (slow.data != pre.data) {
                return false;
            }
            slow = slow.next;
            pre = pre.next;
        }
        return true;


    }
}
