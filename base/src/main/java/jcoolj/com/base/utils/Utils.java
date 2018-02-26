package jcoolj.com.base.utils;

import java.util.HashMap;
import java.util.Stack;
import java.util.TreeMap;

import jcoolj.com.core.utils.Logger;

/**
 * Created by Zack on 2017/10/12.
 */

public class Utils {

    public class TreeNode{
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x){
            val = x;
        }
    }

    public void postOrder(TreeNode node){
        Stack<TreeNode> nodes = new Stack<>();
        Stack<Integer> flags = new Stack<>();
        while(node != null || !nodes.isEmpty()){
            while(node != null){
                nodes.push(node);
                flags.push(0);
                node = node.left;
            }
            while(!nodes.isEmpty() && flags.peek().equals(1)){
                flags.pop();
                Logger.d(nodes.pop().val+" ");
            }
            if(!nodes.isEmpty()){
                flags.pop();
                flags.push(1);
                node = nodes.peek().right;
            }
        }
    }

}
