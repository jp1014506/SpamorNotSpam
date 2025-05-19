import java.util.*;

public class Classifier {
    private Map<String, Double> averageSpamFeatures;
    private Map<String, Double> averageNonSpamFeatures;

    public Classifier() {
        averageSpamFeatures = new HashMap<>();
        averageNonSpamFeatures = new HashMap<>();
    }

    public void train(List<Email> emails) {
        Map<String, Double> totalSpamFeatures = new HashMap<>();
        Map<String, Double> totalNonSpamFeatures = new HashMap<>();
        int spamCount = 0;
        int nonSpamCount = 0;

        // Accumulate feature counts
        for (Email email : emails) {
            Map<String, Integer> features = email.getFeatures();
            if (email.isSpam()) {
                spamCount++;
                features.forEach((key, value) -> totalSpamFeatures.merge(key, (double) value, Double::sum));
            } else {
                nonSpamCount++;
                features.forEach((key, value) -> totalNonSpamFeatures.merge(key, (double) value, Double::sum));
            }
        }

        // Create effectively final variables for the lambda expression
        final double finalSpamCount = (double) spamCount;
        final double finalNonSpamCount = (double) nonSpamCount;

        // Calculate average features
        totalSpamFeatures.forEach((key, value) -> averageSpamFeatures.put(key, value / finalSpamCount));
        totalNonSpamFeatures.forEach((key, value) -> averageNonSpamFeatures.put(key, value / finalNonSpamCount));
    }

    public boolean classify(Email email) {
        double distanceToSpam = euclideanDistance(email.getFeatures(), averageSpamFeatures);
        double distanceToNonSpam = euclideanDistance(email.getFeatures(), averageNonSpamFeatures);

        // Classify as spam if the distance to the average spam features is smaller
        return distanceToSpam < distanceToNonSpam;
    }

    private double euclideanDistance(Map<String, Integer> emailFeatures, Map<String, Double> averageFeatures) {
        double sum = 0.0;
        for (String key : averageFeatures.keySet()) {
            double emailFeatureValue = emailFeatures.getOrDefault(key, 0);
            double averageFeatureValue = averageFeatures.getOrDefault(key, 0.0);
            sum += Math.pow(emailFeatureValue - averageFeatureValue, 2);
        }
        return Math.sqrt(sum);
    }
}
