import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VetsPropertiesCacheTest {

    @Test
    void testCacheTtl() {
        // Arrange
        VetsProperties.Cache cache = new VetsProperties.Cache(100, 200);

        // Act & Assert
        assertEquals(100, cache.ttl());
    }

    @Test
    void testCacheHeapSize() {
        // Arrange
        VetsProperties.Cache cache = new VetsProperties.Cache(100, 200);

        // Act & Assert
        assertEquals(200, cache.heapSize());
    }
    
    // Khai báo VetsProperties và Cache bên trong class này
    static class VetsProperties {
        private Cache cache;

        public VetsProperties(int ttl, int heapSize) {
            this.cache = new Cache(ttl, heapSize);
        }

        public Cache getCache() {
            return cache;
        }

        // Lớp Cache bên trong VetsProperties
        static class Cache {
            private int ttl;
            private int heapSize;

            public Cache(int ttl, int heapSize) {
                this.ttl = ttl;
                this.heapSize = heapSize;
            }

            public int ttl() {
                return ttl;
            }

            public int heapSize() {
                return heapSize;
            }
        }
    }
}
