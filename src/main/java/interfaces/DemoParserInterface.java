package demoparser.interfaces;

import demoparser.config.ParserConfigInterface;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public interface DemoParserInterface {
    DemoInterface parseFromPathJ(Path path, ParserConfigInterface config);
    DemoInterface parseFromInputStreamJ(InputStream input, ParserConfigInterface config);
}


