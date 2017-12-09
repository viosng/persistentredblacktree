package me.collections.persistent.redblacktree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static me.collections.persistent.redblacktree.Node.Builder.copy;
import static me.collections.persistent.redblacktree.Node.Builder.red;
import static me.collections.persistent.redblacktree.Node.nil;

/**
 * @author nickolaysaveliev
 * @since 07/12/2017
 */
@SuppressWarnings({"WeakerAccess", "SuspiciousNameCombination"})
public class PersistentRedBlackTree implements Iterable<Integer> {

    final Node root;

    public PersistentRedBlackTree() {
        this.root = nil();
    }

    PersistentRedBlackTree(Node root) {
        this.root = root;
    }

    public PersistentRedBlackTree add(int x) {
        Node newNode = red(x).build();
        return new PersistentRedBlackTree(makeBlack(insert(root, newNode)));
    }

    static Node insert(Node node, Node newNode) {
        if (node.isNil()) {
            return newNode;
        } else if (newNode.key() < node.key()) {
            return balance(copy(node).left(insert(node.left(), newNode)).build());
        } else {
            return balance(copy(node).right(insert(node.right(), newNode)).build());
        }
    }

    static Node balance(Node node) {
        if (!node.isNil() && node.isBlack()) {
            boolean leftLeftCase = node.left().isRed() && node.left().left().isRed();
            if (leftLeftCase) {
                return red(node.left().key())
                        .left(makeBlack(node.left().left()))
                        .right(copy(node)
                                .left(node.left().right())
                                .build())
                        .build();
            }

            boolean leftRightCase = node.left().isRed() && node.left().right().isRed();
            if (leftRightCase) {
                return red(node.left().right().key())
                        .left(copy(node.left())
                                .black()
                                .right(node.left().right().left())
                                .build())
                        .right(copy(node)
                                .left(node.left().right().right())
                                .build())
                        .build();
            }

            boolean rightLeftCase = node.right().isRed() && node.right().left().isRed();
            if (rightLeftCase) {
                return red(node.right().left().key())
                        .left(copy(node)
                                .right(node.right().left().left())
                                .build())
                        .right(copy(node.right())
                                .black()
                                .left(node.right().left().right())
                                .build())
                        .build();
            }

            boolean rightRightCase = node.right().isRed() && node.right().right().isRed();
            if (rightRightCase) {
                return red(node.right().key())
                        .left(copy(node)
                                .right(node.right().left())
                                .build())
                        .right(makeBlack(node.right().right()))
                        .build();
            }
        }
        return node;
    }

    private static void inOrderTraverse(Node root, Consumer<Node> nodeConsumer) {
        if (root.isNil()) return;
        inOrderTraverse(root.left(), nodeConsumer);
        nodeConsumer.accept(root);
        inOrderTraverse(root.right(), nodeConsumer);
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

    @Override
    public Iterator<Integer> iterator() {
        List<Integer> nodes = new ArrayList<>();
        inOrderTraverse(root, node -> nodes.add(node.key()));
        Iterator<Integer> iterator = nodes.iterator();
        return new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Integer next() {
                return iterator.next();
            }
        };
    }

    private static Node makeBlack(Node node) {
        return node.isNil() ? nil() : copy(node).black().build();
    }
}
