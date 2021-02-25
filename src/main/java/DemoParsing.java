package demoparser;

import demoparser.interfaces.DemoParserInterface;
import demoparser.parser.DemoParser$;

public class DemoParsing {
    public static DemoParserInterface getParser() {
        return DemoParser$.MODULE$;
    }
}
