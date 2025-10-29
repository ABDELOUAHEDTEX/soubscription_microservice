package com.transport.urbain.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis cache configuration for the Schedule Service.
 * <p>
 * This configuration class enables caching support using Redis as the cache provider.
 * It configures cache settings including TTL (Time To Live), serialization strategies,
 * and cache storage behavior.
 * <p>
 * Features:
 * <ul>
 *     <li>Cache entries expire after 1 hour</li>
 *     <li>Keys are serialized as strings</li>
 *     <li>Values are serialized as JSON</li>
 *     <li>Null values are not cached</li>
 * </ul>
 * <p>
 * This improves performance by reducing database load for frequently accessed data
 * like schedules, routes, stops, and buses.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configures the Redis cache manager.
     * <p>
     * Creates a cache manager with the following settings:
     * <ul>
     *     <li>Entry TTL: 1 hour (cache entries expire after 1 hour)</li>
     *     <li>Key serialization: String (e.g., "bus:123")</li>
     *     <li>Value serialization: JSON (serializes objects to JSON format)</li>
     *     <li>Null values: Caching of null values is disabled</li>
     * </ul>
     * <p>
     * This configuration applies to all caches unless overridden at the method level.
     *
     * @param connectionFactory Redis connection factory for connecting to Redis server
     * @return configured CacheManager using Redis as the cache provider
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configure default cache settings
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // Set cache entry time-to-live to 1 hour
                .entryTtl(Duration.ofHours(1))
                // Serialize cache keys as strings
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                // Serialize cache values as JSON
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                )
                // Don't cache null values
                .disableCachingNullValues();

        // Build and return the cache manager with Redis backend
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
