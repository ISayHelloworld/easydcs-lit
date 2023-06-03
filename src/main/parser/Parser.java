package main.parser;

public interface Parser {
    Object parse(String data);
    String getTag();
}
