package me.collections.persistent.redblacktree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author nickolaysaveliev
 * @since 08/12/2017
 */
public class Validator {

    public static <K extends Comparable<K>> void validate(PersistentRedBlackTree<K> tree) {
        if (tree.root.isNil()) return;
        checkRedNode(tree.root);
        checkBlackHeight(tree.root);
        checkBST(tree.root);
    }

    static void checkRedNode(Node node) {
        if (node.isNil()) return;
        if (!node.isNil() && node.isRed() && (node.left().isRed() || node.right().isRed())) {
            throw new IllegalStateException("Red parent and child: " + node);
        }
        checkRedNode(node.left());
        checkRedNode(node.right());
    }

    static int checkBlackHeight(Node node) {
        if (node.isNil()) return 1;
        int lbh = checkBlackHeight(node.left());
        int rbh = checkBlackHeight(node.right());
        if (lbh != rbh) {
            throw new IllegalStateException("Black heights are different: " + node);
        }
        return lbh + (node.isBlack() ? 1 : 0);
    }

    static <K extends Comparable<K>> List<K> checkBST(Node<K> node) {
        K min = node.key(), max = node.key();
        if (!node.left().isNil()) {
            List<K> left = checkBST(node.left());
            if (left.get(1).compareTo(node.key()) > 0) {
                throw new IllegalStateException("Not BST: " + node);
            }
            min = Collections.min(Arrays.asList(min, left.get(0)));
        }
        if (!node.right().isNil()) {
            List<K> right = checkBST(node.right());
            if (right.get(1).compareTo(node.key()) < 0) {
                throw new IllegalStateException("Not BST: " + node);
            }
            max = Collections.max(Arrays.asList(max, right.get(1)));
        }
        return Arrays.asList(min, max);
    }
}
