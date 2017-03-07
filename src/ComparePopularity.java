import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ComparePopularity {

    public static void main(String[] args) {
        ArrayList<String> githubTopics = getTopicsOrTagsFromFile("src/github/github_popularity.txt");
        ArrayList<String> stackOverflowTags = getTopicsOrTagsFromFile("src/stack/overflow/stack_overflow_popularity.txt");
        printPopularityComparison(githubTopics, stackOverflowTags);
    }

    static ArrayList<String> getTopicsOrTagsFromFile(String file) {
        ArrayList<String> topicsOrTags = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String topicOrTag = line.split("\\s+")[0];
                topicsOrTags.add(topicOrTag);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return topicsOrTags;
    }

    static void printPopularityComparison(ArrayList<String> githubTopics, ArrayList<String> stackOverflowTags) {
        for (String topic : githubTopics) {
            String space = "";
            for (int i = 0; i < 50 - topic.length(); i++) {
                space += " ";
            }
            System.out.print(topic + space);
            if (stackOverflowTags.contains(topic)) {
                String difference = String.valueOf(Math.abs(githubTopics.indexOf(topic) - stackOverflowTags.indexOf(topic)));
                System.out.print(difference);
            }
            System.out.println();
        }
    }
}
