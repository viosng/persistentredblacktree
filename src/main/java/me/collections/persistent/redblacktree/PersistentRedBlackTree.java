package me.collections.persistent.redblacktree;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static me.collections.persistent.redblacktree.Node.Color.BLACK;
import static me.collections.persistent.redblacktree.Node.Color.RED;
import static me.collections.persistent.redblacktree.Node.nil;

/**
 * @author nickolaysaveliev
 * @since 07/12/2017
 */
@SuppressWarnings({"WeakerAccess", "SuspiciousNameCombination"})
public class PersistentRedBlackTree {


    private final Node root;

    public PersistentRedBlackTree() {
        this.root = nil();
    }

    PersistentRedBlackTree(Node root) {
        this.root = root;
    }

    public PersistentRedBlackTree put(int x) {
        Node newNode = new Node(x, nil(), nil(), RED);
        return new PersistentRedBlackTree(makeBlack(insert(root, newNode)));
    }

    Node insert(Node node, Node newNode) {
        if (node.isNil()) {
            return newNode;
        } else if (newNode.key < node.key){
            return balance(new Node(node.key, insert(node.left, newNode), node.right, node.color));
        } else {
            return balance(new Node(node.key, node.left, insert(node.right, newNode), node.color));
        }
    }

    private static Node makeBlack(Node node) {
        return new Node(node.key, node.left, node.right, BLACK);
    }

    static Node balance(Node node) {
        if (!node.isNil() && node.color == BLACK) {
            boolean leftLeftCase = !node.left.isNil() && node.left.color == RED
                    && !node.left.left.isNil() && node.left.left.color == RED;
            if (leftLeftCase) {
                Node left = makeBlack(node.left.left);
                Node right = new Node(node.key, node.left.right, node.right, BLACK);
                return new Node(node.left.key, left, right, RED);
            }

            boolean leftRightCase = !node.left.isNil() && node.left.color == RED
                    && !node.left.right.isNil() && node.left.right.color == RED;
            if (leftRightCase) {
                Node left = new Node(node.left.key, node.left.left, node.left.right.left, BLACK);
                Node right = new Node(node.key, node.left.right.right, node.right, BLACK);
                return new Node(node.left.right.key, left, right, RED);
            }

            boolean rightLeftCase = !node.right.isNil() && node.right.color == RED
                    && !node.right.left.isNil() && node.right.left.color == RED;
            if (rightLeftCase) {
                Node left = new Node(node.key, node.left, node.right.left.left, BLACK);
                Node right = new Node(node.right.key, node.right.left.right, node.right.right, BLACK);
                return new Node(node.right.left.key, left, right, RED);
            }

            boolean rightRightCase = !node.right.isNil() && node.right.color == RED
                    && !node.right.right.isNil() && node.right.right.color == RED;
            if (rightRightCase) {
                Node left = new Node(node.key, node.left, node.right.left, BLACK);
                Node right = makeBlack(node.right.right);
                return new Node(node.right.key, left, right, RED);
            }
        }
        return node;
    }

    void validate() {
        if (root.isNil()) return;
        if (root.color == RED) {
            throw new IllegalStateException("Root is RED:" + root);
        }
        checkRedNode(root);
        checkBlackHeight(root);
        checkBST(root);
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

    @Override
    public String toString() {
        return "PersistentRedBlackTree{" +
                "root=" + root +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PersistentRedBlackTree that = (PersistentRedBlackTree) o;
        return Objects.equals(root, that.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }
}
