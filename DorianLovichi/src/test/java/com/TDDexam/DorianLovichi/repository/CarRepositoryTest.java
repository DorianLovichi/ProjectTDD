package com.TDDexam.DorianLovichi.repository;

import com.TDDexam.DorianLovichi.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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

    @Test
    void testGetAllCarsReturnsEmptyListWhenNoCarAdded() {
        List<Car> cars = carRepository.getAllCars();

        assertNotNull(cars);
        assertTrue(cars.isEmpty());
    }

    @Test
    void testGetAllCarsReturnsCars() {
        Car car1 = new Car("ABC123", "Tesla", true);
        Car car2 = new Car("XYZ789", "Toyota", false);

        carRepository.addCar(car1);
        carRepository.addCar(car2);

        List<Car> cars = carRepository.getAllCars();

        assertEquals(2, cars.size());
        assertTrue(cars.contains(car1));
        assertTrue(cars.contains(car2));
    }

    @Test
    void testFindByRegistrationNumberReturnsEmptyWhenCarNotFound() {
        Optional<Car> result = carRepository.findByRegistrationNumber("NOTFOUND");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByRegistrationNumberIsCaseSensitive() {
        Car car = new Car("ABC123", "Tesla", true);
        carRepository.addCar(car);

        Optional<Car> result1 = carRepository.findByRegistrationNumber("ABC123");
        Optional<Car> result2 = carRepository.findByRegistrationNumber("abc123");

        assertTrue(result1.isPresent());
        assertFalse(result2.isPresent());
    }

    @Test
    void testUpdateCarDoesNothingWhenCarNotFound() {
        Car car1 = new Car("ABC123", "Tesla", true);
        Car car2 = new Car("XYZ789", "Toyota", false);

        carRepository.addCar(car1);

        carRepository.updateCar(car2);
        Optional<Car> result = carRepository.findByRegistrationNumber("ABC123");
        assertTrue(result.isPresent());
        assertEquals("Tesla", result.get().getModel());
        assertTrue(result.get().isAvailable());

        Optional<Car> notAdded = carRepository.findByRegistrationNumber("XYZ789");
        assertFalse(notAdded.isPresent());
    }

    @Test
    void testUpdateCarChangesMultiplePropertiesAtOnce() {
        Car car = new Car("ABC123", "Tesla", true);
        carRepository.addCar(car);

        Car updatedCar = new Car("ABC123", "Updated Model", false);
        carRepository.updateCar(updatedCar);

        Optional<Car> result = carRepository.findByRegistrationNumber("ABC123");
        assertTrue(result.isPresent());
        assertEquals("Updated Model", result.get().getModel());
        assertFalse(result.get().isAvailable());
    }

    @Test
    void testAddMultipleCars() {
        Car car1 = new Car("ABC123", "Tesla", true);
        Car car2 = new Car("DEF456", "Toyota", true);
        Car car3 = new Car("GHI789", "Honda", false);

        carRepository.addCar(car1);
        carRepository.addCar(car2);
        carRepository.addCar(car3);

        List<Car> cars = carRepository.getAllCars();

        assertEquals(3, cars.size());
        assertTrue(cars.contains(car1));
        assertTrue(cars.contains(car2));
        assertTrue(cars.contains(car3));
    }

    @Test
    void testAddCarWithDuplicateRegistrationNumber() {
        Car car1 = new Car("ABC123", "Tesla", true);
        Car car2 = new Car("ABC123", "Toyota", false);

        carRepository.addCar(car1);
        carRepository.addCar(car2);

        List<Car> cars = carRepository.getAllCars();
        assertEquals(2, cars.size());

        Optional<Car> result = carRepository.findByRegistrationNumber("ABC123");
        assertTrue(result.isPresent());
        assertEquals("Tesla", result.get().getModel());
    }
}