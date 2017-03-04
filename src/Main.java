import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Main {

    public static void main(String[] args) {
//        printRepositories(1, 100);
//        getTopicsOfRepositoriesMostRecentlyUpdateWith1000OrMoreStars();
        orderTopicsByPopularity();
    }

    /*
     * Prints the list of GitHub repositories with id between start and end (inclusive)
     * TODO: There is an API call for each repository and this is really slow.
     */
    static void printRepositories(int start, int end) {
        for (int i = start; i <= end; i++) {
            try {

                URL repositoriesUrl = new URL("https://api.github.com/repositories/" + i);
                BufferedReader reader = new BufferedReader(new InputStreamReader(repositoriesUrl.openStream(), "UTF-8"));
                String line = reader.readLine();
                line = "{\"repository\":" + line + "}";
                JSONObject object1 = new JSONObject(line);
                JSONObject object2 = object1.getJSONObject("repository");

                if (i > start) {
                    System.out.println();
                }
                String name = null;
                if (!object2.isNull("name")) {
                    name = object2.getString("name");
                }
                System.out.println(name);
                String description = null;
                if (!object2.isNull("description")) {
                    description = object2.getString("description");
                }
                System.out.println(description);
                String url = null;
                if (!object2.isNull("url")) {
                    url = object2.getString("url");
                }
                System.out.println(url);

            } catch (MalformedURLException e) {
            } catch (UnsupportedEncodingException e) {
            } catch (IOException e) {
            }
        }
    }

    static void getTopicsOfRepositoriesMostRecentlyUpdateWith1000OrMoreStars() {

        Set<String> topics = new HashSet<String>();
        ArrayList<Integer> pages = new ArrayList<Integer>();
        for (int i = 1; i <= 100; i++) {
            pages.add(i);
        }

        while (!pages.isEmpty()) {
            try {

                int page = pages.get(0);
                URL repositoriesUrl = new URL("https://github.com/search?o=desc&p=" + page + "&q=stars%3A%3E%3D1000&ref=searchresults&s=updated&type=Repositories&utf8=%E2%9C%93");
                BufferedReader reader = new BufferedReader(new InputStreamReader(repositoriesUrl.openStream(), "UTF-8"));
                System.out.println(page);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("/search?q=topic%3A") && line.contains("+org%3A")) {
                        int beginIndex = line.indexOf("/search?q=topic%3A") + "/search?q=topic%3A".length();
                        int endIndex = line.indexOf("+org%3A");
                        String substring = line.substring(beginIndex, endIndex);
                        topics.add(substring);
                        System.out.println(substring);
                    }
                }
                System.out.println();
                pages.remove(0);

            } catch (MalformedURLException e) {
                sleepForTenSeconds();
            } catch (UnsupportedEncodingException e) {
                sleepForTenSeconds();
            } catch (IOException e) {
                sleepForTenSeconds();
            }
        }

        for (String topic : topics) {
            System.out.println(topic);
        }

    }

    static void orderTopicsByPopularity() {

        List<String> topics = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/topics.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                topics.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, Integer> popularity = new HashMap<String, Integer>();
        while (!topics.isEmpty()) {
            try {
                String topic = topics.get(0);
                URL repositoriesUrl = new URL("https://github.com/search?utf8=%E2%9C%93&q=topic%3A" + topic + "&ref=simplesearch");
                BufferedReader reader = new BufferedReader(new InputStreamReader(repositoriesUrl.openStream(), "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("<span class=\"counter\">") && line.contains("</span>")) {

                        int beginIndex = line.indexOf("<span class=\"counter\">") + "<span class=\"counter\">".length();
                        int endIndex = line.indexOf("</span>");
                        String substring = line.substring(beginIndex, endIndex);
                        int topicPopularity = Integer.parseInt(substring.replace(",", ""));
                        popularity.put(topic, topicPopularity);

                        String space = "";
                        for (int i = 0; i < 50 - topic.length(); i++) {
                            space += " ";
                        }
                        System.out.println(topic + space + topicPopularity);

                        break;

                    }
                }
                topics.remove(0);
            } catch (MalformedURLException e) {
                sleepForTenSeconds();
            } catch (UnsupportedEncodingException e) {
                sleepForTenSeconds();
            } catch (IOException e) {
                sleepForTenSeconds();
            }
        }
        System.out.println();

        List<Map.Entry<String, Integer>> sortedPopularity = getSortedPopularity(popularity);
        for (Map.Entry entry : sortedPopularity) {
            String topic = (String) entry.getKey();
            int topicPopularity = (int) entry.getValue();
            String space = "";
            for (int i = 0; i < 50 - topic.length(); i++) {
                space += " ";
            }
            System.out.println(topic + space + topicPopularity);
        }

    }

    private static List<Map.Entry<String, Integer>> getSortedPopularity(Map<String, Integer> unsortedMap) {
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortedMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });
        return list;
    }

    static void sleepForTenSeconds() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

}
