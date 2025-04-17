package com.TDDexam.DorianLovichi.service;

import com.TDDexam.DorianLovichi.model.Car;
import com.TDDexam.DorianLovichi.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CarRentalServiceTest {

    private CarRepository carRepository;
    private CarRentalService carRentalService;

    @BeforeEach
    void setUp() {
        carRepository = mock(CarRepository.class);
        carRentalService = new CarRentalService(carRepository);
    }

    @Test
    void testGetAllCars() {
        List<Car> expectedCars = Arrays.asList(
                new Car("123", "Toyota", true),
                new Car("456", "Honda", false)
        );
        when(carRepository.getAllCars()).thenReturn(expectedCars);

        List<Car> result = carRentalService.getAllCars();

        assertEquals(expectedCars, result);
        verify(carRepository).getAllCars();
    }

    @Test
    void testGetAllCarsReturnsEmptyList() {
        when(carRepository.getAllCars()).thenReturn(Collections.emptyList());

        List<Car> result = carRentalService.getAllCars();

        assertTrue(result.isEmpty());
        verify(carRepository).getAllCars();
    }

    @Test
    void testRentCarSuccess() {
        Car car = new Car("123", "Toyota", true);
        when(carRepository.findByRegistrationNumber("123")).thenReturn(Optional.of(car));

        boolean result = carRentalService.rentCar("123");

        assertTrue(result);
        assertFalse(car.isAvailable());
        verify(carRepository).updateCar(car);
    }

    @Test
    void testRentCarFailsIfAlreadyRented() {
        Car car = new Car("456", "Honda", false);
        when(carRepository.findByRegistrationNumber("456")).thenReturn(Optional.of(car));

        boolean result = carRentalService.rentCar("456");

        assertFalse(result);
        verify(carRepository, never()).updateCar(any());
    }

    @Test
    void testRentCarUpdatesCar() {
        Car car = new Car("789", "Audi", true);
        when(carRepository.findByRegistrationNumber("789")).thenReturn(Optional.of(car));

        carRentalService.rentCar("789");

        verify(carRepository, times(1)).updateCar(car);
    }

    @Test
    void testRentCarFailsIfCarNotFound() {
        when(carRepository.findByRegistrationNumber("nonexistent")).thenReturn(Optional.empty());

        boolean result = carRentalService.rentCar("nonexistent");

        assertFalse(result);
        verify(carRepository, never()).updateCar(any());
    }

    @Test
    void testReturnCarSuccess() {
        Car car = new Car("123", "Toyota", false);
        when(carRepository.findByRegistrationNumber("123")).thenReturn(Optional.of(car));

        carRentalService.returnCar("123");

        assertTrue(car.isAvailable());
        verify(carRepository).updateCar(car);
    }

    @Test
    void testReturnCarDoesNothingIfCarNotFound() {
        when(carRepository.findByRegistrationNumber("nonexistent")).thenReturn(Optional.empty());

        carRentalService.returnCar("nonexistent");

        verify(carRepository, never()).updateCar(any());
    }

    @Test
    void testReturnCarAlreadyAvailable() {
        Car car = new Car("123", "Toyota", true);
        when(carRepository.findByRegistrationNumber("123")).thenReturn(Optional.of(car));

        carRentalService.returnCar("123");

        assertTrue(car.isAvailable());
        verify(carRepository).updateCar(car);
    }

    @Test
    void testAddCarSuccess() {
        Car car = new Car("123", "Toyota", true);
        when(carRepository.findByRegistrationNumber("123")).thenReturn(Optional.empty());

        boolean result = carRentalService.addCar(car);

        assertTrue(result);
        verify(carRepository).addCar(car);
    }

    @Test
    void testAddCarFailsIfCarAlreadyExists() {
        Car existingCar = new Car("123", "Toyota", true);
        Car newCar = new Car("123", "Honda", true);
        when(carRepository.findByRegistrationNumber("123")).thenReturn(Optional.of(existingCar));

        boolean result = carRentalService.addCar(newCar);

        assertFalse(result);
        verify(carRepository, never()).addCar(any());
    }

    @Test
    void testFindCarsByModel() {
        List<Car> allCars = Arrays.asList(
                new Car("123", "Toyota", true),
                new Car("456", "Honda", true),
                new Car("789", "Toyota", false)
        );
        when(carRepository.getAllCars()).thenReturn(allCars);

        List<Car> result = carRentalService.findCarsByModel("Toyota");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(car -> car.getModel().equalsIgnoreCase("Toyota")));
        verify(carRepository).getAllCars();
    }

    @Test
    void testFindCarsByModelCaseInsensitive() {
        List<Car> allCars = Arrays.asList(
                new Car("123", "Toyota", true),
                new Car("456", "Honda", true),
                new Car("789", "TOYOTA", false)
        );
        when(carRepository.getAllCars()).thenReturn(allCars);

        List<Car> result = carRentalService.findCarsByModel("toyota");

        assertEquals(2, result.size());
        verify(carRepository).getAllCars();
    }

    @Test
    void testFindCarsByModelReturnsEmptyListWhenNoMatch() {
        List<Car> allCars = Arrays.asList(
                new Car("123", "Toyota", true),
                new Car("456", "Honda", true)
        );
        when(carRepository.getAllCars()).thenReturn(allCars);

        List<Car> result = carRentalService.findCarsByModel("BMW");

        assertTrue(result.isEmpty());
        verify(carRepository).getAllCars();
    }
}