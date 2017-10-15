package io.dlminer.ner.utils;

/**
 *
 */
public class Validate {

    public static void notNull(final Object object) {
        if (object == null) {
            throw new NullPointerException("Object is null");
        }
    }

}
