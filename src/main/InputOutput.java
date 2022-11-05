package main;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class InputOutput {
    private final String inputFileName;
    private final File outputDirectory;
    private FileWriter fileWriter;

    public InputOutput(File inputFile) {
        this.inputFileName = inputFile.getName();
        String outputPath = inputFile.getParent() + File.separator + inputFile.getName().split("\\.")[0] + "__report";
        deleteDirectory(new File(outputPath));
        this.outputDirectory = new File(outputPath);
        if(!this.outputDirectory.mkdir() && !this.outputDirectory.isDirectory()) {
            System.out.println("File stuff not working");
            System.exit(1);
        }
        setFileName("default");
    }

    public InputOutput(File outputDirectory, String chatname) {
        this.inputFileName = chatname;
        String outputPath = outputDirectory + File.separator + chatname.split("\\.")[0] + "-report";
        deleteDirectory(new File(outputPath));
        this.outputDirectory = new File(outputPath);
        if(!this.outputDirectory.mkdir() && !this.outputDirectory.isDirectory()) {
            System.out.println("File stuff not working");
            System.exit(1);
        }
        setFileName("default");
    }

    private void deleteDirectory(File directory) {
        if (directory != null) {
            File[] fileList = directory.listFiles();
            if (fileList != null && fileList.length > 0) {
                for (File file : fileList) {
                    deleteDirectory(file);
                }
            }
            directory.delete();
        }
    }

    public void println(String output) {
        System.out.println(output);
    }

    public void writeToFile(String output) {
        try {
            fileWriter.write(output + "\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String fileName, String output) {
        try {
            FileWriter fw = new FileWriter(new File(outputDirectory, fileName));
            fw.write(output);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void output(String output) {
        println(output);
        writeToFile(output);
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setFileName(String fileName) {
        if (!fileName.endsWith(".txt") && !fileName.endsWith(".md")) {
            fileName = fileName.split("\\.")[0] + ".md";
        }
        if (null != fileWriter) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String outputPath = outputDirectory.getAbsolutePath() + File.separator + fileName;
        try {
            deleteDirectory(new File(outputPath));
            File newFile = new File(outputPath);
            fileWriter = new FileWriter(newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printToPNG(JFreeChart chart, int width, int height) {
        String fileName = chart.getTitle().getText();
        if (!fileName.endsWith(".png")) {
            fileName = fileName.split("\\.")[0] + ".png";
        }
        File outputFile = new File(outputDirectory.getAbsolutePath() + File.separator + fileName);
        try {
            ChartUtils.saveChartAsPNG(outputFile, chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printToPNG(JFreeChart chart) {
        printToPNG(chart, 960, 540);
    }
}