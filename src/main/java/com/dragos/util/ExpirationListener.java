package com.dragos.util;

/**
 * Created by dragos on 19.07.2018.
 */
public interface ExpirationListener<K, V> {

    void entryExpired(K key, V value);
}
