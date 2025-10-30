package flaxbeard.cyberware.common.misc;

import java.util.Stack;

// http://stackoverflow.com/a/16206356/1754640
public class SizedStack<T> extends Stack<T> {
    private final int maxSize;

    public SizedStack(int size) {
        super();
        this.maxSize = size;
    }

    @Override
    public T push(T object) {
        while (this.size() >= maxSize) {
            this.remove(0);
        }
        return super.push(object);
    }
}
