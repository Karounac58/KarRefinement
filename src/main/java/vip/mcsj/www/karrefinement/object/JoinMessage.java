package vip.mcsj.www.karrefinement.object;

import java.util.List;

public class JoinMessage {
    private String permission;
    private List<String> message;

    public JoinMessage(String permission, List<String> message) {
        this.permission = permission;
        this.message = message;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }
}
