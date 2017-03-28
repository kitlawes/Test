package github;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class GithubData {

    public static void main(String[] args) {

        // Methods for finding the popularity of topics
//        printRepositories(1, 100);
//        getTopicsOfRepositoriesMostRecentlyUpdateWith1000OrMoreStars();
//        orderTopicsByPopularity();

        // Method for finding repositories with specific characteristics
        printRepositoriesWithMinIssuesMaxCommitsMaxLines(1, 200, 10000);

    }

    /*
     * Prints the list of GitHub repositories with id between start and end (inclusive)
     * TODO: There is an API call for each repository and this is really slow.
     */
    static void printRepositories(int startingRepositoryId, int endingRepositoryId) {
        for (int repositoryId = startingRepositoryId; repositoryId <= endingRepositoryId; repositoryId++) {
            JSONObject repositoryData = getRepositoryDataFromGithubApi(repositoryId);
            printRepositoryData(repositoryData, startingRepositoryId);
        }
    }

    static JSONObject getRepositoryDataFromGithubApi(int repositoryId) {
        try {
            URL repositoriesUrl = new URL("https://api.github.com/repositories/" + repositoryId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(repositoriesUrl.openStream(), "UTF-8"));
            String line = reader.readLine();
            line = "{\"repository\":" + line + "}";
            JSONObject object1 = new JSONObject(line);
            return object1.getJSONObject("repository");
        } catch (MalformedURLException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }
        return null;
    }

    static void printRepositoryData(JSONObject repositoryData, int startRepositoryId) {
        if (repositoryData != null) {
            if (repositoryData.getInt("id") > startRepositoryId) {
                System.out.println();
            }
            String name = null;
            if (!repositoryData.isNull("name")) {
                name = repositoryData.getString("name");
            }
            System.out.println(name);
            String description = null;
            if (!repositoryData.isNull("description")) {
                description = repositoryData.getString("description");
            }
            System.out.println(description);
            String url = null;
            if (!repositoryData.isNull("url")) {
                url = repositoryData.getString("url");
            }
            System.out.println(url);
        }
    }

    static void getTopicsOfRepositoriesMostRecentlyUpdateWith1000OrMoreStars() {

        Set<String> topics = new HashSet<String>();
        ArrayList<Integer> pages = new ArrayList<Integer>();
        for (int i = 1; i <= 100; i++) {
            pages.add(i);
        }

        while (!pages.isEmpty()) {
            addTopicsFromNextPage(topics, pages);
        }

        for (String topic : topics) {
            System.out.println(topic);
        }

    }

    static void addTopicsFromNextPage(Set<String> topics, ArrayList<Integer> pages) {
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

    static void orderTopicsByPopularity() {

        List<String> topics = getTopicsFromFile();

        HashMap<String, Integer> popularity = new HashMap<String, Integer>();
        while (!topics.isEmpty()) {
            addPopularityOfNextTopic(popularity, topics);
        }
        System.out.println();

        List<Map.Entry<String, Integer>> orderedPopularity = getOrderedPopularity(popularity);

        printOrderedPopularity(orderedPopularity);

    }

    static List<String> getTopicsFromFile() {
        List<String> topics = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/github_topics.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                topics.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return topics;
    }

    static void addPopularityOfNextTopic(HashMap<String, Integer> popularity, List<String> topics) {
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

    private static List<Map.Entry<String, Integer>> getOrderedPopularity(Map<String, Integer> unsortedMap) {
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortedMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });
        return list;
    }

    static void printOrderedPopularity(List<Map.Entry<String, Integer>> orderedPopularity) {
        for (Map.Entry entry : orderedPopularity) {
            String topic = (String) entry.getKey();
            int topicPopularity = (int) entry.getValue();
            String space = "";
            for (int i = 0; i < 50 - topic.length(); i++) {
                space += " ";
            }
            System.out.println(topic + space + topicPopularity);
        }
    }

    static void sleepForTenSeconds() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    static void printRepositoriesWithMinIssuesMaxCommitsMaxLines(int minIssues, int maxCommits, int maxLines) {
        for (int page = 1; page <= 100; page++) {
            List<String> repositoryUrls = getRepositoryUrlsForPage(page);
            for (String repositoryUrl : repositoryUrls) {
                int issues = getIssuesForRepository(repositoryUrl);
                System.out.println("repository: " + repositoryUrl);
                System.out.println("issues: " + issues);
                if (issues >= minIssues) {
                    List<String> commits = getCommitsForRepository(repositoryUrl, maxCommits);
                    if (commits.size() <= maxCommits) {
                        System.out.println("commits: " + commits.size());
                        int lines = getLinesForRepository(repositoryUrl, commits, maxLines);
                        if (lines <= maxLines) {
//                            System.out.println("repository: " + repositoryUrl);
//                            System.out.println("issues: " + issues);
//                            System.out.println("commits: " + commits.size());
                            System.out.println("lines: " + lines);
//                            System.out.println();
                        } else {
                            System.out.println("lines: " + lines + "+");
                        }
                    } else {
                        System.out.println("commits: " + commits.size() + "+");
                    }
                }
                System.out.println();
            }
        }
    }

    static List<String> getRepositoryUrlsForPage(int page) {
        List<String> repositoryUrls = new ArrayList<String>();
        try {
            URL url = new URL("https://github.com/search?o=desc&p=" + page + "&q=pushed%3A%3E2017-01-01&s=updated&type=Repositories&utf8=%E2%9C%93");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("class=\"v-align-middle\">")) {
                    int beginIndex = line.indexOf("class=\"v-align-middle\">") + "class=\"v-align-middle\">".length();
                    int endIndex = line.indexOf("</a>");
                    String substring = line.substring(beginIndex, endIndex);
                    repositoryUrls.add("https://github.com/" + substring);
                }
            }
        } catch (MalformedURLException e) {
            sleepForTenSeconds();
        } catch (UnsupportedEncodingException e) {
            sleepForTenSeconds();
        } catch (IOException e) {
            sleepForTenSeconds();
        }
        return repositoryUrls;
    }

    static int getIssuesForRepository(String repositoryUrl) {
        try {
            URL url = new URL(repositoryUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<span class=\"counter\">")) {
                    int beginIndex = line.indexOf("<span class=\"counter\">") + "<span class=\"counter\">".length();
                    int endIndex = line.indexOf("</span>");
                    String substring = line.substring(beginIndex, endIndex);
                    int issues = Integer.parseInt(substring);
                    return issues;
                }
            }
        } catch (MalformedURLException e) {
            sleepForTenSeconds();
        } catch (UnsupportedEncodingException e) {
            sleepForTenSeconds();
        } catch (IOException e) {
            sleepForTenSeconds();
        }
        return 0;
    }

    static List<String> getCommitsForRepository(String repositoryUrl, int maxCommits) {
        List<String> commits = getCommitsForUrl(repositoryUrl + "/commits/master", repositoryUrl);
        int commitsSize = 0;
        while (commits.size() > commitsSize) {
            commitsSize = commits.size();
            System.out.println("commits so far: " + commitsSize);
            if (commitsSize > maxCommits) {
                return commits;
            }
            commits.addAll(getCommitsForUrl(repositoryUrl + "/commits/master?after=" + commits.get(0) + "+" + (commits.size() - 1), repositoryUrl));
        }
        return commits;
    }

    static List<String> getCommitsForUrl(String commitsUrl, String repositoryUrl) {
        List<String> commits = new ArrayList<String>();
        try {
            URL url = new URL(commitsUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(repositoryUrl.substring("https://github.com".length()) + "/commit/") && line.contains("\" class=\"sha btn btn-outline BtnGroup-item\">")) {
                    int beginIndex = line.indexOf(repositoryUrl.substring("https://github.com".length()) + "/commit/") + (repositoryUrl.substring("https://github.com".length()) + "/commit/").length();
                    int endIndex = line.indexOf("\" class=\"sha btn btn-outline BtnGroup-item\">");
                    String substring = line.substring(beginIndex, endIndex);
                    commits.add(substring);
                }
            }
        } catch (MalformedURLException e) {
            sleepForTenSeconds();
        } catch (UnsupportedEncodingException e) {
            sleepForTenSeconds();
        } catch (IOException e) {
            sleepForTenSeconds();
        }
        return commits;
    }

    static int getLinesForRepository(String repositoryUrl, List<String> commits, int maxLines) {
        int linesOfCode = 0;
        for (String commit : commits) {
            linesOfCode += getLinesForCommit(repositoryUrl + "/commit/" + commit);
            System.out.println("lines so far: " + linesOfCode);
            if (linesOfCode > maxLines) {
                return linesOfCode;
            }
        }
        return linesOfCode;
    }

    static int getLinesForCommit(String commitUrl) {
        try {
            URL url = new URL(commitUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            int linesForCommit = 0;
            while ((line = reader.readLine()) != null) {
                linesForCommit += getLinesForLine(line, "addition");
                linesForCommit += getLinesForLine(line, "additions");
                linesForCommit -= getLinesForLine(line, "deletion");
                linesForCommit -= getLinesForLine(line, "deletions");
                if (line.contains(" deletion</strong>") || line.contains(" deletions</strong>")) {
                    return linesForCommit;
                }
            }
        } catch (MalformedURLException e) {
            sleepForTenSeconds();
        } catch (UnsupportedEncodingException e) {
            sleepForTenSeconds();
        } catch (IOException e) {
            sleepForTenSeconds();
        }
        return 0;
    }

    static int getLinesForLine(String line, String qualifier) {
        if (line.contains(" " + qualifier + "</strong>")) {
            int beginIndex = line.indexOf("<strong>") + "<strong>".length();
            int endIndex = line.indexOf(" " + qualifier + "</strong>");
            String substring = line.substring(beginIndex, endIndex);
            return Integer.parseInt(substring.replace(",", ""));
        }
        return 0;
    }

}