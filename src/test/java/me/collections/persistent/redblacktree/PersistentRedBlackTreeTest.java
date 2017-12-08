package me.collections.persistent.redblacktree;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static me.collections.persistent.redblacktree.Node.Builder.black;
import static me.collections.persistent.redblacktree.Node.Builder.red;
import static me.collections.persistent.redblacktree.Node.nil;
import static me.collections.persistent.redblacktree.PersistentRedBlackTree.balance;
import static me.collections.persistent.redblacktree.PersistentRedBlackTree.balanceLeft;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author nickolaysaveliev
 * @since 08/12/2017
 */
class PersistentRedBlackTreeTest {

    private static Stream<Arguments> createBalanceTests() {
        return Stream.of(
                Arguments.of("LeftLeftCase", black(3).left(red(2).left(red(1).build()).build()).build()),
                Arguments.of("LeftRightCase", black(3).left(red(1).right(red(2).build()).build()).build()),
                Arguments.of("RightLeftCase", black(1).right(red(3).left(red(2).build()).build()).build()),
                Arguments.of("RightRightCase", black(1).right(red(2).right(red(3).build()).build()).build())
        );
    }

    @ParameterizedTest
    @MethodSource("createBalanceTests")
    void should_balance(String caseName, Node x) {
        Node balanceResult = red(2)
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
        PersistentRedBlackTree tree = new PersistentRedBlackTree();
        TreeSet<Integer> treeSet = new TreeSet<>();
        for (int i = 0; i < 1000; i++) {
            int v = random.nextInt();

            tree = tree.add(v);
            treeSet.add(v);

            Validator.validate(tree);

            List<Integer> persistentTreeList = new ArrayList<>();
            tree.iterator().forEachRemaining(persistentTreeList::add);

            List<Integer> treeSetList = new ArrayList<>();
            treeSet.iterator().forEachRemaining(treeSetList::add);

            assertEquals(treeSetList, persistentTreeList);
        }
    }

    private static Stream<Arguments> createBalanceLeftTests() {
        return Stream.of(
                Arguments.of("ignore empty", nil(), nil()),
                Arguments.of("ignore red", red(1).right(black(5).build()).build(), red(1).right(black(5).build()).build()),
                Arguments.of("left is red",
                        black(1).left(red(1).build()).build(),
                        red(1).left(black(1).build()).build()
                ),
                Arguments.of("right is black",
                        black(1)
                                .left(black(2).build())
                                .right(black(3).build())
                                .build(),
                        black(1)
                                .left(black(2).build())
                                .right(red(3).build())
                                .build()
                ),
                Arguments.of("right is red",
                        black(1)
                                .left(black(2).build())
                                .right(red(3)
                                        .left(black(4)
                                                .left(red(6).build())
                                                .right(red(7).build())
                                                .build())
                                        .right(black(5).build())
                                        .build())
                                .build(),
                        red(4)
                                .left(black(1)
                                        .left(black(2).build())
                                        .right(red(6).build())
                                        .build())
                                .right(black(3)
                                        .left(red(7).build())
                                        .right(black(5).build())
                                        .build())
                                .build()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createBalanceLeftTests")
    void should_balance_left(String caseName, Node target, Node expected) {
        assertEquals(expected, balanceLeft(target), caseName);
    }
}