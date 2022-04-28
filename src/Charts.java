import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetGroup;

import java.util.ArrayList;

public class Charts {
    private final InputOutput io;
    private final ArrayList<WhatsAppMessage> messages;

    public Charts(InputOutput io, ArrayList<WhatsAppMessage> allStandardMessages) {
        this.io = io;
        this.messages = allStandardMessages;
    }

    public void allCharts() {

    }

    public void averageMessagePerHour() {

    }
    
}
