package me.collections.persistent.redblacktree;

import java.util.Objects;

import static me.collections.persistent.redblacktree.Node.Color.BLACK;
import static me.collections.persistent.redblacktree.Node.Color.RED;

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

    Node(int key, Node left, Node right, Color color) {
        this.key = key;
        this.left = left;
        this.right = right;
        this.color = color;
    }

    boolean isNil() {
        return this == nil();
    }

    boolean isRed() {
        return this.color == RED;
    }

    boolean isBlack() {
        return this.color == BLACK;
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

    static class Builder {
        private int key;
        private Node left = nil(), right = nil();
        private Color color;

        static Builder copy(Node node) {
            return new Builder().key(node.key).left(node.left).right(node.right).color(node.color);
        }

        static Builder black(int key) {
            return new Builder().key(key).black();
        }

        static Builder red(int key) {
            return new Builder().key(key).red();
        }

        Builder key(int key) {
            this.key = key;
            return this;
        }

        Builder left(Node left) {
            this.left = left;
            return this;
        }

        Builder right(Node right) {
            this.right = right;
            return this;
        }

        Builder color(Color color) {
            this.color = color;
            return this;
        }

        Builder red() {
            this.color = RED;
            return this;
        }

        Builder black() {
            this.color = BLACK;
            return this;
        }

        Node build() {
            return new Node(key, left, right, color);
        }
    }
}
