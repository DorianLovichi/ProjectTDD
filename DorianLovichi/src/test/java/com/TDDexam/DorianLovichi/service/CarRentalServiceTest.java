package com.TDDexam.DorianLovichi.service;

import com.TDDexam.DorianLovichi.model.Car;
import com.TDDexam.DorianLovichi.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CarRentalServiceTest {

    private CarRepository carRepository;
    private CarRentalService carRentalService;

    @BeforeEach
    void setUp() {
        carRepository = mock(CarRepository.class);
        carRentalService = new CarRentalService(carRepository); // ← direct via constructeur
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

        verify(carRepository, times(1)).updateCar(car); // ← ton test ici
    }
}
