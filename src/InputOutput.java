import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class InputOutput {
    private final File inputFile;
    private final File outputDirectory;
    private FileWriter fileWriter;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public InputOutput(File inputFile) throws FileNotFoundException {

        this.inputFile = inputFile;
        String outputPath = inputFile.getParent() + File.separator + inputFile.getName().split("\\.")[0] + "__report";
        File outputDirectory1 = new File(outputPath);

        try {
            if (outputDirectory1.isDirectory() && outputDirectory1.listFiles() != null) {
                for (File file : outputDirectory1.listFiles()) {
                    file.delete();
                }
            }
            Files.deleteIfExists(Paths.get(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        this.outputDirectory = new File(outputPath);;
        if(!this.outputDirectory.mkdir() && !this.outputDirectory.isDirectory()) {
            throw new FileNotFoundException();
        }
        setFileName("default");
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

    public void output(String output) {
        println(output);
        writeToFile(output);
    }

    public void setFileName(String fileName) {
        if (null != fileWriter) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Files.deleteIfExists(Path.of(outputDirectory.getAbsolutePath() + File.separator + fileName));
            File newFile = new File(outputDirectory.getAbsolutePath() + File.separator + fileName);
            fileWriter = new FileWriter(newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
