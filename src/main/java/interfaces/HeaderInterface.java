package demoparser.interfaces;

public interface HeaderInterface extends PrettyPrintable {
    String magic();
    int protocol();
    int networkProtocol();
    String serverName();
    String clientName();
    String mapName();
    String gameDirectory();
    float playbackTime();
    int playbackTicks();
    int playbackFrames();
    int signonLength();
}
