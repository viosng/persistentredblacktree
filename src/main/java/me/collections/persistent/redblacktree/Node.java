package me.collections.persistent.redblacktree;

import java.util.Objects;

import static me.collections.persistent.redblacktree.Node.Builder.copy;
import static me.collections.persistent.redblacktree.Node.Color.*;

/**
 * @author nickolaysaveliev
 * @since 07/12/2017
 */
@SuppressWarnings("unchecked")
final class Node<K> {

    enum Color {
        RED("R"), BLACK("B"), DOUBLE_BLACK("BB");

        final String name;

        Color(String name) {
            this.name = name;
        }
    }

    private static final Node NIL = new Node(null, null, null, BLACK);
    private static final Node DOUBLE_NIL = new Node(null, null, null, DOUBLE_BLACK);

    private final K key;
    private final Node left, right;
    private final Color color;

    Node(K key, Node left, Node right, Color color) {
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

    K key() {
        return key;
    }

    Node<K> left() {
        return this == nil() ? nil() : left;
    }

    Node<K> right() {
        return this == nil() ? nil() : right;
    }

    Node<K> blacken() {
        return this.isRed() ? copy(this).black().build() : this;
    }

    Node<K> redden() {
        return !isNil() && isBlack() && left.isBlack() && right.isBlack() ? copy(this).red().build() : this;
    }

    Node<K> demote() {
        return isDoubleBlack() ? (isDoubleNil() ? nil() : copy(this).black().build()) : this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Node node = (Node) o;
        return Objects.equals(key, node.key) &&
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

    static <K1> Node<K1> nil() {
        return NIL;
    }

    static <K1> Node<K1> doubleNil() {
        return DOUBLE_NIL;
    }

    static class Builder<K> {
        private K key;
        private Node left = nil(), right = nil();
        private Color color;

        static <K1> Builder<K1> copy(Node<K1> node) {
            if (node.isNil()) throw new UnsupportedOperationException();
            return new Builder<K1>().key(node.key).left(node.left).right(node.right).color(node.color);
        }

        static <K1> Builder<K1> black(K1 key) {
            return new Builder<K1>().key(key).black();
        }

        static <K1> Builder<K1> red(K1 key) {
            return new Builder<K1>().key(key).red();
        }

        static <K1> Builder<K1> doubleBlack(K1 key) {
            return new Builder<K1>().key(key).doubleBlack();
        }

        Builder<K> key(K key) {
            this.key = key;
            return this;
        }

        Builder<K> left(Node left) {
            this.left = left;
            return this;
        }

        Builder<K> right(Node right) {
            this.right = right;
            return this;
        }

        Builder<K> color(Color color) {
            this.color = color;
            return this;
        }

        Builder<K> red() {
            this.color = RED;
            return this;
        }

        Builder<K> black() {
            this.color = BLACK;
            return this;
        }

        Builder<K> doubleBlack() {
            this.color = DOUBLE_BLACK;
            return this;
        }

        Node<K> build() {
            return new Node<>(key, left, right, color);
        }
    }
}
