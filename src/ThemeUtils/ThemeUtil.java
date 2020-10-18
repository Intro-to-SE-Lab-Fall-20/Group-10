package ThemeUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class ThemeUtil {

    public ThemeUtil() {
    }

    public static String getUserTheme(String username) {
        String theme = "defaultStyle";
        JSONParser parser = new JSONParser();
        String name;

        try (Reader reader = new FileReader("users.txt")) {
            JSONObject users = (JSONObject) parser.parse(reader);
            JSONArray array = (JSONArray) users.get("users");
            for (Object o : array) {
                JSONObject user = (JSONObject) o;
                name = String.valueOf(user.get("username"));
                if (name.equals(username)) {
                    theme = getThemeFileString(user.get("theme").toString());
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        return theme;
    }

    private static String getThemeFileString(String theme) {
        switch (theme) {
            case "pinkStyle":
                return "pinkStyle.css";
            case "blueStyle":
                return "blueStyle.css";
            default:
                    return "DefaultStyle.css";
        }
    }

}
