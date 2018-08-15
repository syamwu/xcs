package syamwu.xchushi.fw.common.util;

public class BooleanData<T> {
    private T data;

    private boolean bl;

    public BooleanData(T data, boolean bl) {
        this.data = data;
        this.bl = bl;
    }

    public boolean success() {
        return bl;
    }

    public T data() {
        return data;
    }
}
