package aspira.global.Entity;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class Event {
    private List<String> competitors;
    private Date date;
    private Time time;
    private List<String> winner;
    private List<String> doubleChance;
    private String sport;
    private String league;

    public Event() {
    }

    public Event(List<String> competitors, Date date, Time time, List<String> winner, List<String> doubleChance) {
        this.competitors = competitors;
        this.date = date;
        this.time = time;
        this.winner = winner;
        this.doubleChance = doubleChance;
    }

    public Event(List<String> competitors, Date date, Time time, List<String> winner, List<String> doubleChance, String sport, String league) {
        this.competitors = competitors;
        this.date = date;
        this.time = time;
        this.winner = winner;
        this.doubleChance = doubleChance;
        this.sport = sport;
        this.league = league;
    }

    public List<String> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(List<String> competitors) {
        this.competitors = competitors;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public List<String> getWinner() {
        return winner;
    }

    public void setWinner(List<String> winner) {
        this.winner = winner;
    }

    public List<String> getDoubleChance() {
        return doubleChance;
    }

    public void setDoubleChance(List<String> doubleChance) {
        this.doubleChance = doubleChance;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    @Override
    public String toString() {
        String format = String.format("%s, %s\n" +
                        "\t%s, %s %s\n" +
                        "\t\tWinner\n",
                sport, league,
                competitors.stream()
                        .reduce(((s1, s2) -> String.format("%s - %s", s1, s2)))
                        .orElseThrow(() -> new RuntimeException("No competitors")),
                date.toString(),
                time.toString());
        if (winner.size() == 3)
            format = format.concat(String.format("\t\t\t1, %s\n" +
                            "\t\t\tX, %s\n" +
                            "\t\t\t2, %s\n" +
                            "\t\tDouble Chance\n" +
                            "\t\t\t1X, %s\n" +
                            "\t\t\t12, %s\n" +
                            "\t\t\t2X, %s\n",
                    winner.get(0),
                    winner.get(1),
                    winner.get(2),
                    doubleChance.get(0),
                    doubleChance.get(1),
                    doubleChance.get(2)));
        else if (winner.size() == 2)
            format = format.concat(String.format("\t\t\t1, %s\n" +
                            "\t\t\t2, %s\n",
                    winner.get(0),
                    winner.get(1)));
        return format;
    }
}
