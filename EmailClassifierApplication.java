import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailClassifierApplication {

public static void main(String[] args) {
    String csvFilePath = "spam_or_not_spam.csv"; // Update this path
    List<Email> emails = new ArrayList<>();

    int actualSpamCount = 0;
    int predictedSpamCount = 0;
    int correctPredictions = 0;

    try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean isFirstLine = true;
        while ((line = br.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }
            String[] values = line.split(",");
            if (values.length == 2) {
                boolean isActualSpam = Integer.parseInt(values[1]) == 1;
                Email email = new Email(values[0], isActualSpam);
                email.setFeatures(FeatureExtractor.extractFeatures(email));
                emails.add(email);
                if (isActualSpam) {
                    actualSpamCount++;
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    Classifier classifier = new Classifier();
    classifier.train(emails);

    for (Email email : emails) {
        boolean isPredictedSpam = classifier.classify(email);
        if (isPredictedSpam) {
            predictedSpamCount++;
        }
        if (email.isSpam() == isPredictedSpam) {
            correctPredictions++;
        }
    }

    int totalEmails = emails.size();
    double accuracy = (double) correctPredictions / totalEmails;

    System.out.println("Total emails: " + totalEmails);
    System.out.println("Actual spam emails: " + actualSpamCount);
    System.out.println("Predicted spam emails: " + predictedSpamCount);
    System.out.println("Model accuracy: " + accuracy);
}

   private static String getFeatureString(Map<String, Integer> features) {
      return String.join(",", 
            features.getOrDefault("WordCount", 0).toString(),
            features.getOrDefault("SpamWordCount", 0).toString(),
            features.getOrDefault("MisspelledCount", 0).toString(),
            features.getOrDefault("SpecialCharCount", 0).toString(),
            features.getOrDefault("UrlCount", 0).toString(),
            features.getOrDefault("AllCapsCount", 0).toString());
   }

   private static void updateSummaryStats(Map<String, Integer> features, Map<String, SummaryStats> summaryStatsMap) {
      features.forEach(
            (key, value) -> {
               SummaryStats stats = summaryStatsMap.computeIfAbsent(key, k -> new SummaryStats());
               stats.update(value);
            });
   }

   private static void safeWriteSummaryStats(String feature, SummaryStats stats, BufferedWriter writer) {
      try {
         writeSummaryStats(feature, stats, writer);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private static void writeSummaryStats(String feature, SummaryStats stats, BufferedWriter writer) throws IOException {
      writer.write(String.format("%s,%d,%d,%.2f\n", feature, stats.getMin(), stats.getMax(), stats.getAverage()));
   }

   static class SummaryStats {
      private int min = Integer.MAX_VALUE;
      private int max = Integer.MIN_VALUE;
      private long sum = 0;
      private int count = 0;
   
      public void update(int value) {
         if (value < min) min = value;
         if (value > max) max = value;
         sum += value;
         count++;
      }
   
      public int getMin() {
         return min;
      }
   
      public int getMax() {
         return max;
      }
   
      public double getAverage() {
         return count == 0 ? 0 : (double) sum / count;
      }
   }
}
