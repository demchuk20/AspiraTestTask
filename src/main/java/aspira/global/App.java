package aspira.global;

import aspira.global.Entity.Event;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Parser parser = new Parser();
        List<Event> events = parser.parse();
        events.forEach(System.out::println);
    }
}
