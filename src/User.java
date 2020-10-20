import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

// doing this for pull request
public class User {

    private String username;
    private String theme;
    private JSONArray jsonArray = new JSONArray();
    private boolean firstUser;

    public User() {
        this.username = "";
        this.theme = "";
    }

    public User(String username, String theme) {
        this.username = username;
        setThemeValue(theme);
    }

    public void writeUser() {
        try {
            FileWriter file = new FileWriter("users.txt");
            JSONObject users = new JSONObject();

            JSONObject newUser = checkIfNewUser();

            if (this.firstUser){
                JSONObject user = new JSONObject();
                user.put("username", this.username);
                user.put("theme", this.theme);
                jsonArray.add(user);
            }

            if (!(newUser.isEmpty())){
                jsonArray.add(newUser);
            }
            users.put("users", jsonArray);

            file = new FileWriter("users.txt");
            file.write(users.toJSONString());

            file.flush();
            file.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject checkIfNewUser(){
        JSONParser parser = new JSONParser();
        String name;
        JSONObject newUser = new JSONObject();

        try(Reader reader = new FileReader("users.txt")) {
            JSONObject users = (JSONObject) parser.parse(reader);
            JSONArray array = (JSONArray) users.get("users");

            for (Object o : array) {
                JSONObject user = (JSONObject) o;
                name = String.valueOf(user.get("username"));

                if (name.equals(this.username)) {
                    if (checkIfNewTheme()){
                        // if not new user but has a new theme
                        array.remove(user);
                        newUser.put("username", name);
                        newUser.put("theme", user.get("theme"));
                    }

                    break;

                } else {
                    newUser.put("username", name);
                    newUser.put("theme", user.get("theme"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return newUser;
    }

    private boolean checkIfNewTheme(){
        JSONParser parser = new JSONParser();
        String theme;
        boolean newTheme = false;

        try(Reader reader = new FileReader("users.txt")) {
            JSONObject users = (JSONObject) parser.parse(reader);
            JSONArray array = (JSONArray) users.get("users");

            for (Object o : array) {
                JSONObject user = (JSONObject) o;
                theme = String.valueOf(user.get("theme"));

                if (theme.equals(this.theme)) {
                    newTheme = false;
                    break;
                } else {
                    newTheme = true;
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return newTheme;
    }

    private void setThemeValue(String theme){
        switch (theme) {
            case "0":
                this.theme = "BlueStyle";
                break;
            case "1":
                this.theme = "PinkStyle";
                break;
            default:
                this.theme = "DefaultStyle";
        }
    }

    public void deleteUser() throws IOException {
        FileWriter file = new FileWriter("users.txt");
        file.write("");
        file.close();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getUsername() {
        return username;
    }

    public String getTheme() {
        return theme;
    }
}
