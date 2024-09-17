import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "chatController")
@ViewScoped
public class ChatController implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> users;
    private String selectedUser;
    private String newMessage;
    private Map<String, List<String>> messages;

    @PostConstruct
    public void init() {
        // Initialize users
        users = new ArrayList<>();
        users.add("A");
        users.add("B");
        users.add("C");

        // Initialize messages map
        messages = new HashMap<>();
        for (String user : users) {
            messages.put(user, new ArrayList<>());
            // Add some initial messages for demonstration
            messages.get(user).add("Hello!");
            messages.get(user).add("Hi " + user + "!");
        }

        // Check if there's a user specified in the URL and set it as the selected user
        FacesContext context = FacesContext.getCurrentInstance();
        String userName = context.getExternalContext().getRequestParameterMap().get("userName");
        if (userName != null && !userName.trim().isEmpty() && users.contains(userName)) {
            setSelectedUser(userName);
        } else {
            // Optionally set a default user if no valid user is provided in the URL
            selectedUser = users.isEmpty() ? null : users.get(0);
        }
    }

    public void redirectToChat(String userName) {
        FacesContext context = FacesContext.getCurrentInstance();
        String redirectUrl = "chat?faces-redirect=true&userName=" + userName;
        try {
            context.getExternalContext().redirect(context.getExternalContext().getRequestContextPath() + "/" + redirectUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        if (selectedUser != null && newMessage != null && !newMessage.trim().isEmpty()) {
            messages.get(selectedUser).add("You: " + newMessage);
            newMessage = ""; // Clear the input after sending
        }
    }

    public List<String> getSelectedUserMessages() {
        return selectedUser != null ? messages.get(selectedUser) : new ArrayList<>();
    }

    // Getters and setters
    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(String selectedUser) {
        this.selectedUser = selectedUser;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }
}
