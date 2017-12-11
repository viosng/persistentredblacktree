package me.collections.persistent.redblacktree;

import java.util.Objects;

import static me.collections.persistent.redblacktree.Node.Builder.copy;
import static me.collections.persistent.redblacktree.Node.Color.*;

/**
 * @author nickolaysaveliev
 * @since 07/12/2017
 */
final class Node {

    enum Color {
        RED("R"), BLACK("B"), DOUBLE_BLACK("BB");

        final String name;

        Color(String name) {
            this.name = name;
        }
    }

    private static final Node NIL = new Node(0, null, null, BLACK);
    private static final Node DOUBLE_NIL = new Node(0, null, null, DOUBLE_BLACK);

    private final int key;
    private final Node left, right;
    private final Color color;

    Node(int key, Node left, Node right, Color color) {
        this.key = key;
        this.left = left;
        this.right = right;
        this.color = color;
    }

    boolean isNil() {
        return this == nil();
    }

    boolean isDoubleNil() {
        return this == doubleNil();
    }

    boolean isRed() {
        return this.color == RED;
    }

    boolean isBlack() {
        return this.color == BLACK;
    }

    boolean isBlackNode() {
        return !isNil() && isBlack();
    }

    boolean isDoubleBlack() {
        return this.color == DOUBLE_BLACK;
    }

    int key() {
        return key;
    }

    Node left() {
        return this == nil() ? nil() : left;
    }

    Node right() {
        return this == nil() ? nil() : right;
    }

    Node blacken() {
        return this.isRed() ? copy(this).black().build() : this;
    }

    Node redden() {
        return !isNil() && isBlack() && left.isBlack() && right.isBlack() ? copy(this).red().build() : this;
    }

    Node demote() {
        return isDoubleBlack() ? (isDoubleNil() ? nil() : copy(this).black().build()) : this;
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
        if (this.isNil()) return "NIL";
        if (this.isDoubleNil()) return "DOUBLE_NIL";
        return color.name + "{" +
                "key=" + key +
                ", left=" + left +
                ", right=" + right +
                '}';
    }

    static Node nil() {
        return NIL;
    }

    static Node doubleNil() {
        return DOUBLE_NIL;
    }

    static class Builder {
        private int key;
        private Node left = nil(), right = nil();
        private Color color;

        static Builder copy(Node node) {
            if (node.isNil()) throw new UnsupportedOperationException();
            return new Builder().key(node.key).left(node.left).right(node.right).color(node.color);
        }

        static Builder black(int key) {
            return new Builder().key(key).black();
        }

        static Builder red(int key) {
            return new Builder().key(key).red();
        }

        static Builder doubleBlack(int key) {
            return new Builder().key(key).doubleBlack();
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

        Builder doubleBlack() {
            this.color = DOUBLE_BLACK;
            return this;
        }

        Node build() {
            return new Node(key, left, right, color);
        }
    }
}
