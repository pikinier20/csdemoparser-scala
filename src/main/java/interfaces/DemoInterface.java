package demoparser.interfaces;

import demoparser.model.Header;

import java.util.List;

public interface DemoInterface extends PrettyPrintable {
    HeaderInterface header();
    List<GameEventInterface> eventsList();
}
