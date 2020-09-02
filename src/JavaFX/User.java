package JavaFX;

import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

// doing this for pull request
public class User {
    private String username;
    private String password;
    private String theme;

    public User() {
        this.username = "";
        this.password = "";
        this.theme = "";
    }
    public void writeUser() throws IOException {
        //todo use all the info from the interface to store in a JSON file: mallory
        FileWriter file = new FileWriter("user.txt");
        JSONObject user = new JSONObject();
        user.put("username", this.username);
        user.put("password", this.password); // get secured password
        try {
            file = new FileWriter("user.txt");
            file.write(user.toJSONString());

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

    public void deleteUser() throws IOException {
        FileWriter file = new FileWriter("user.txt");
        file.write("");
        file.close();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
