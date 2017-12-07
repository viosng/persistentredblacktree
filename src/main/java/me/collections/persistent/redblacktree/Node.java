package me.collections.persistent.redblacktree;

import java.util.Objects;

import static me.collections.persistent.redblacktree.Node.Color.BLACK;

/**
 * @author nickolaysaveliev
 * @since 07/12/2017
 */
final class Node {

    enum Color {
        RED, BLACK
    }

    private static final Node NIL = new Node(0, null, null, BLACK);

    final int key;
    final Node left, right;
    final Color color;

    public Node(int key, Node left, Node right, Color color) {
        this.key = key;
        this.left = left;
        this.right = right;
        this.color = color;
    }

    boolean isNil() {
        return this == nil();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Node node = (Node) o;
        return key == node.key &&
                Objects.equals(left, node.left) &&
                Objects.equals(right, node.right) &&
                color == node.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, left, right, color);
    }

    @Override
    public String toString() {
        return this == nil()
                ? "NIL"
                : color + "{" +
                    "key=" + key +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
    }

    static Node nil() {
        return NIL;
    }
}
