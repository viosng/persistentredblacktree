package me.collections.persistent.redblacktree;

import me.collections.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.collections.persistent.redblacktree.Node.Builder.*;
import static me.collections.persistent.redblacktree.Node.doubleNil;
import static me.collections.persistent.redblacktree.Node.nil;
import static me.collections.persistent.redblacktree.PersistentRedBlackTree.*;
import static me.collections.persistent.redblacktree.Validator.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author nickolaysaveliev
 * @since 08/12/2017
 */
@SuppressWarnings("Duplicates")
class PersistentRedBlackTreeTest {

    private static Stream<Arguments> createBalanceBlackTests() {
        return Stream.of(
                Arguments.of("LeftLeftCase", black(3).left(red(2).left(red(1).build()).build()).build()),
                Arguments.of("LeftRightCase", black(3).left(red(1).right(red(2).build()).build()).build()),
                Arguments.of("RightLeftCase", black(1).right(red(3).left(red(2).build()).build()).build()),
                Arguments.of("RightRightCase", black(1).right(red(2).right(red(3).build()).build()).build())
        );
    }

    @ParameterizedTest
    @MethodSource("createBalanceBlackTests")
    void should_balance_black(String caseName, Node x) {
        Node balanceResult = red(2)
                .left(black(1).build())
                .right(black(3).build())
                .build();
        assertEquals(balanceResult, balance(x), caseName);
    }

    private static Stream<Arguments> createBalanceDoubleBlackTests() {
        return Stream.of(
                Arguments.of("LeftRightCase", doubleBlack(3).left(red(1).right(red(2).build()).build()).build()),
                Arguments.of("RightLeftCase", doubleBlack(1).right(red(3).left(red(2).build()).build()).build())
        );
    }

    @ParameterizedTest
    @MethodSource("createBalanceDoubleBlackTests")
    void should_balance_double_black(String caseName, Node x) {
        Node balanceResult = black(2)
                .left(black(1).build())
                .right(black(3).build())
                .build();
        assertEquals(balanceResult, balance(x), caseName);
    }

    private static Stream<Node> createBalanceOtherTests() {
        return Stream.of(
                nil(),
                red(3).build(),
                red(2).right(red(3).build()).build(),
                black(2).left(red(3).build()).build()
        );
    }

    @ParameterizedTest
    @MethodSource("createBalanceOtherTests")
    void should_balance_other(Node x) {
        assertEquals(x, balance(x));
    }

    @Test
    void should_insert() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Integer> list = new ArrayList<>();
        PersistentRedBlackTree tree = new PersistentRedBlackTree();
        for (int i = 0; i < 1000; i++) {
            int v = random.nextInt(10) - 5;
            tree = tree.add(v);
            list.add(v);

            validate(tree);

            assertEquals(list.stream().sorted().collect(Collectors.toList()), tree.asList());
        }
    }

    @Test
    void should_rotate_red_case_1() {
        Node node1 = red(1)
                .left(doubleBlack(2).build())
                .right(black(3).build())
                .build();
        Node expected1 = black(3)
                .left(red(1)
                        .left(black(2).build())
                        .build())
                .build();
        assertEquals(balance(expected1), rotate(node1));

        Node node2 = red(1)
                .left(doubleNil())
                .right(black(3).build())
                .build();
        Node expected2 = black(3)
                .left(red(1).build())
                .build();
        assertEquals(balance(expected2), rotate(node2));
    }

    @Test
    void should_rotate_red_case_2() {
        Node node1 = red(1)
                .left(black(3).build())
                .right(doubleBlack(2).build())
                .build();
        Node expected1 = black(3)
                .right(red(1)
                        .right(black(2).build())
                        .build())
                .build();
        assertEquals(balance(expected1), rotate(node1));

        Node node2 = red(1)
                .left(black(3).build())
                .right(doubleNil())
                .build();
        Node expected2 = black(3)
                .right(red(1).build())
                .build();
        assertEquals(balance(expected2), rotate(node2));
    }

    @Test
    void should_rotate_black_case_1() {
        Node node1 = black(1)
                .left(doubleBlack(2).build())
                .right(black(3).build())
                .build();
        Node expected1 = doubleBlack(3)
                .left(red(1)
                        .left(black(2).build())
                        .build())
                .build();
        assertEquals(balance(expected1), rotate(node1));

        Node node2 = black(1)
                .left(doubleNil())
                .right(black(3).build())
                .build();
        Node expected2 = doubleBlack(3)
                .left(red(1).build())
                .build();
        assertEquals(balance(expected2), rotate(node2));
    }

    @Test
    void should_rotate_black_case_2() {
        Node node1 = black(1)
                .left(black(3).build())
                .right(doubleBlack(2).build())
                .build();
        Node expected1 = doubleBlack(3)
                .right(red(1)
                        .right(black(2).build())
                        .build())
                .build();
        assertEquals(balance(expected1), rotate(node1));

        Node node2 = black(1)
                .left(black(3).build())
                .right(doubleNil())
                .build();
        Node expected2 = doubleBlack(3)
                .right(red(1).build())
                .build();
        assertEquals(balance(expected2), rotate(node2));
    }

    @Test
    void should_rotate_black_case_3() {
        Node node1 = black(1)
                .left(doubleBlack(2).build())
                .right(red(3)
                        .left(black(4).build())
                        .build())
                .build();
        Node expected1 = black(3)
                .left(balance(black(4)
                                .left(red(1)
                                        .left(black(2).build())
                                        .build())
                                .build()
                        )
                )
                .build();
        assertEquals(expected1, rotate(node1));

        Node node2 = black(1)
                .left(doubleNil())
                .right(red(3)
                        .left(black(4).build())
                        .build())
                .build();
        Node expected2 = black(3)
                .left(balance(black(4)
                                .left(red(1).build())
                                .build()
                        )
                )
                .build();
        assertEquals(expected2, rotate(node2));
    }

    @Test
    void should_rotate_black_case_4() {
        Node node1 = black(1)
                .left(red(3)
                        .right(black(4).build())
                        .build())
                .right(doubleBlack(2).build())
                .build();
        Node expected1 = black(3)
                .right(balance(black(4)
                                .right(red(1)
                                        .right(black(2).build())
                                        .build())
                                .build()
                        )
                )
                .build();
        assertEquals(expected1, rotate(node1));

        Node node2 = black(1)
                .left(red(3)
                        .right(black(4).build())
                        .build())
                .right(doubleNil())
                .build();
        Node expected2 = black(3)
                .right(balance(black(4)
                                .right(red(1).build())
                                .build()
                        )
                )
                .build();
        assertEquals(expected2, rotate(node2));
    }

    private static Stream<Node> createNotRotateTests() {
        return Stream.of(
                nil(),
                red(3).build(),
                red(2).right(red(3).build()).build(),
                black(2).left(red(3).build()).build(),
                black(2).left(doubleBlack(3).build()).build()
        );
    }

    @ParameterizedTest
    @MethodSource("createNotRotateTests")
    void should_not_rotate(Node x) {
        assertEquals(x, rotate(x));
    }

    @Test
    void should_find_min_and_remove() {
        assertThrows(IllegalArgumentException.class, () -> minRemove(nil()));
        assertThrows(IllegalArgumentException.class, () -> minRemove(doubleNil()));
        assertEquals(Pair.of(1, nil()), minRemove(red(1).build()));
        assertEquals(Pair.of(1, doubleNil()), minRemove(black(1).build()));
        assertEquals(Pair.of(1, black(2).build()), minRemove(black(1).right(red(2).build()).build()));

        Node node = black(3)
                .left(black(1).right(red(2).build()).build())
                .right(black(4).build())
                .build();
        assertEquals(Pair.of(1, copy(node).left(black(2).build()).build()), minRemove(node));

        Pair<TreeSet<Integer>, PersistentRedBlackTree> treePair = fillTrees(1000);
        TreeSet<Integer> treeSet = treePair.getKey();
        PersistentRedBlackTree tree = treePair.getValue();
        while(!treeSet.isEmpty()) {
            Integer v = treeSet.pollFirst();
            tree = new PersistentRedBlackTree(tree.root.redden()); // emulate deletion
            Pair<Integer, PersistentRedBlackTree> pair = tree.minRemove();
            assertEquals(v, pair.getKey());
            tree = pair.getValue();
            validate(tree);
        }
    }

    @Test
    void should_delete() {
        assertEquals(nil(), delete(nil(), 1));

        assertEquals(nil(), delete(red(1).build(), 1));

        assertEquals(red(1).build(), delete(red(1).build(), 2));
        assertEquals(doubleNil(), delete(black(1).build(), 1));
        assertEquals(black(1).build(), delete(black(1).build(), 2));

        Node node1 = black(2).left(red(1).build()).build();
        assertEquals(black(2).build(), delete(node1, 1));
        assertEquals(black(1).build(), delete(node1, 2));
        assertEquals(node1, delete(node1, 3));

        Node node2 = black(2).left(red(1).build()).right(black(3).build()).build();
        assertEquals(black(2).right(black(3).build()).build(), delete(node2, 1));
        assertEquals(black(3).left(red(1).build()).right(doubleNil()).build(), delete(node2, 2));
        assertEquals(black(2).left(red(1).build()).right(doubleNil()).build(), delete(node2, 3));

        Node node3 = black(2).left(red(1).build()).right(red(3).build()).build();
        assertEquals(black(2).right(red(3).build()).build(), delete(node3, 1));
        assertEquals(black(3).left(red(1).build()).right(nil()).build(), delete(node3, 2));
        assertEquals(black(2).left(red(1).build()).right(nil()).build(), delete(node3, 3));
    }

    @Test
    void should_remove() {
        assertEquals(new PersistentRedBlackTree(), new PersistentRedBlackTree().remove(1));
        assertEquals(new PersistentRedBlackTree(), new PersistentRedBlackTree(red(1).build()).remove(1));
        assertEquals(new PersistentRedBlackTree(red(1).build()), new PersistentRedBlackTree(red(1).build()).remove(2));
        assertEquals(new PersistentRedBlackTree(), new PersistentRedBlackTree(black(1).build()).remove(1));
        assertEquals(new PersistentRedBlackTree(red(1).build()), new PersistentRedBlackTree(black(1).build()).remove(2));
        assertEquals(new PersistentRedBlackTree(black(3).build()), new PersistentRedBlackTree(black(1).build())
                .remove(1).add(2).add(3).remove(3).remove(1).remove(2).remove(100500).add(3));
    }

    @Test
    void should_remove_min() {
        Pair<TreeSet<Integer>, PersistentRedBlackTree> pair = fillTrees(1000);
        TreeSet<Integer> treeSet = pair.getKey();
        PersistentRedBlackTree tree = pair.getValue();
        while(!treeSet.isEmpty()) {
            Integer v = treeSet.pollFirst();
            PersistentRedBlackTree newTree = tree.remove(v);
            assertNotEquals(tree, newTree);
            validate(newTree);
            tree = newTree;
        }
    }

    @Test
    void should_remove_random() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        PersistentRedBlackTree tree = new PersistentRedBlackTree();
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            int v = random.nextInt();
            tree = tree.add(v);
            values.add(v);
        }

        int n = values.size();
        while (n > 0) {
            int index = random.nextInt(n);
            Collections.swap(values, index, n - 1);
            int v = values.get(n - 1);
            PersistentRedBlackTree newTree = tree.remove(v);
            assertNotEquals(tree, newTree);

            assertEquals(values.subList(0, n - 1).stream().sorted().collect(Collectors.toList()), newTree.asList());

            validate(newTree);
            tree = newTree;
            n--;
        }

        assertEquals(new PersistentRedBlackTree(), tree);
    }

    private static Pair<TreeSet<Integer>, PersistentRedBlackTree> fillTrees(int n) {
        TreeSet<Integer> treeSet = new TreeSet<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        PersistentRedBlackTree tree = new PersistentRedBlackTree();
        for (int i = 0; i < n; i++) {
            int v = random.nextInt();
            while(treeSet.contains(v)) {
                v = random.nextInt();
            }
            tree = tree.add(v);
            treeSet.add(v);
        }
        return Pair.of(treeSet, tree);
    }
}