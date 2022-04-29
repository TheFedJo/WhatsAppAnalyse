import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Charts {
    private final InputOutput io;
    private final ArrayList<WhatsAppMessage> messages;

    public Charts(InputOutput io, ArrayList<WhatsAppMessage> allStandardMessages) {
        this.io = io;
        this.messages = allStandardMessages;
    }

    public void allCharts() {
        totalMessagePerHour();
        averageMessagePerHour();
        averageMessageUserHourLineChart();

    }

    public void averageMessagePerHour() {
        JFreeChart barChart = ChartFactory.createBarChart("Gemiddeld aantal berichten per uur op een dag", "Uren", "Berichten", averageMessagePerHourDataset());
        io.printToPNG(barChart);
        JFreeChart lineChart = ChartFactory.createLineChart("Gemiddeld aantal berichten per uur op een dag (lijn)", "Uren", "Berichten", averageMessagePerHourDataset());
        io.printToPNG(lineChart);
    }

    public void totalMessagePerHour() {
        Map<Integer, Integer> hourMessageCountMap = messageMapPerHour(this.messages);
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        for(Map.Entry<Integer, Integer> entry : hourMessageCountMap.entrySet()) {
            data.addValue(entry.getValue(), "messages", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart("Totaal aantal berichten per uur uit " + io.getInputFileName(), "Uren", "Berichten", data);
        io.printToPNG(chart);
    }

    private Map<Integer, Integer> messageMapPerHour(ArrayList<WhatsAppMessage> messages) {
        Map<Integer, Integer> hourMessageCountMap = new HashMap<>();
        int hour;
        for (WhatsAppMessage message : messages) {
            hour = message.getTime().getHour();
            if (hourMessageCountMap.containsKey(hour)) {
                hourMessageCountMap.replace(hour, hourMessageCountMap.get(hour) + 1);
            } else {
                hourMessageCountMap.put(hour, 1);
            }
        }
        return hourMessageCountMap;
    }

    public CategoryDataset averageMessagePerHourDataset() {
        Map<Integer, Integer> hourMessageCountMap = messageMapPerHour(this.messages);
        long timespanDays = messageHistoryTimespanDays();
        Map<Integer, Double> averageMessagePerHourMap = new HashMap<>(24);
        for (Map.Entry<Integer, Integer> entry : hourMessageCountMap.entrySet()) {
            averageMessagePerHourMap.put(entry.getKey(), (double) entry.getValue() / timespanDays);
        }
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (Map.Entry<Integer, Double> entry : averageMessagePerHourMap.entrySet()) {
            data.addValue(entry.getValue(), "average messages per hour", entry.getKey());
        }
        return data;
    }

    private long messageHistoryTimespanDays() {
        LocalDateTime earliest = messages.get(0).getDateTime();
        LocalDateTime latest = messages.get(0).getDateTime();
        for (WhatsAppMessage message : messages) {
            if (message.getDateTime().isBefore(earliest)) {
                earliest = message.getDateTime();
            } else if (message.getDateTime().isAfter(latest)) {
                latest = message.getDateTime();
            }
        }
        return latest.getLong(ChronoField.EPOCH_DAY) - earliest.getLong(ChronoField.EPOCH_DAY);
    }

    private void averageMessageUserHourLineChart() {
        HashMap<String, Map<Integer, Integer>> fullMap = new HashMap<>();
        for (String author : Main.createAuthorList(messages)) {
            fullMap.put(author, new HashMap<>());
            for (int hour = 0; hour < 24; hour++) {
                fullMap.get(author).put(hour, 0);
            }
        }
        for (WhatsAppMessage message : messages) {
            fullMap.get(message.getAuthor()).replace(message.getTime().getHour(), fullMap.get(message.getAuthor()).get(message.getTime().getHour()) + 1);
        }
        long timespanDays = messageHistoryTimespanDays();
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (Map.Entry<String, Map<Integer, Integer>> entry : fullMap.entrySet()) {
            for (Map.Entry<Integer, Integer> hourEntry : entry.getValue().entrySet()) {
                data.addValue((double) hourEntry.getValue() / (double) timespanDays, entry.getKey(), hourEntry.getKey());
            }
        }
        JFreeChart lineChart =  ChartFactory.createLineChart("Gemiddeld aantal berichten per uur (iedereen voor zich)", "Uur", "berichten", data);
        io.printToPNG(lineChart);
        io.printToPNG(lineChart, 3840, 2160);
    }
}
