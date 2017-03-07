package stack.overflow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class StackOverflowData {

    public static void main(String[] args) {
        printTagPopularity();
    }

    static void printTagPopularity() {
        for (int page = 1; page <= 50; page++) {
            try {
                URL repositoriesUrl = new URL("http://stackoverflow.com/tags?page=" + page + "&tab=popular");
                BufferedReader reader = new BufferedReader(new InputStreamReader(repositoriesUrl.openStream(), "UTF-8"));
                printTagPopularityForPage(reader);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void printTagPopularityForPage(BufferedReader reader) {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("rel=\"tag\">") && line.contains("class=\"item-multiplier-count\">")) {

                    int beginIndex = line.indexOf("rel=\"tag\">") + "rel=\"tag\">".length();
                    int endIndex = beginIndex + line.substring(beginIndex).indexOf("</a>");
                    String tag = line.substring(beginIndex, endIndex);
                    if (tag.contains("class=\"sponsor-tag-img\">")) {
                        beginIndex = tag.indexOf("class=\"sponsor-tag-img\">") + "class=\"sponsor-tag-img\">".length();
                        tag = tag.substring(beginIndex);
                    }

                    beginIndex = line.indexOf("class=\"item-multiplier-count\">") + "class=\"item-multiplier-count\">".length();
                    endIndex = beginIndex + line.substring(beginIndex).indexOf("</span>");
                    String popularity = line.substring(beginIndex, endIndex);

                    String space = "";
                    for (int i = 0; i < 50 - tag.length(); i++) {
                        space += " ";
                    }

                    System.out.println(tag + space + popularity);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
