package Refresher.Users;

import DTO.UserData;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class UsersAndVersion {
    final private List<UserData> entries;
    final private Map<String, Integer> userVersion;
    final private Set<String> deletedusers;

    public UsersAndVersion(List<UserData> entries, Map<String, Integer> userVersion, Set<String> deletedusers) {
        this.entries = entries;
        this.userVersion = userVersion;
        this.deletedusers = deletedusers;
    }

    public Set<String> getDeletedusers() {
        return deletedusers;
    }

    public List<UserData> getEntries() {
        return entries;
    }

    public Map<String, Integer> getUserVersion() {
        return userVersion;
    }
}
