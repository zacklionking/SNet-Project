package jcoolj.com.dribbble.oauth;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.api.client.util.Base64;
import com.google.api.client.util.IOUtils;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.DataStoreUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jcoolj.com.core.utils.Logger;

/**
 * 将加密的Credential储存到SharedPreferences的工厂类
 */
public class SharedPreferencesDataStoreFactory implements DataStoreFactory {

    /** Lock on access to the data store map. */
    private final Lock lock = new ReentrantLock();

    /** Map of data store ID to data store. */
    private final Map<String, DataStore<? extends Serializable>> dataStoreMap = Maps.newHashMap();

    private Context context;

    public SharedPreferencesDataStoreFactory(Context context){
        this.context = context.getApplicationContext();
    }

    @Override
    public <V extends Serializable> DataStore<V> getDataStore(String id) throws IOException {
        lock.lock();
        try {
            @SuppressWarnings("unchecked")
            DataStore<V> dataStore = (DataStore<V>) dataStoreMap.get(id);
            if (dataStore == null) {
                dataStore = createDataStore(id);
                dataStoreMap.put(id, dataStore);
            }
            return dataStore;
        } finally {
            lock.unlock();
        }
    }

    protected <V extends Serializable> DataStore<V> createDataStore(String id) throws IOException {
        return new SharedPreferencesDataStore<V>(context, this, id);
    }

    private class SharedPreferencesDataStore<V extends Serializable> extends AbstractDataStore<V> {

        private static final String KEY_STORE = "user.store";

        /** Lock on access to the store. */
        private final Lock lock = new ReentrantLock();

        /** Data store map from the key to the value. */
        HashMap<String, byte[]> keyValueMap = Maps.newHashMap();

        private final SharedPreferences prefs;

        /**
         * @param dataStoreFactory data store factory
         * @param id                 data store ID
         */
        protected SharedPreferencesDataStore(Context context, DataStoreFactory dataStoreFactory, String id) {
            super(dataStoreFactory, id);
            this.prefs = Preconditions.checkNotNull(context.getApplicationContext().getSharedPreferences(id, Context.MODE_PRIVATE));
            if (prefs.contains(KEY_STORE)) {
                // load credentials from existing file
                try {
                    keyValueMap = IOUtils.deserialize(Base64.decodeBase64(prefs.getString(KEY_STORE, "")));
                    Logger.d("TokenStore " + id + " exist, load from SharedPreferences");
                } catch (IOException ignored) { }
            }
        }

        @Override
        public Set<String> keySet() throws IOException {
            lock.lock();
            try {
                return Collections.unmodifiableSet(keyValueMap.keySet());
            } finally {
                lock.unlock();
            }
        }

        @Override
        public Collection<V> values() throws IOException {
            lock.lock();
            try {
                List<V> result = Lists.newArrayList();
                for (byte[] bytes : keyValueMap.values()) {
                    result.add(IOUtils.<V>deserialize(bytes));
                }
                return Collections.unmodifiableList(result);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public V get(String key) throws IOException {
            if (key == null) {
                return null;
            }
            lock.lock();
            try {
                return IOUtils.deserialize(keyValueMap.get(key));
            } finally {
                lock.unlock();
            }
        }

        @Override
        public DataStore<V> set(String key, V value) throws IOException {
            Preconditions.checkNotNull(key);
            Preconditions.checkNotNull(value);
            lock.lock();
            try {
                keyValueMap.put(key, IOUtils.serialize(value));
                save();
            } finally {
                lock.unlock();
            }
            return this;
        }

        @Override
        public DataStore<V> clear() throws IOException {
            lock.lock();
            try {
                keyValueMap.clear();
                prefs.edit().clear().apply();
            } finally {
                lock.unlock();
            }
            return this;
        }

        @Override
        public DataStore<V> delete(String key) throws IOException {
            if (key == null) {
                return this;
            }
            lock.lock();
            try {
                keyValueMap.remove(key);
                save();
            } finally {
                lock.unlock();
            }
            return this;
        }

        @Override
        public boolean containsKey(String key) throws IOException {
            if (key == null) {
                return false;
            }
            lock.lock();
            try {
                return keyValueMap.containsKey(key);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public boolean isEmpty() throws IOException {
            lock.lock();
            try {
                return keyValueMap.isEmpty();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int size() throws IOException {
            lock.lock();
            try {
                return keyValueMap.size();
            } finally {
                lock.unlock();
            }
        }

        /**
         * Persist the key-value map into storage at the end of {@link #set}, {@link #delete(String)}, and
         * {@link #clear()}.
         */
        private void save() throws IOException {
            prefs.edit().putString(KEY_STORE, new String(Base64.encodeBase64(IOUtils.serialize(keyValueMap)))).apply();
        }

        @Override
        public String toString() {
            return DataStoreUtils.toString(this);
        }

    }

}
