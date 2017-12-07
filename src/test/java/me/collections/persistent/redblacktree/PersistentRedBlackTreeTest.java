package me.collections.persistent.redblacktree;

import org.junit.jupiter.api.Test;

import static me.collections.persistent.redblacktree.Node.Color.BLACK;
import static me.collections.persistent.redblacktree.Node.Color.RED;
import static me.collections.persistent.redblacktree.Node.nil;
import static me.collections.persistent.redblacktree.PersistentRedBlackTree.balance;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author nickolaysaveliev
 * @since 08/12/2017
 */
class PersistentRedBlackTreeTest {

    private final Node balanceResult = new Node(2, new Node(1, nil(), nil(), BLACK), new Node(3, nil(), nil(), BLACK), RED);

    @Test
    void balanceLeftLeftCase() {
        Node x = new Node(1, nil(), nil(), RED);
        Node y = new Node(2, x, nil(), RED);
        Node z = new Node(3, y, nil(), BLACK);
        Node result = balance(z);
        assertEquals(balanceResult, result);
    }

    @Test
    void balanceLeftRightCase() {
        Node y = new Node(2, nil(), nil(), RED);
        Node x = new Node(1, nil(), y, RED);
        Node z = new Node(3, x, nil(), BLACK);
        Node result = balance(z);
        assertEquals(balanceResult, result);
    }

    @Test
    void balanceRightLeftCase() {
        Node y = new Node(2, nil(), nil(), RED);
        Node z = new Node(3, y, nil(), RED);
        Node x = new Node(1, nil(), z, BLACK);
        Node result = balance(x);
        assertEquals(balanceResult, result);
    }

    @Test
    void balanceRightRightCase() {
        Node z = new Node(3, nil(), nil(), RED);
        Node y = new Node(2, nil(), z, RED);
        Node x = new Node(1, nil(), y, BLACK);
        Node result = balance(x);
        assertEquals(balanceResult, result);
    }

    @Test
    void balanceOther() {
        assertEquals(nil(), balance(nil()));
        Node z = new Node(3, nil(), nil(), RED);
        assertEquals(z, balance(z));
        Node y = new Node(2, nil(), z, RED);
        assertEquals(y, balance(y));
        Node x = new Node(1, z, nil(), BLACK);
        assertEquals(x, balance(x));
    }
}