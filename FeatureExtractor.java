import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureExtractor {

    private static final Set<String> spamWords = new HashSet<>(Arrays.asList("free", "offer", "winner")); // Add more typical spam words here
    private static final Set<String> dictionary = new HashSet<>();

    static {
        // Load dictionary words from a CSV file
        String dictionaryFilePath = "dictionary.csv"; // Update this to the path of your dictionary file
        try (BufferedReader br = new BufferedReader(new FileReader(dictionaryFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Assuming each line in the CSV contains a single word
                dictionary.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.err.println("Error reading dictionary file: " + e.getMessage());
        }

        // Initialize spamWords set with typical spam words
        // You can also consider loading this from an external source
    }

    public static Map<String, Integer> extractFeatures(Email email) {
        Map<String, Integer> features = new HashMap<>();
        String[] words = email.getRawText().split("\\s+");
        
        int spamWordCount = 0;
        int misspelledCount = 0;
        int specialCharCount = 0;
        int urlCount = 0;
        int allCapsCount = 0;

        Pattern urlPattern = Pattern.compile("http[s]?://\\S+");
        Matcher urlMatcher;

        for (String word : words) {
            if (spamWords.contains(word.toLowerCase())) {
                spamWordCount++;
            }
            if (!dictionary.contains(word.toLowerCase())) {
                misspelledCount++;
            }
            if (word.matches(".*[!@#$%^&*()_+].*")) {
                specialCharCount++;
            }
            if (word.equals(word.toUpperCase())) {
                allCapsCount++;
            }
            urlMatcher = urlPattern.matcher(word);
            if (urlMatcher.find()) {
                urlCount++;
            }
        }

        features.put("spamWordCount", spamWordCount);
        features.put("wordCount", words.length);
        features.put("misspelledCount", misspelledCount);
        features.put("specialCharCount", specialCharCount);
        features.put("urlCount", urlCount);
        features.put("allCapsCount", allCapsCount);

        return features;
    }
}
