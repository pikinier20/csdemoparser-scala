package demoparser.interfaces;

import demoparser.config.ParserConfigInterface;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public interface DemoParserInterface {
    Future<DemoInterface> parseFromPath(Path path, ParserConfigInterface config, ExecutorService exService);
    Future<DemoInterface> parseFromInputStream(InputStream input, ParserConfigInterface config, ExecutorService exService);
}


