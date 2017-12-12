package me.collections.persistent.redblacktree;

import me.collections.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.collections.persistent.redblacktree.Node.Builder.*;
import static me.collections.persistent.redblacktree.Node.doubleNil;
import static me.collections.persistent.redblacktree.Node.nil;
import static me.collections.persistent.redblacktree.PersistentRedBlackTree.*;
import static me.collections.persistent.redblacktree.Validator.validate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author nickolaysaveliev
 * @since 08/12/2017
 */
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
    void should_balance_black(String caseName, Node<Integer> x) {
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
    void should_balance_double_black(String caseName, Node<Integer> x) {
        Node balanceResult = black(2)
                .left(black(1).build())
                .right(black(3).build())
                .build();
        assertEquals(balanceResult, balance(x), caseName);
    }

    private static Stream<Node<Integer>> createBalanceOtherTests() {
        return Stream.of(
                nil(),
                red(3).build(),
                red(2).right(red(3).build()).build(),
                black(2).left(red(3).build()).build()
        );
    }

    @ParameterizedTest
    @MethodSource("createBalanceOtherTests")
    void should_balance_other(Node<Integer> x) {
        assertEquals(x, balance(x));
    }

    @Test
    void should_insert() {
        assertThrows(IllegalArgumentException.class, () -> new PersistentRedBlackTree<>().add(null));
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Integer> list = new ArrayList<>();
        PersistentRedBlackTree<Integer> tree = new PersistentRedBlackTree<>();
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
        Node<Integer> node1 = red(1)
                .left(doubleBlack(2).build())
                .right(black(3).build())
                .build();
        Node<Integer> expected1 = black(3)
                .left(red(1)
                        .left(black(2).build())
                        .build())
                .build();
        assertEquals(balance(expected1), rotate(node1));

        Node<Integer> node2 = red(1)
                .left(doubleNil())
                .right(black(3).build())
                .build();
        Node<Integer> expected2 = black(3)
                .left(red(1).build())
                .build();
        assertEquals(balance(expected2), rotate(node2));
    }

    @Test
    void should_rotate_red_case_2() {
        Node<Integer> node1 = red(1)
                .left(black(3).build())
                .right(doubleBlack(2).build())
                .build();
        Node<Integer> expected1 = black(3)
                .right(red(1)
                        .right(black(2).build())
                        .build())
                .build();
        assertEquals(balance(expected1), rotate(node1));

        Node<Integer> node2 = red(1)
                .left(black(3).build())
                .right(doubleNil())
                .build();
        Node<Integer> expected2 = black(3)
                .right(red(1).build())
                .build();
        assertEquals(balance(expected2), rotate(node2));
    }

    @Test
    void should_rotate_black_case_1() {
        Node<Integer> node1 = black(1)
                .left(doubleBlack(2).build())
                .right(black(3).build())
                .build();
        Node<Integer> expected1 = doubleBlack(3)
                .left(red(1)
                        .left(black(2).build())
                        .build())
                .build();
        assertEquals(balance(expected1), rotate(node1));

        Node<Integer> node2 = black(1)
                .left(doubleNil())
                .right(black(3).build())
                .build();
        Node<Integer> expected2 = doubleBlack(3)
                .left(red(1).build())
                .build();
        assertEquals(balance(expected2), rotate(node2));
    }

    @Test
    void should_rotate_black_case_2() {
        Node<Integer> node1 = black(1)
                .left(black(3).build())
                .right(doubleBlack(2).build())
                .build();
        Node<Integer> expected1 = doubleBlack(3)
                .right(red(1)
                        .right(black(2).build())
                        .build())
                .build();
        assertEquals(balance(expected1), rotate(node1));

        Node<Integer> node2 = black(1)
                .left(black(3).build())
                .right(doubleNil())
                .build();
        Node<Integer> expected2 = doubleBlack(3)
                .right(red(1).build())
                .build();
        assertEquals(balance(expected2), rotate(node2));
    }

    @Test
    void should_rotate_black_case_3() {
        Node<Integer> node1 = black(1)
                .left(doubleBlack(2).build())
                .right(red(3)
                        .left(black(4).build())
                        .build())
                .build();
        Node<Integer> expected1 = black(3)
                .left(balance(black(4)
                                .left(red(1)
                                        .left(black(2).build())
                                        .build())
                                .build()
                        )
                )
                .build();
        assertEquals(expected1, rotate(node1));

        Node<Integer> node2 = black(1)
                .left(doubleNil())
                .right(red(3)
                        .left(black(4).build())
                        .build())
                .build();
        Node<Integer> expected2 = black(3)
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
        Node<Integer> node1 = black(1)
                .left(red(3)
                        .right(black(4).build())
                        .build())
                .right(doubleBlack(2).build())
                .build();
        Node<Integer> expected1 = black(3)
                .right(balance(black(4)
                                .right(red(1)
                                        .right(black(2).build())
                                        .build())
                                .build()
                        )
                )
                .build();
        assertEquals(expected1, rotate(node1));

        Node<Integer> node2 = black(1)
                .left(red(3)
                        .right(black(4).build())
                        .build())
                .right(doubleNil())
                .build();
        Node<Integer> expected2 = black(3)
                .right(balance(black(4)
                                .right(red(1).build())
                                .build()
                        )
                )
                .build();
        assertEquals(expected2, rotate(node2));
    }

    private static Stream<Node<Integer>> createNotRotateTests() {
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
    void should_not_rotate(Node<Integer> x) {
        assertEquals(x, rotate(x));
    }

    @Test
    void should_find_min_and_remove() {
        assertThrows(IllegalArgumentException.class, () -> PersistentRedBlackTree.<Integer>minRemove(nil()));
        assertThrows(IllegalArgumentException.class, () -> PersistentRedBlackTree.<Integer>minRemove(doubleNil()));
        assertEquals(Pair.of(1, nil()), minRemove(red(1).build()));
        assertEquals(Pair.of(1, doubleNil()), minRemove(black(1).build()));
        assertEquals(Pair.of(1, black(2).build()), minRemove(black(1).right(red(2).build()).build()));

        Node<Integer> node = black(3)
                .left(black(1).right(red(2).build()).build())
                .right(black(4).build())
                .build();
        assertEquals(Pair.of(1, copy(node).left(black(2).build()).build()), minRemove(node));

        Pair<TreeSet<Integer>, PersistentRedBlackTree<Integer>> treePair = fillTrees(2000);
        TreeSet<Integer> treeSet = treePair.getKey();
        PersistentRedBlackTree<Integer> tree = treePair.getValue();
        while(!treeSet.isEmpty()) {
            Integer v = treeSet.pollFirst();
            tree = new PersistentRedBlackTree<>(tree.root.redden()); // emulate deletion
            Pair<Integer, PersistentRedBlackTree<Integer>> pair = tree.pollMin();
            assertEquals(v, pair.getKey());
            tree = pair.getValue();
            validate(tree);
        }
    }

    @Test
    void should_delete() {
        assertEquals(nil(), delete(nil(), 1, Integer::compareTo));

        assertEquals(nil(), delete(red(1).build(), 1, Integer::compareTo));

        assertEquals(red(1).build(), delete(red(1).build(), 2, Integer::compareTo));
        assertEquals(doubleNil(), delete(black(1).build(), 1, Integer::compareTo));
        assertEquals(black(1).build(), delete(black(1).build(), 2, Integer::compareTo));

        Node<Integer> node1 = black(2).left(red(1).build()).build();
        assertEquals(black(2).build(), delete(node1, 1, Integer::compareTo));
        assertEquals(black(1).build(), delete(node1, 2, Integer::compareTo));
        assertEquals(node1, delete(node1, 3, Integer::compareTo));

        Node<Integer> node2 = black(2).left(red(1).build()).right(black(3).build()).build();
        assertEquals(black(2).right(black(3).build()).build(), delete(node2, 1, Integer::compareTo));
        assertEquals(black(3).left(red(1).build()).right(doubleNil()).build(), delete(node2, 2, Integer::compareTo));
        assertEquals(black(2).left(red(1).build()).right(doubleNil()).build(), delete(node2, 3, Integer::compareTo));

        Node<Integer> node3 = black(2).left(red(1).build()).right(red(3).build()).build();
        assertEquals(black(2).right(red(3).build()).build(), delete(node3, 1, Integer::compareTo));
        assertEquals(black(3).left(red(1).build()).right(nil()).build(), delete(node3, 2, Integer::compareTo));
        assertEquals(black(2).left(red(1).build()).right(nil()).build(), delete(node3, 3, Integer::compareTo));
    }

    @Test
    void should_remove() {
        assertThrows(IllegalArgumentException.class, () -> new PersistentRedBlackTree<>().remove(null));
        assertEquals(new PersistentRedBlackTree<Integer>(), new PersistentRedBlackTree<Integer>().remove(1));
        assertEquals(new PersistentRedBlackTree<Integer>(), new PersistentRedBlackTree<>(red(1).build()).remove(1));
        assertEquals(new PersistentRedBlackTree<>(red(1).build()), new PersistentRedBlackTree<>(red(1).build()).remove(2));
        assertEquals(new PersistentRedBlackTree<Integer>(), new PersistentRedBlackTree<>(black(1).build()).remove(1));
        assertEquals(new PersistentRedBlackTree<>(red(1).build()), new PersistentRedBlackTree<>(black(1).build()).remove(2));
        assertEquals(new PersistentRedBlackTree<>(black(3).build()), new PersistentRedBlackTree<>(black(1).build())
                .remove(1).add(2).add(3).remove(3).remove(1).remove(2).remove(100500).add(3));
    }

    @Test
    void should_remove_min() {
        Pair<TreeSet<Integer>, PersistentRedBlackTree<Integer>> pair = fillTrees(1000);
        TreeSet<Integer> treeSet = pair.getKey();
        PersistentRedBlackTree<Integer> tree = pair.getValue();
        while(!treeSet.isEmpty()) {
            Integer v = treeSet.pollFirst();
            PersistentRedBlackTree<Integer> newTree = tree.remove(v);
            assertNotEquals(tree, newTree);
            validate(newTree);
            tree = newTree;
        }
    }

    @Test
    void should_remove_random() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        PersistentRedBlackTree<Integer> tree = new PersistentRedBlackTree<>();
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
            PersistentRedBlackTree<Integer> newTree = tree.remove(v);
            assertNotEquals(tree, newTree);

            assertEquals(values.subList(0, n - 1).stream().sorted().collect(Collectors.toList()), newTree.asList());

            validate(newTree);
            tree = newTree;
            n--;
        }

        assertEquals(new PersistentRedBlackTree(), tree);
    }

    @Test
    void should_add_and_remove() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        PersistentRedBlackTree<Integer> tree = new PersistentRedBlackTree<>();
        ArrayList<Integer> values = new ArrayList<>();
        int operationsCount = 10000;
        double border = 0.5, step = 0.01;
        while (operationsCount-- > 0) {
            double randDouble = random.nextDouble();
            PersistentRedBlackTree<Integer> newTree;
            if (values.isEmpty() || randDouble > border) {
                border -= step;

                int v = random.nextInt(1000);
                newTree = tree.add(v);
                values.add(v);

                if (border < 0.1) border = 0.5;
            } else {
                border += step;

                int v = values.remove(random.nextInt(values.size()));
                newTree = tree.remove(v);

                if (border > 0.9) border = 0.5;
            }

            assertNotEquals(tree, newTree);
            assertEquals(values.stream().sorted().collect(Collectors.toList()), newTree.asList());
            validate(newTree);

            tree = newTree;
        }
    }

    private static Stream<Arguments> createContainTest() {
        return Stream.of(
                Arguments.of(red(3).build(), 3),
                Arguments.of(black(5).build(), 5),
                Arguments.of(black(5).left(red(3).build()).build(), 3),
                Arguments.of(black(5).right(red(7).build()).build(), 7)
        );
    }

    @ParameterizedTest
    @MethodSource("createContainTest")
    void should_contain(Node<Integer> node, int x) {
        assertTrue(new PersistentRedBlackTree<>(node).contains(x));
    }

    private static Stream<Arguments> createNotContainTest() {
        return Stream.of(
                Arguments.of(doubleNil(), 2),
                Arguments.of(red(1).build(), 2),
                Arguments.of(black(2).build(), 3),
                Arguments.of(red(2).left(black(1).build()).build(), 0),
                Arguments.of(red(2).right(black(3).build()).build(), 4)
        );
    }

    @ParameterizedTest
    @MethodSource("createNotContainTest")
    void should_not_contain(Node<Integer> node, Integer x) {
        assertFalse(new PersistentRedBlackTree<>(node).contains(x));
    }

    @Test
    void should_peek_min() {
        assertThrows(IllegalStateException.class, () -> new PersistentRedBlackTree().peekMin());
        assertEquals(1, new PersistentRedBlackTree<>(black(1).build()).peekMin().intValue());
        assertEquals(1, new PersistentRedBlackTree<>(black(2).left(red(1).build()).build()).peekMin().intValue());
        assertEquals(1, new PersistentRedBlackTree<>(black(1).right(red(3).build()).build()).peekMin().intValue());
    }

    @Test
    void should_peek_max() {
        assertThrows(IllegalStateException.class, () -> new PersistentRedBlackTree().peekMax());
        assertEquals(1, new PersistentRedBlackTree<>(black(1).build()).peekMax().intValue());
        assertEquals(2, new PersistentRedBlackTree<>(black(2).left(red(1).build()).build()).peekMax().intValue());
        assertEquals(3, new PersistentRedBlackTree<>(black(1).right(red(3).build()).build()).peekMax().intValue());
    }

    @Test
    void should_use_different_comparators() {
        PersistentRedBlackTree<String> ascendingTree = new PersistentRedBlackTree<String>()
                .add("a").add("b").add("c");
        PersistentRedBlackTree<String> descendingTree = new PersistentRedBlackTree<String>(Comparator.reverseOrder())
                .add("a").add("b").add("c");

        assertEquals(ascendingTree.peekMin(), descendingTree.peekMax());
        assertEquals(ascendingTree.peekMax(), descendingTree.peekMin());
        ascendingTree = ascendingTree.pollMin().getValue();
        descendingTree = descendingTree.pollMin().getValue();
        assertEquals(ascendingTree.peekMin(), descendingTree.peekMin());
    }

    private static Pair<TreeSet<Integer>, PersistentRedBlackTree<Integer>> fillTrees(int n) {
        TreeSet<Integer> treeSet = new TreeSet<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        PersistentRedBlackTree<Integer> tree = new PersistentRedBlackTree<>();
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