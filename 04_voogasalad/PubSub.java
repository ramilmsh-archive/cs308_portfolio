package util.pubsub;

import util.pubsub.messages.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Class that implements publish/subscribe pattern
 *
 * @author ramilmsh
 */
public class PubSub {
    private static PubSub instance;

    private HashMap<String, ArrayList<Consumer<Message>>> callbacks;
    private HashMap<String, Function<Message, Object>> callbacksSync;

    private HashMap<String, Class> channels;

    /**
     * Create a new instance of PubSub
     */
    public PubSub(Properties config) {
        channels = new HashMap<>();
        for (String name : config.stringPropertyNames()) {
            try {
                channels.put(name.toLowerCase(), Class.forName(config.getProperty(name)));
            } catch (ClassNotFoundException ignored) {}
        }
        callbacks = new HashMap<>();
        callbacksSync = new HashMap<>();
    }

    /**
     * Subscribe to a synchronous pubsub
     *
     * @param channel:  pubsub
     * @param callback: callback that processes message
     */
    public void subscribe(String channel, Consumer<Message> callback) {
        channel = channel.toLowerCase();
        if (!channels.containsKey(channel))
            return;

        if (!callbacks.containsKey(channel))
            callbacks.put(channel, new ArrayList<>());

        callbacks.get(channel).add(callback);
    }

    /**
     * Unsubscribe from an async channel
     *
     * @param channel: channel name
     * @param callback: callback to be removed
     */
    public void unsubscribe(String channel, Consumer<Message> callback) {
        channel = channel.toLowerCase();
        if (callbacks != null && callbacks.containsKey(channel))
            callbacks.get(channel).remove(callback);
    }

    /**
     * Unsubscribe from a sync channel
     *
     * @param channel: channel name
     */
    public void unsubscribeSync(String channel) {
        channel = channel.toLowerCase();
        if (callbacksSync != null && callbacksSync.containsKey(channel))
            callbacks.put(channel, null);
    }

    /**
     * Subscribe to a synchronous pubsub
     *
     * @param channel:  pubsub
     * @param callback: callback that processes message
     */
    public void subscribeSync(String channel, Function<Message, Object> callback) {
        channel = channel.toLowerCase();
        if (!channels.containsKey(channel))
            return;

        callbacksSync.put(channel, callback);
    }

    /**
     * Publish to an asynchronous pubsub
     *
     * @param channel: pubsub
     * @param msg:     message
     */
    public void publish(String channel, Message msg) {
        channel = channel.toLowerCase();
        if (!(channels.containsKey(channel) && channels.get(channel).equals(msg.getClass())
                && callbacks.containsKey(channel)))
            return;

        for (Consumer<Message> callback : callbacks.get(channel))
            if (callback != null)
                callback.accept(msg);
    }

    /**
     * Publish to a synchronous pubsub
     *
     * @param channel: pubsub
     * @param msg:     message
     * @return result of processing
     */
    public Object publishSync(String channel, Message msg) {
        channel = channel.toLowerCase();
        if (!(channels.containsKey(channel) && channels.get(channel).equals(msg.getClass())
                && callbacks.containsKey(channel)))
            return null;

        return callbacksSync.get(channel).apply(msg);
    }

    /**
     * Singleton pattern for pubsub
     *
     * @return singleton instance of PubSub
     */
    public static PubSub getInstance() {
        if (instance == null)
            try {
                Properties properties = new Properties();
                properties.load(PubSub.class.getResourceAsStream("global.properties"));
                instance = new PubSub(properties);
            } catch (IOException e) {
                return null;
            }

        return instance;
    }
}
