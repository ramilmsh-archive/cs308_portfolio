package engine.entities;

import database.fileloaders.ScriptLoader;
import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Executes scripts and adds listeners associated with a given entity
 */
class EntityScriptFactory {

    /**
     * Execute all the scripts and add all listeners from properties of the entity
     *
     * @param entity: entity
     */
    static void executeScripts(Entity entity) {
        Map<String, Object> properties = entity.getProperties();
        parseScripts(entity, properties);
        parseListeners(entity, properties);
    }


    private static void parseScripts(Entity entity, Map<String, Object> properties) {
        Map scripts = (Map) properties.getOrDefault("scripts", new HashMap<String, ArrayList<Map>>());
        for (Object o : scripts.entrySet()) {
            Map<String, Object> bindings = new HashMap<>();
            parse(entity, bindings, (Map.Entry) o).call(entity, bindings);
        }
    }

    private static void parseListeners(Entity entity, Map<String, Object> properties) {
        Map listeners = (Map) properties.getOrDefault("listeners", new HashMap<String, ArrayList<Map>>());
        for (Object o : listeners.entrySet()) {
            String type = (String) ((Map.Entry) o).getKey();
            Map callbacks = (Map) listeners.getOrDefault(type, new HashMap<>());
            for (Object oo : callbacks.entrySet()) {
                Map<String, Object> bindings = new HashMap<>();
                Closure callback = parse(entity, bindings, (Map.Entry) oo);
                entity.on(type, (event) -> callback.call(entity, bindings, event));
            }
        }
    }

    private static Closure parse(Entity entity, Map<String, Object> bindings, Map.Entry entry) {
        String name = (String) entry.getKey();
        Map params = (Map) entry.getValue();
        addBindings(entity, params, bindings);
        return ScriptLoader.getScript(name);
    }

    private static void addBindings(Entity entity, Map params, Map<String, Object> bindings) {
        if (params != null)
            for (Object e : params.entrySet())
                bindings.put((String) ((Map.Entry) e).getKey(), ((Map.Entry) e).getValue());
    }
}
