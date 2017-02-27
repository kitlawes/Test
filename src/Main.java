import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        printRepositories(1, 100);
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
}
