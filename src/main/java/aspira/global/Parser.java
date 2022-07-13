package aspira.global;

import aspira.global.Entity.Event;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    private final String HOME = "https://leon.bet/ru/bets";
    private final String LEON = "https://leon.bet";
    private List<String> SPORTS;
    private final int TIME_OUT = 10;
    private WebDriver driver;
    private WebDriverWait driverWait;

    public Parser() {
        System.setProperty("webdriver.chrome.driver", "D://chromedriver.exe");
        driver = new ChromeDriver();
        driverWait = new WebDriverWait(driver, Duration.ofSeconds(TIME_OUT));
        SPORTS = new ArrayList<>();
        SPORTS.add("soccer");
        SPORTS.add("tennis");
        SPORTS.add("hockey");
        SPORTS.add("basketball");
    }

    private Document getDocument(String url, String selector) {
        driver.get(url);
        driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
        Document document = Jsoup.parse(driver.getPageSource());
        return document;
    }

    private Map<String, String> getSportLinks() {
        Map<String, String> sportUrls = new HashMap<>();
        Document main = getDocument(HOME, "a.sport-event-list-filter__item");
        Elements links = main.select("a.sport-event-list-filter__item");
        for (Element el : links) {
            String href = el.attr("href");
            sportUrls.put(href.replace("/ru/bets/", ""), LEON.concat(href));
        }
        return sportUrls;
    }

    private Map<String, String> getLeagueLinks(String url) {
        Map<String, String> leagueLinks = new HashMap<>();
        Document document = getDocument(url, "a.sport-event-list-filter__item");
        Elements links = document.select("li.leagues-list-item");
        for (Element el : links) {
            String href = el.selectFirst("a[href]").attr("href");
            leagueLinks.put(href.split("/")[4], LEON.concat(href));
        }
        return leagueLinks;
    }

    private Optional<Event> getFirstEvent(String url) {
        Document document = getDocument(url, "span.sport-event-list-sport-headline__label");
        Element dropDown = document.selectFirst("a.sport-event-list-item-market__more-indicator");
        if (dropDown == null)
            return Optional.empty();
        driver.findElement(By.cssSelector("a.sport-event-list-item-market__more-indicator")).click();
        driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section.sport-event-details-market-list__block")));
        document = Jsoup.parse(driver.getPageSource());
        Element element = document.selectFirst("div.sport-event-list-item__block");

        List<String> competitors = element.select("span.sport-event-list-item-competitor__name")
                .stream()
                .map(Element::text)
                .collect(Collectors.toList());
        String dateString = element.selectFirst("span.kickoff-countdown__date").text().concat(String.format(".%s", Year.now()));
        String timeString = element.selectFirst("span.kickoff-countdown__time").text();
        Date date = null;
        Time time = null;
        try {
            date = new SimpleDateFormat("dd.MM.yyyy").parse(dateString);
            time = new Time(new SimpleDateFormat("hh:mm").parse(timeString).getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Can't parse data", e);
        }
        Elements sections = document.select("section.sport-event-details-market-list__block");
        List<String> winner = sections.get(0)
                .select("span.sport-event-list-item-market__coefficient--right")
                .stream()
                .map(Element::text)
                .collect(Collectors.toList());
        List<String> doubleChance;
        if (winner.size() == 3)
            doubleChance = sections.get(1)
                    .select("span.sport-event-list-item-market__coefficient--right")
                    .stream()
                    .map(Element::text)
                    .collect(Collectors.toList());
        else
            doubleChance = winner;
        return Optional.of(new Event(competitors, date, time, winner, doubleChance));
    }

    public List<Event> parse() {
        List<Event> events = new ArrayList<>();
        Map<String, String> sportLinks = getSportLinks();
        for (String sport : SPORTS) {
            Map<String, String> leagueLinks = getLeagueLinks(sportLinks.get(sport));
            for (String league : leagueLinks.keySet()) {
                Optional<Event> optionalEvent = getFirstEvent(leagueLinks.get(league));
                if (optionalEvent.isPresent()) {
                    Event firstEvent = optionalEvent.get();
                    firstEvent.setSport(sport);
                    firstEvent.setLeague(league);
                    events.add(firstEvent);
                }
            }
        }
        return events;
    }
}
