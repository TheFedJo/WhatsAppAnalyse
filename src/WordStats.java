import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordStats {
    private final InputOutput io;

    public WordStats(InputOutput io) {
        this.io = io;
    }

    public void allStats(Map<String, Map<String, Integer>> authorWordMap) {
        generalWordTopTen(authorWordMap);

        wordCountPerAuthorAndFavoriteWords(authorWordMap);
    }

    private void generalWordTopTen(Map<String, Map<String, Integer>> authorWordMap) {
        Map<String, Integer> generalWordMap = new HashMap<>();
        for (Map<String, Integer> wordMap : authorWordMap.values()) {
            for (Map.Entry<String, Integer> entry : wordMap.entrySet()) {
                if (!generalWordMap.containsKey(entry.getKey())) {
                    generalWordMap.put(entry.getKey(), entry.getValue());
                } else {
                    generalWordMap.replace(entry.getKey(), generalWordMap.get(entry.getKey()) + entry.getValue());
                }
            }
        }
    }

    private void wordCountPerAuthorAndFavoriteWords(Map<String, Map<String, Integer>> authorWordMap) {
        Map<String, ArrayList<Map.Entry<String, Integer>>> favoriteWordsPerAuthor = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> authorWordEntry : authorWordMap.entrySet()) {
            ArrayList<Map.Entry<String, Integer>> sortedFavoriteWords = SortMethods.mergeSort(new ArrayList<>(authorWordEntry.getValue().entrySet()), new EntryComparator());
            favoriteWordsPerAuthor.put(authorWordEntry.getKey(), new ArrayList<>(sortedFavoriteWords.subList(0, Math.min(10, sortedFavoriteWords.size()))));
        }
        Map<String, Integer> wordCountPerAuthor = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> authorWordEntry : authorWordMap.entrySet()) {
            int countForCurrentAuthor = 0;
            for(int wordCount : authorWordEntry.getValue().values()) {
                countForCurrentAuthor += wordCount;
            }
            wordCountPerAuthor.put(authorWordEntry.getKey(), countForCurrentAuthor);
        }
        ArrayList<Map.Entry<String, Integer>> totalWordCountPerAuthor = SortMethods.mergeSort(new ArrayList<>(wordCountPerAuthor.entrySet()), new EntryComparator());
        int i;
        for (Map.Entry<String, Integer> entry : totalWordCountPerAuthor) {
            io.output("\n" + entry.getKey() + " heeft " + entry.getValue() + " woorden uitgekraamd.\nDit zijn de favorieten:");
            i = 1;
            for (Map.Entry<String, Integer> wordPlusCount : favoriteWordsPerAuthor.get(entry.getKey())) {
                io.output(i + ". " + wordPlusCount.getKey() + ", " + wordPlusCount.getValue() + " keer gezegd.");
                i++;
            }
        }
    }
}
