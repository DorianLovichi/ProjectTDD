package com.TDDexam.DorianLovichi.repository;
import com.TDDexam.DorianLovichi.model.Car;
import com.TDDexam.DorianLovichi.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CarRepositoryTest {
    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepository();
    }

    @Test
    void testAddAndFindCar() {
        Car car = new Car("ABC123", "Tesla", true);
        carRepository.addCar(car);

        Optional<Car> result = carRepository.findByRegistrationNumber("ABC123");
        assertTrue(result.isPresent());
        assertEquals("Tesla", result.get().getModel());
    }

    @Test
    void testUpdateCar() {
        Car car = new Car("XYZ789", "Toyota", true);
        carRepository.addCar(car);

        car.setAvailable(false);
        carRepository.updateCar(car);

        Optional<Car> updated = carRepository.findByRegistrationNumber("XYZ789");
        assertTrue(updated.isPresent());
        assertFalse(updated.get().isAvailable());
    }
}
