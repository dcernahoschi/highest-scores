package com.dragos.util;

/**
 * Created by dragos on 19.07.2018.
 */
public class Tuple<T1, T2> {

    private T1 first;
    private T2 second;

    public Tuple(T1 first, T2 second) {

        this.first = first;
        this.second = second;
    }

    public T1 getFirst() {

        return first;
    }

    public T2 getSecond() {

        return second;
    }

    public void setFirst(T1 first) {

        this.first = first;
    }

    public void setSecond(T2 second) {

        this.second = second;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple<?, ?> tuple = (Tuple<?, ?>) o;

        if (first != null ? !first.equals(tuple.first) : tuple.first != null) return false;
        return second != null ? second.equals(tuple.second) : tuple.second == null;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {

        return "Tuple{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
