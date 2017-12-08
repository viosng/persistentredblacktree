package me.collections.persistent.redblacktree;

import java.util.Arrays;
import java.util.List;

import static me.collections.persistent.redblacktree.Node.Color.RED;

/**
 * @author nickolaysaveliev
 * @since 08/12/2017
 */
public class Validator {

    public static void validate(PersistentRedBlackTree tree) {
        if (tree.root.isNil()) return;
        if (tree.root.color == RED) {
            throw new IllegalStateException("Root is RED:" + tree.root);
        }
        checkRedNode(tree.root);
        checkBlackHeight(tree.root);
        checkBST(tree.root);
    }

    static void checkRedNode(Node node) {
        if (node.isNil()) return;
        if (!node.isNil() && node.isRed() && (node.left.isRed() || node.right.isRed())) {
            throw new IllegalStateException("Red parent and child: " + node);
        }
        checkRedNode(node.left);
        checkRedNode(node.right);
    }

    static int checkBlackHeight(Node node) {
        if (node.isNil()) return 1;
        int lbh = checkBlackHeight(node.left);
        int rbh = checkBlackHeight(node.right);
        if (lbh != rbh) {
            throw new IllegalStateException("Black heights are different: " + node);
        }
        return lbh + (node.isBlack() ? 1 : 0);
    }

    static List<Integer> checkBST(Node node) {
        int min = node.key, max = node.key;
        if (!node.left.isNil()) {
            List<Integer> left = checkBST(node.left);
            if (left.get(1) > node.key) {
                throw new IllegalStateException("Not BST: " + node);
            }
            min = Math.min(min, left.get(0));
        }
        if (!node.right.isNil()) {
            List<Integer> right = checkBST(node.right);
            if (right.get(0) < node.key) {
                throw new IllegalStateException("Not BST: " + node);
            }
            max = Math.max(max, right.get(1));
        }
        return Arrays.asList(min, max);
    }
}
