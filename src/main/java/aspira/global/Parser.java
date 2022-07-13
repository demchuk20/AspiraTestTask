package aspira.global;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    private final String HOME = "https://leon.bet/ru/bets";
    private final String LEON = "https://leon.bet";
    private final int TIME_OUT = 10;
    private WebDriver driver;
    private WebDriverWait driverWait;

    public Parser() {
        System.setProperty("webdriver.chrome.driver", "D://chromedriver.exe");
        driver = new ChromeDriver();
        driverWait = new WebDriverWait(driver, Duration.ofSeconds(TIME_OUT));
    }

    public Document getDocument(String url, String selector) {
        driver.get(url);
        driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
        Document document = Jsoup.parse(driver.getPageSource());
        return document;
    }

    public Map<String, String> getSportUrls() {
        Map<String, String> sportUrls = new HashMap<>();
        Document main = getDocument(HOME, "a.sport-event-list-filter__item");
        Elements links = main.select("a.sport-event-list-filter__item");
        for (Element el : links) {
            String href = el.attr("href");
            sportUrls.put(href.replace("/ru/bets/", ""), LEON.concat(href));
        }
        return sportUrls;
    }

    public Map<String, String> getLeagueLinks(String url) {
        Map<String, String> leagueLinks = new HashMap<>();
        Document document = getDocument(url, "a.sport-event-list-filter__item");
        Elements links = document.select("li.leagues-list-item");
        for (Element el : links) {
            String href = el.selectFirst("a[href]").attr("href");
            leagueLinks.put(href.split("/")[4], LEON.concat(href));
        }
        return leagueLinks;
    }

    public Map<String, String> getFirstEvent(String url) {
        Map<String, String> leagueLinks = new HashMap<>();

        Document document = getDocument(url, "span.sport-event-list-sport-headline__label");
        Elements links = document.select("li.leagues-list-item");
        for (Element el : links) {
            String href = el.selectFirst("a[href]").attr("href");
            leagueLinks.put(href.split("/")[4], LEON.concat(href));
        }
        return leagueLinks;
    }

    public void parse() {
        Map<String, String> sportUrls = getSportUrls();
        Map<String, String> soccerLeagues = getLeagueLinks(sportUrls.get("soccer"));
        soccerLeagues.get("england");
        System.out.println(soccerLeagues.keySet());
        System.out.println(soccerLeagues.values());
    }
}
