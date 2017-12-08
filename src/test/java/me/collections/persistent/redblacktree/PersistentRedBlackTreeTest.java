package me.collections.persistent.redblacktree;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static me.collections.persistent.redblacktree.Node.Builder.black;
import static me.collections.persistent.redblacktree.Node.Builder.red;
import static me.collections.persistent.redblacktree.Node.nil;
import static me.collections.persistent.redblacktree.PersistentRedBlackTree.balance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author nickolaysaveliev
 * @since 08/12/2017
 */
class PersistentRedBlackTreeTest {

    @Test
    void should_not_validate_red_root() {
        assertThrows(IllegalStateException.class, () -> new PersistentRedBlackTree(red(1).build()).validate());
    }

    private static Stream<Node> createRedParentAndChildTests() {
        return Stream.of(
                black(4).left(red(3).left(red(1).build()).build()).build(),
                black(1).right(red(2).right(red(3).build()).build()).build()
        );
    }

    @ParameterizedTest
    @MethodSource("createRedParentAndChildTests")
    void should_not_validate_red_parent_and_child(Node x) {
        assertThrows(IllegalStateException.class, () -> PersistentRedBlackTree.checkRedNode(x));
    }

    private static Stream<Node> createDifferentBlackHeightsTests() {
        return Stream.of(
                black(1)
                        .left(black(2).build())
                        .build(),
                black(1)
                        .right(black(2).build())
                        .build(),
                black(1)
                        .left(red(2).build())
                        .right(black(2).build())
                        .build(),
                black(1)
                        .left(black(2).build())
                        .right(red(2).build())
                        .build()
        );
    }

    @ParameterizedTest
    @MethodSource("createDifferentBlackHeightsTests")
    void should_not_validate_different_black_heights(Node x) {
        assertThrows(IllegalStateException.class, () -> PersistentRedBlackTree.checkBlackHeight(x));
    }

    private static Stream<Node> createBrokenBSTTests() {
        return Stream.of(
                black(1).left(black(2).build()).build(),
                black(1).right(black(0).build()).build(),
                black(1)
                        .left(red(1).build())
                        .right(black(0).build())
                        .build(),
                black(1)
                        .left(black(0).build())
                        .right(red(2).
                                left(red(3).build())
                                .build())
                        .build()
        );
    }

    @ParameterizedTest
    @MethodSource("createBrokenBSTTests")
    void should_not_validate_broken_bst(Node x) {
        assertThrows(IllegalStateException.class, () -> PersistentRedBlackTree.checkBST(x));
    }

    private static Stream<Node> createValidateTests() {
        return Stream.of(
                nil(),
                black(1).build(),
                black(2)
                        .left(red(1)
                                .left(black(0)
                                        .right(red(0).build())
                                        .build())
                                .right(black(1).build())
                                .build())
                        .right(black(3).build())
                        .build(),
                black(1)
                    .left(black(1).build())
                    .right(black(2).build())
                .build()
        );
    }

    @ParameterizedTest
    @MethodSource("createValidateTests")
    void should_validate(Node x) {
        new PersistentRedBlackTree(x).validate();
    }

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
        assertEquals(balanceResult, balance(x));
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
    void should_balance_Other(Node x) {
        assertEquals(x, balance(x));
    }

    @Test
    void should_insert() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        PersistentRedBlackTree tree = new PersistentRedBlackTree();
        for (int i = 0; i < 1000; i++) {
            tree = tree.put(random.nextInt());
            tree.validate();
        }
    }
}