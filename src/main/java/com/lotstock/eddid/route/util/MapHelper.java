package com.lotstock.eddid.route.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Iterator;
import java.util.Map;

public class MapHelper {

    public static <K,V> MultiValueMap<K,V> convertMapToMultiValueMap(Map<K,V> map){
        MultiValueMap multiValueMap = new LinkedMultiValueMap();
        if(!map.isEmpty()) {
            Iterator<K> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                K key = iterator.next();
                multiValueMap.set(key,map.get(key));
            }
        }
        return multiValueMap;
    }
}
