package me.collections.persistent.redblacktree;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.collections.persistent.redblacktree.Node.Builder.*;
import static me.collections.persistent.redblacktree.Node.doubleNil;
import static me.collections.persistent.redblacktree.Node.nil;
import static me.collections.persistent.redblacktree.PersistentRedBlackTree.balance;
import static me.collections.persistent.redblacktree.PersistentRedBlackTree.rotate;
import static me.collections.persistent.redblacktree.Validator.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

            List<Integer> persistentTreeList = new ArrayList<>();
            tree.iterator().forEachRemaining(persistentTreeList::add);

            assertEquals(list.stream().sorted().collect(Collectors.toList()), persistentTreeList);
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

}