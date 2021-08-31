package com.github.ys3d.printbot;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for managing a whitelist of users
 * @author Daniel Schild
 */
public class Whitelist {
    private final Set<String> whitelist;

    /**
     * Initializes a new whitelist
     * @param whitelist The set of strings on the whitelist
     */
    public Whitelist(Set<String> whitelist) {
        this.whitelist = whitelist;
    }

    /**
     * Initializes a new empty whitelist
     */
    public Whitelist() {
        this.whitelist = new HashSet<>();
    }

    /**
     * Initializes a new whitelist
     * @param stream A {@link InputStream} of a file containing all names of the whitelist. The file should contain one name per line.
     * @throws IOException If the given stream von not be read properly
     */
    public Whitelist(InputStream stream) throws IOException {
        this.whitelist = new HashSet<>();
        String whitelistString = IOUtils.toString(stream, StandardCharsets.UTF_8);
        this.whitelist.addAll(Arrays.asList(whitelistString.split("\r\n")));
    }

    /**
     * Checks if the given name is whitelisted
     * @param name The name to check
     * @return True, if the name is on the whitelist. False otherwise
     */
    public boolean isWhitelisted(String name) { return whitelist.contains(name); }

    /**
     * Returns the number of whitelisted names
     * @return The number of whitelisted names
     */
    public int size() { return whitelist.size(); }

    /**
     * Adds a name to the whitelist
     * @param name The name that should be added to the whitelist
     */
    public void addName(String name) { whitelist.add(name); }

}
