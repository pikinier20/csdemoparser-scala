package demoparser.interfaces;

import demoparser.model.Header;

import java.util.List;

public interface DemoInterface {
    HeaderInterface header();
    List<GameEventInterface> eventsList();
}
