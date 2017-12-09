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
import static me.collections.persistent.redblacktree.Node.nil;
import static me.collections.persistent.redblacktree.PersistentRedBlackTree.balance;
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
        for (int i = 0; i < 3000; i++) {
            int v = random.nextInt(10) - 5;
            tree = tree.add(v);
            list.add(v);

            validate(tree);

            List<Integer> persistentTreeList = new ArrayList<>();
            tree.iterator().forEachRemaining(persistentTreeList::add);

            assertEquals(list.stream().sorted().collect(Collectors.toList()), persistentTreeList);
        }
    }
}