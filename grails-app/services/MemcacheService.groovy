/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */


import javax.cache.Cache
import javax.cache.CacheException
import javax.cache.CacheFactory
import javax.cache.CacheManager

/**
 * @author Sven Haiges <hansamann@yahoo.de>
 */
class MemcacheService {

    boolean transactional = true
    Cache cache = null

    def containsKey(key)
    {
        return getCache().containsKey(key)
    }

    def get(key)
    {
        getCache().get(key)
    }

    def put(key, value)
    {
        getCache().put(key, value)
    }

    def getTimestamped(key, ageInMinutes)
    {
        def cache = getCache()
        def timestampKey = key + '_timestamp'
        if (cache.containsKey(timestampKey) && cache.containsKey(key))
        {
            def timestamp = cache.get(timestampKey).time
            def limit = new Date().time - (1000 * 60 * ageInMinutes)
            if (limit < timestamp)
                return cache.get(key)
        }

        return null
    }

    def putTimestamped(key, value)
    {
        def cache = getCache()
        cache.put(key + '_timestamp', new Date())
        cache.put(key, value)
    }

    def getCache()
    {
        if (cache)
            return cache

        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
        } catch (CacheException e) {
            // ...
        }

        return cache
    }
}
