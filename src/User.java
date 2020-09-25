
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

// doing this for pull request
public class User {
    private String username;
    private String theme;
    private JSONArray jsonArray = new JSONArray();

    public User() {
        this.username = "";
        this.theme = "";
    }

    public User(String username, String theme) {
        this.username = username;
        setThemeValue(theme);
    }

    public void writeUser() throws IOException {
        //todo use all the info from the interface to store in a JSON file: mallory
        FileWriter file = new FileWriter("user.txt");
        JSONObject user = new JSONObject();
        if (checkIfNewUser() == true){
            user.put("username", this.username);
        }
        if(checkIfNewTheme() == true){
            user.put("theme", this.theme);
        }

        jsonArray.add(user);
        try {
            file = new FileWriter("users.txt");
            file.write(jsonArray.toJSONString());

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkIfNewUser(){
        JSONParser parser = new JSONParser();
        String name = "";
        try(Reader reader = new FileReader("users.text")) {
            JSONObject object = (JSONObject) parser.parse(reader);
            name = String.valueOf(object.get("username"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return !name.equals(this.username);
    }

    private boolean checkIfNewTheme(){
        JSONParser parser = new JSONParser();
        String theme1 = "";
        try(Reader reader = new FileReader("users.text")) {
            JSONObject object = (JSONObject) parser.parse(reader);
            theme1 = String.valueOf(object.get("theme"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return !theme1.equals(this.theme);
    }

    private void setThemeValue(String theme){
        switch (theme) {
            case "0":
                this.theme = "blueStyle";
                break;
            case "1":
                this.theme = "pinkStyle";
                break;
            default:
                this.theme = "defaultStyle";
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
