package com.code.sort;

import java.util.*;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/21 17:31
 *
给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。

有效字符串需满足：

左括号必须用相同类型的右括号闭合。
左括号必须以正确的顺序闭合。
注意空字符串可被认为是有效字符串。
输入: "()"
输出: true
输入: "([)]"
输出: false
 */
public class IsValid {

    public static void main(String[] args) {
        String s="{[]}";
        boolean valid1 = isValid1(s);
        System.out.println(valid1);

    }

    public static boolean isValid(String s){
        int n=s.length();
        //如果是奇数个，则直接返回false
        if (n%2==1){
            return false;
        }
        Map<Character, Character> pairs = new HashMap<Character, Character>() {{
            put(')', '(');
            put(']', '[');
            put('}', '{');
        }};

        Deque<Character> stack = new LinkedList<Character>();
            for(int i=0;i<n;i++){
                char ch=s.charAt(i);
                if (pairs.containsKey(i)) {
                if (stack.isEmpty()||stack.peek()!= pairs.get(ch)){
                    return false;
                }
                stack.pop();
                }else {
                    stack.push(ch);
                }
                }
            return stack.isEmpty();
            }


            public static boolean isValid1(String s){
                if (s.length()%2==1){
                    return false;
                }
                if(s.isEmpty())
                    return true;
                Stack<Character> stack=new Stack<Character>();
                for(char c:s.toCharArray()){
                    if(c=='(')
                        stack.push(')');
                    else if(c=='{')
                        stack.push('}');
                    else if(c=='[')
                        stack.push(']');
                    else if(stack.empty()||c!=stack.pop())
                        return false;
                }
                if(stack.empty())
                    return true;
                return false;
            }

}
