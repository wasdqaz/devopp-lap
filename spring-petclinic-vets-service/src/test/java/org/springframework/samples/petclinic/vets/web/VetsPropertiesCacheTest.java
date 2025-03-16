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
}
