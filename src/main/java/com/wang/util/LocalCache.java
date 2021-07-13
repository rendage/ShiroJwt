package com.wang.util;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.wang.util.common.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author renxz
 * @desrciprtion 基于caffeine实现本地缓存
 * @date 2021/6/28 18:04
 * @see Caffeine 缓存
 * @see LocalCacheType
 * @see LocalCacheType
 */
public class LocalCache {

    private static final Logger log = LoggerFactory.getLogger(LocalCache.class);
    // 同步缓存
    private static LoadingCache<String, Object> synCache;
    // 异步缓存
    private static AsyncLoadingCache<String, Object> asynCache;
    // 手动加载

    private static int maximumSize = 10_00;
    private static int expireMinutesTime = 10; // MINUTES
    private static int initialCapacity = 1000;

    private static LocalCacheType DEFAULT_CACHE_TYPE = LocalCacheType.ASYN;

    private LocalCache() {

    }

    public static LocalCache initAsynCache() {
        if (null == asynCache) {
             asynCache = Caffeine.newBuilder().expireAfterWrite(expireMinutesTime, TimeUnit.MINUTES)
                    .initialCapacity(initialCapacity)
                    .maximumSize(maximumSize)
                    .buildAsync(key -> createExpensiveGraph(key));
        }
        return new LocalCache();
    }
    public static LocalCache initSynCache() {
        if (null == synCache) {
           synCache = Caffeine.newBuilder().expireAfterWrite(expireMinutesTime, TimeUnit.MINUTES)
                    .initialCapacity(initialCapacity)
                    .maximumSize(maximumSize)
                    .build(key -> createExpensiveGraph(key));
        }
        return new LocalCache();
    }



    // init cache
    public static LocalCache init(LocalCacheType localCacheType) {
            if (null == localCacheType) {
                localCacheType = DEFAULT_CACHE_TYPE;
            }
            switch (localCacheType) {
                case ASYN :
                    if (asynCache == null) {
                          asynCache = Caffeine.newBuilder().expireAfterWrite(expireMinutesTime, TimeUnit.MINUTES)
                                .initialCapacity(initialCapacity)
                                .maximumSize(maximumSize)
                                .buildAsync(key -> createExpensiveGraph(key));
                    }
                    break;
                case SYN:
                    if (synCache==null){
                       synCache = Caffeine.newBuilder().expireAfterWrite(expireMinutesTime, TimeUnit.MINUTES)
                                .initialCapacity(initialCapacity)
                                .maximumSize(maximumSize)
                                .build(key -> createExpensiveGraph(key));

                    }
                    break;

            }

        return new LocalCache();
    }

    // put cache
    public void put(String key, String value) {
        if (!StringUtil.isBlank(key)) {
            synCache.put(key, value);
            log.info("local cache put => success :key{},value{}", key, value);
        }
    }

    // get cache
    public  <V> V get(String key) {
        if (!StringUtil.isBlank(key)) {
            log.info("local cache get => failed :key is not null");
            return null;
        } else {
            return (V) synCache.get(key);
        }
    }

    // del cache with key
    public  void delOne(String key) {
        synCache.invalidate(key);
        log.info("local cache delOne => sucess :key{}", key);
    }

    // del all
    public  void delAll() {
        synCache.invalidateAll();
        log.info("local cache delAll => sucess");
    }

    // get cache by keys
    public  <K, V, T> Map<K, V> getAll(List<T> keys) {
        if (null == keys || keys.size() < 0) {
            log.warn("local cache getAll => failed:keys is not null");
        }
        return (Map<K, V>) synCache.getAll((Iterable<? extends String>) keys);
    }

    // refresh
    public  void refresh(String key){
        synCache.refresh(key);
    }

    private static Object createExpensiveGraph(String key) {
        return null;
    }
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int maximumSize = 10000;
        private int expireMinutesTime = 10;
        private int initialCapacity = 1000;
        private LocalCacheType cacheType;

        public LocalCacheType getLocalCacheType() {
            return cacheType;
        }

        public Builder setLocalCacheType(LocalCacheType localCacheType) {
            cacheType = localCacheType;
            return this;
        }

        public int getMaximumSize() {
            return maximumSize;
        }

        public Builder setMaximumSize(int maximumSize) {
            this.maximumSize = maximumSize;
            return this;
        }

        public int getMinutesTime() {
            return expireMinutesTime;
        }

        public Builder setMinutesTime(int minutesTime) {
            this.expireMinutesTime = minutesTime;
            return this;
        }

        public int getInitialCapacity() {
            return initialCapacity;
        }

        public Builder setInitialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return this;
        }
    }

    public void build() {
        Cache<Object, Object> cache = Caffeine.newBuilder().build();

    }
}

