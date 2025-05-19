import java.util.Map;
import java.util.HashMap;

public class Email {
    private String rawText;
    private Map<String, Integer> features;
    private boolean isSpam;

    public Email(String rawText, boolean isSpam) {
        this.rawText = rawText;
        this.isSpam = isSpam;
        this.features = new HashMap<>();
    }

    // Getters and Setters
    public String getRawText() {
        return rawText;
    }

    public Map<String, Integer> getFeatures() {
        return features;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public void setFeatures(Map<String, Integer> features) {
        this.features = features;
    }
}
