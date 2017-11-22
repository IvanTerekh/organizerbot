package com.intetics.organizerbot.context;

import java.util.HashMap;
import java.util.Map;

public class ContextHolder {
    private static final String LOGTAG = "CONTEXT_HOLDER";

    private static volatile ContextHolder instance;
    private volatile Map<Long, Context> contexts;

    private ContextHolder() {
        contexts = new HashMap<Long, Context>();
    }

    public static ContextHolder getInstance() {
        final ContextHolder currentInstance;
        if (instance == null) {
            synchronized (ContextHolder.class) {
                if (instance == null) {
                    instance = new ContextHolder();
                }
                currentInstance = instance;
            }
        } else {
            currentInstance = instance;
        }
        return currentInstance;
    }

    public void setContext(Long id, Context context) {
        contexts.put(id, context);
    }

    public Context getContext(Long id){
        return contexts.get(id);
    }

    public boolean contains(Long id){
        return contexts.containsKey(id);
    }
}
