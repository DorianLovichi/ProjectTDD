package com.TDDexam.DorianLovichi.controller;

import com.TDDexam.DorianLovichi.model.Car;
import com.TDDexam.DorianLovichi.service.CarRentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CarControllerTest {

    private CarRentalService carRentalService;
    private CarController carController;

    @BeforeEach
    void setUp() {
        carRentalService = mock(CarRentalService.class);
        carController = new CarController();
        carController.carRentalService = carRentalService;
    }

    @Test
    void testGetAllCars() {
        List<Car> cars = List.of(new Car("123ABC", "Toyota", true));
        when(carRentalService.getAllCars()).thenReturn(cars);

        List<Car> result = carController.getAllCars();

        assertEquals(1, result.size());
        assertEquals("Toyota", result.getFirst().getModel());
    }

    @Test
    void testRentCar() {
        when(carRentalService.rentCar("123ABC")).thenReturn(true);

        boolean result = carController.rentCar("123ABC");

        assertTrue(result);
    }

    @Test
    void testReturnCar() {
        doNothing().when(carRentalService).returnCar("123ABC");

        carController.returnCar("123ABC");

        verify(carRentalService, times(1)).returnCar("123ABC");
    }
}
