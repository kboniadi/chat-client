package databus;

import data.IDataType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DataBus {
    private static final HashMap<String, Set<Member>> list = new HashMap<>();

    private DataBus() {
        // empty
    }

    private static class InstanceHolder {
        private static final DataBus INSTANCE = new DataBus();
    }

    public synchronized static DataBus getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public synchronized static void register(Member client, String... messagesType) {
        Objects.requireNonNull(client);
        Objects.requireNonNull(messagesType);
        for (var message : messagesType) {
            list.computeIfAbsent(message, k -> new HashSet<>()).add(client);
        }
    }

    public synchronized static void unregister(Member client, String... messagesType) {
        Objects.requireNonNull(client);
        Objects.requireNonNull(messagesType);
        for (var message : messagesType) {
            Set<?> reference = list.get(message);
            if (reference != null) {
                reference.remove(client);

                if (reference.isEmpty())
                    list.remove(message);
            }
        }
    }

    public synchronized static void publish(String type, IDataType message) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(message);
        Set<?> clients = list.get(type);
        if (clients != null)
            clients.forEach(k -> ((Member) k).send(message));
    }

    public synchronized static boolean hasListeners(String message) {
        Objects.requireNonNull(message);
        return list.get(message) != null;
    }

    public synchronized static void cleanup() {
        list.clear();
    }
}
