package com.github.ys3d.printbot;

import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Tests {@link Whitelist}
 * @author Daniel Schild
 */
public class WhitelistTest {

    @Test
    public void initWithSetTest() {
        HashSet<String> set = new HashSet<>();
        set.add("Name1");
        set.add("Name2");

        Whitelist wl = new Whitelist(set);
        assertTrue(wl.isWhitelisted("Name1"));
        assertTrue(wl.isWhitelisted("Name2"));
        assertFalse(wl.isWhitelisted("Name3"));
    }

    @Test
    public void initWithFileTest() throws IOException {
        Whitelist wl = new Whitelist(Thread.currentThread().getContextClassLoader().getResourceAsStream("test_whitelist.txt"));
        assertTrue(wl.isWhitelisted("Name1"));
        assertTrue(wl.isWhitelisted("Name2"));
        assertFalse(wl.isWhitelisted("Name3"));
    }

    @Test
    public void initBlankTest() {
        Whitelist wl = new Whitelist();
        assertEquals(0, wl.size());
    }

    @Test
    public void sizeTest() {
        Whitelist wl = new Whitelist();
        assertEquals(0, wl.size());
        wl.addName("test");
        assertEquals(1, wl.size());

        HashSet<String> set = new HashSet<>();
        set.add("Name1");
        set.add("Name2");
        wl = new Whitelist(set);
        assertEquals(2, wl.size());
    }

    @Test
    public void addNameTest() {
        Whitelist wl = new Whitelist();
        assertFalse(wl.isWhitelisted("test"));
        wl.addName("test");
        assertTrue(wl.isWhitelisted("test"));
    }
}
