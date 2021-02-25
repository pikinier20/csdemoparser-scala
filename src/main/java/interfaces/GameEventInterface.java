package demoparser.interfaces;

import demoparser.model.Value;

import java.util.Map;

public interface GameEventInterface extends PrettyPrintable, JsonSerializable {
    String name();
    int tick();
    Map<String, ValueInterface<?>> keysMap();
}
