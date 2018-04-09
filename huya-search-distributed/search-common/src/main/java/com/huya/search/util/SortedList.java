package com.huya.search.util;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class SortedList<T> {

    private LinkedList<T> list = new LinkedList<>();

    public boolean add(T t) {
        if (list.size() == 0) return list.add(t);
        for (int i = 0; i < list.size(); i++) {
            T temp = list.get(i);
            if (compare(t, temp) > 0) {
                list.add(i, t);
                return true;
            }
        }
        return list.add(t);
    }

    public Iterator<T> descIterator() {
        return new DescIterator(list);
    }

    public Iterator<T> ascIterator() {
        return new AscIterator(list);
    }

    public abstract int compare(T t1, T t2);

    private class DescIterator implements Iterator<T> {

        private List<T> temp;

        private int i;

        DescIterator(List<T> list) {
            this.temp = list;
            this.i = 0;
        }

        @Override
        public boolean hasNext() {
            return i < temp.size();
        }

        @Override
        public T next() {
            return temp.get(i++);
        }
    }

    private class AscIterator implements Iterator<T> {

        private List<T> temp;

        private int i;

        AscIterator(List<T> list) {
            this.temp = list;
            this.i = list.size() - 1;
        }

        @Override
        public boolean hasNext() {
            return i >= 0;
        }

        @Override
        public T next() {
            return temp.get(i--);
        }
    }
}
