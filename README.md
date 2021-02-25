# CS Demo Parser

CS Demo Parser is a Scala library for retrieving information from CS:GO .dem files.

## Usage
CS Demo Parser exhibits interface to use it from Scala and Java.
Java:
```java
//Obtain parser interface by call
DemoParserInterface parser = DemoParsing.getParser();

//Parser API
public interface DemoParserInterface {
    Future<DemoInterface> parseFromPath(Path path, ParserConfigInterface config, ExecutorService exService);
    Future<DemoInterface> parseFromInputStream(InputStream input, ParserConfigInterface config, ExecutorService exService);
}
//Future will throw DemoParsingException(String reason) on error

//For ParserConfigInterface you can use JavaParserConfig
class JavaParserConfig {
    public JavaParserConfig(Set<String> _ignoredGameEvents);
}
```
Scala:
```scala
//Use DemoParser companion object for parsing demos
object DemoParser {
  def parseFromPath(path: Path, config: ParserConfigInterface)(implicit ec: ExecutionContext): Future[Either[String, Demo]]
  def parseFromInputStream(inputStream: InputStream, config: ParserConfigInterface)(implicit ec: ExecutionContext): Future[Either[String, Demo]]
}
//Instead of throwing exception it returns Either[String, Demo]

//Instead of interfaces you can use model implemented with case classes
case class Demo(header: Header, events: Seq[GameEvent])
case class Header(/*...*/)
case class GameEvent(name: String, tick: Int, keys: Map[String, Value[_]])
sealed trait Value[T]

//For config you can use ParserConfig case class
case class ParserConfig(ignoredGameEvents: Set[String])
```

Model classes also implement `PrettyPrintable` and `JsonSerializable` interfaces.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## Credits
Parsing algorithm used in this project was taken from [demofile](https://github.com/saul/demofile).


## License
[MIT](https://choosealicense.com/licenses/mit/)