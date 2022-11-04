package stats;

import main.InputOutput;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import parse.WhatsAppChat;
import parse.WhatsAppMessage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Charts {
    private final InputOutput io;
    private final ArrayList<WhatsAppMessage> messages;
    private final WhatsAppChat chat;

    public Charts(InputOutput io, WhatsAppChat chat) {
        this.io = io;
        this.messages = chat.getRegularMessages();
        this.chat = chat;
    }

    public void allCharts() {
        totalMessagePerHour();
        averageMessagePerHour();
        averageMessageUserHourLineChart();
        messagesPerDayChart();
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
        long timespanDays = messageHistoryTimespanDays(messages);
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

    private long messageHistoryTimespanDays(ArrayList<WhatsAppMessage> messages) {
        return latestDate(messages) - earliestDate(messages);
    }

    protected static long earliestDate(ArrayList<WhatsAppMessage> messages) {
        long earliest = messages.get(0).getEpochDate();
        for (WhatsAppMessage message : messages) {
            long epochDate = message.getEpochDate();
            if (epochDate < earliest) {
                earliest = epochDate;
            }
        }
        return earliest;
    }

    protected static long latestDate(ArrayList<WhatsAppMessage> messages) {
        long latest = messages.get(0).getEpochDate();
        for (WhatsAppMessage message : messages) {
            long epochDate = message.getEpochDate();
            if (epochDate > latest) {
                latest = epochDate;
            }
        }
        return latest;
    }

    private void averageMessageUserHourLineChart() {
        HashMap<String, Map<Integer, Integer>> fullMap = new HashMap<>();
        for (String author : chat.getAuthorList()) {
            fullMap.put(author, new HashMap<>());
            for (int hour = 0; hour < 24; hour++) {
                fullMap.get(author).put(hour, 0);
            }
        }
        for (WhatsAppMessage message : messages) {
            fullMap.get(message.getAuthor()).replace(message.getTime().getHour(), fullMap.get(message.getAuthor()).get(message.getTime().getHour()) + 1);
        }
        long timespanDays = messageHistoryTimespanDays(messages);
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (Map.Entry<String, Map<Integer, Integer>> entry : fullMap.entrySet()) {
            for (Map.Entry<Integer, Integer> hourEntry : entry.getValue().entrySet()) {
                data.addValue((double) hourEntry.getValue() / (double) timespanDays, entry.getKey(), hourEntry.getKey());
            }
        }
        JFreeChart lineChart =  ChartFactory.createLineChart("Gemiddeld aantal berichten per uur (iedereen voor zich)", "Uur", "berichten", data);
        io.printToPNG(lineChart, 1920, 1080);
    }

    private void messagesPerDayChart() {
        HashMap<String, HashMap<Long, Integer>> authorDayMessageCountMap = new HashMap<>();
        long latestDate = latestDate(messages);
        long earliestDate = earliestDate(messages);
        for (String author : chat.getAuthorList()) {
            authorDayMessageCountMap.put(author, new HashMap<>());
            Map<Long, Integer> currentMap = authorDayMessageCountMap.get(author);
            for (long i = earliestDate; i <= latestDate; i++) {
                currentMap.put(i, 0);
            }
        }
        for (WhatsAppMessage message : messages) {
            long epochDay = message.getDate().toEpochDay();
            HashMap<Long, Integer> thisAuthorMap = authorDayMessageCountMap.get(message.getAuthor());
            thisAuthorMap.replace(epochDay, thisAuthorMap.get(epochDay) + 1);
        }
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (Map.Entry<String, HashMap<Long, Integer>> authorEntry : authorDayMessageCountMap.entrySet()) {
            for (Map.Entry<Long, Integer> dayEntry : authorEntry.getValue().entrySet()) {
                data.addValue(dayEntry.getValue(), authorEntry.getKey(), LocalDate.ofEpochDay(dayEntry.getKey()));
            }
        }
        JFreeChart lineChart =  ChartFactory.createLineChart("Berichtentrend", "dag", "berichten", data);
        lineChart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        io.printToPNG(lineChart, 9600, 1080);
    }
}

