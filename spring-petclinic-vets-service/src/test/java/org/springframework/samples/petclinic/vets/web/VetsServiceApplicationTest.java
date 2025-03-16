package org.springframework.samples.petclinic.vets.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VetsServiceApplication.class)  // Đảm bảo không khởi động server thực tế trong môi trường test
class VetsServiceApplicationTest {

    @Test
    void testMain() {
        // Arrange
        String[] args = {}; // Tham số truyền vào phương thức main

        // Act & Assert
        // Kiểm tra xem phương thức main có gây lỗi không
        assertDoesNotThrow(() -> VetsServiceApplication.main(args));
    }

    @Test
    void testVetsServiceApplicationConstructor() {
        // Act
        VetsServiceApplication app = new VetsServiceApplication();  // Tạo đối tượng của VetsServiceApplication

        // Assert
        assertNotNull(app);  // Kiểm tra đối tượng không null
    }
}
