package cl.sebastialonso.jaidef;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by seba on 11/28/15.
 */
public class DefaultHashMap<K,V> extends HashMap<K,V> {
    protected V defaultValue;
    public DefaultHashMap(V defaultValue){
        this.defaultValue = defaultValue;
    }

    @Override
    public V get(Object k){
        V v = super.get(k);
        //Hashmap can contain null as a key, so better check that
        return ((v == null) && !this.containsKey(k)) ? this.defaultValue: v;
    }
}
