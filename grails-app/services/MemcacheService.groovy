import javax.cache.Cache
import javax.cache.CacheException
import javax.cache.CacheFactory
import javax.cache.CacheManager

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
