package me.collections.persistent.redblacktree;

import me.collections.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static me.collections.persistent.redblacktree.Node.Builder.*;
import static me.collections.persistent.redblacktree.Node.doubleNil;
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

    public Pair<Integer, PersistentRedBlackTree> minRemove() {
        Pair<Integer, Node> pair = minRemove(root);
        return Pair.of(pair.getKey(), new PersistentRedBlackTree(pair.getValue()));
    }

    public PersistentRedBlackTree remove(int x) {
        return new PersistentRedBlackTree(delete(root.redden(), x));
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
        if (!node.isNil() && node.isDoubleBlack()) {
            boolean doubleBlackLeftRightCase = node.left().isRed() && node.left().right().isRed();
            if (doubleBlackLeftRightCase) {
                return black(node.left().right().key())
                        .left(black(node.left().key())
                                .left(node.left().left())
                                .right(node.left().right().left())
                                .build()
                        )
                        .right(black(node.key())
                                .left(node.left().right().right())
                                .right(node.right())
                                .build()
                        )
                        .build();
            }
            boolean doubleBlackRightLeftCase = node.right().isRed() && node.right().left().isRed();
            if (doubleBlackRightLeftCase) {
                return black(node.right().left().key())
                        .left(black(node.key())
                                .left(node.left())
                                .right(node.right().left().left())
                                .build()
                        )
                        .right(black(node.right().key())
                                .left(node.right().left().right())
                                .right(node.right().right())
                                .build()
                        )
                        .build();
            }
        }
        return node;
    }

    static Node rotate(Node node) {
        Node left = node.left();
        Node right = node.right();
        if (node.isRed()) {
            // R (T BB a x b) y (T B c z d) = balance B (T R (T B a x b) y c) z d
            // R EE           y (T B c z d) = balance B (T R E y c) z d
            if (left.isDoubleBlack() && right.isBlackNode()) {
                return balance(
                        copy(right)
                                .left(red(node.key())
                                        .left(left.demote())
                                        .right(right.left())
                                        .build()
                                )
                                .build()
                );
            }
            // R (T B a x b) y (T BB c z d) = balance B a x (T R b y (T B c z d))
            // R (T B a x b) y EE           = balance B a x (T R b y E)
            if (left.isBlackNode() && right.isDoubleBlack()) {
                return balance(
                        copy(left)
                                .right(red(node.key())
                                        .left(left.right())
                                        .right(right.demote())
                                        .build()
                                )
                                .build()
                );
            }
        } else if (node.isBlackNode()) {
            // B (T BB a x b) y (T B c z d) = balance BB (T R (T B a x b) y c) z d
            // B EE           y (T B c z d) = balance BB (T R E y c) z d
            if (left.isDoubleBlack() && right.isBlackNode()) {
                return balance(
                        copy(right)
                                .doubleBlack()
                                .left(red(node.key())
                                        .left(left.demote())
                                        .right(right.left())
                                        .build())
                                .build()
                );
            }
            // B (T B a x b) y (T BB c z d) = balance BB a x (T R b y (T B c z d))
            // B (T B a x b) y EE           = balance BB a x (T R b y E)
            if (left.isBlackNode() && right.isDoubleBlack()) {
                return balance(
                        copy(left)
                                .doubleBlack()
                                .right(red(node.key())
                                        .left(left.right())
                                        .right(right.demote())
                                        .build())
                                .build()
                );
            }
            // B (T BB a w b) x (T R (T B c y d) z e) = T B (balance B (T R (T B a w b) x c) y d) z e
            // B EE           x (T R (T B c y d) z e) = T B (balance B (T R E x c) y d) z e
            if (left.isDoubleBlack() && right.isRed() && right.left().isBlackNode()) {
                return black(right.key())
                        .left(balance(
                                copy(right.left())
                                        .left(red(node.key())
                                                .left(left.demote())
                                                .right(right.left().left())
                                                .build())
                                        .build()
                                )
                        )
                        .right(right.right())
                        .build();
            }
            // B (T R a w (T B b x c)) y (T BB d z e) = T B a w (balance B b x (T R c y (T B d z e)))
            // B (T R a w (T B b x c)) y EE           = T B a w (balance B b x (T R c y E))
            if (left.isRed() && left.right().isBlackNode() && right.isDoubleBlack()) {
                return copy(left)
                        .black()
                        .right(balance(
                                copy(left.right())
                                        .right(red(node.key())
                                                .left(left.right().right())
                                                .right(right.demote())
                                                .build())
                                        .build()
                                )
                        )
                        .build();
            }
        }
        return node;
    }

    static Pair<Integer, Node> minRemove(Node node) {
        if (node.isNil() || node.isDoubleNil()) {
            throw new IllegalArgumentException("Empty tree");
        }
        if (node.isRed() && node.left().isNil() && node.right().isNil()) {
            return Pair.of(node.key(), nil());
        }
        if (node.isBlackNode() && node.left().isNil() && node.right().isNil()) {
            return Pair.of(node.key(), doubleNil());
        }
        if (node.isBlackNode() && node.left().isNil()
                && node.right().isRed() && node.right().left().isNil() && node.right().right().isNil()) {
            return Pair.of(node.key(), node.right().blacken());
        }
        Pair<Integer, Node> leftMin = minRemove(node.left());
        return Pair.of(leftMin.getKey(), rotate(copy(node).left(leftMin.getValue()).build()));
    }

    static Node delete(Node node, int x) {
        if (node.isNil()) return node;
        Node left = node.left();
        Node right = node.right();
        int key = node.key();
        if (node.isRed() && left.isNil() && right.isNil()) {
            return key == x ? nil() : node;
        }
        if (node.isBlackNode() && left.isNil() && right.isNil()) {
            return key == x ? doubleNil() : node;
        }
        if (node.isBlackNode() && left.isRed() && left.left().isNil() && left.right().isNil() && right.isNil()) {
            if (x < key) {
                return copy(node)
                        .left(delete(left, x))
                        .build();
            } else if (x == key) {
                return left.blacken();
            } else {
                return node;
            }
        }
        if (x < key) {
            return rotate(
                    copy(node)
                            .left(delete(left, x))
                            .build()
            );
        } else if (x == key) {
            Pair<Integer, Node> pair = minRemove(right);
            return rotate(
                    copy(node)
                            .key(pair.getKey())
                            .right(pair.getValue())
                            .build()
            );
        } else {
            return rotate(
                    copy(node)
                            .right(delete(right, x))
                            .build()
            );
        }
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

    public List<Integer> asList() {
        List<Integer> list = new ArrayList<>();
        iterator().forEachRemaining(list::add);
        return list;
    }

    private static Node makeBlack(Node node) {
        return node.isNil() ? nil() : copy(node).black().build();
    }
}