package demoparser.interfaces;

public interface ValueInterface<T> extends PrettyPrintable, JsonSerializable {
    public T v();
}
