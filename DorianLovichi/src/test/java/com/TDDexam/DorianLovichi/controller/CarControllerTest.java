package com.TDDexam.DorianLovichi.controller;

import com.TDDexam.DorianLovichi.model.Car;
import com.TDDexam.DorianLovichi.service.CarRentalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarRentalService carRentalService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllCars() throws Exception {
        List<Car> cars = Arrays.asList(
                new Car("ABC123", "Toyota", true),
                new Car("DEF456", "Honda", false)
        );
        when(carRentalService.getAllCars()).thenReturn(cars);

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].registrationNumber").value("ABC123"))
                .andExpect(jsonPath("$[0].model").value("Toyota"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].registrationNumber").value("DEF456"))
                .andExpect(jsonPath("$[1].model").value("Honda"))
                .andExpect(jsonPath("$[1].available").value(false));

        verify(carRentalService).getAllCars();
    }

    @Test
    void testGetAllCarsReturnsEmptyList() throws Exception {
        when(carRentalService.getAllCars()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(carRentalService).getAllCars();
    }

    @Test
    void testRentCarSuccess() throws Exception {
        when(carRentalService.rentCar("ABC123")).thenReturn(true);

        mockMvc.perform(post("/cars/rent/ABC123"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(carRentalService).rentCar("ABC123");
    }

    @Test
    void testRentCarFailure() throws Exception {
        when(carRentalService.rentCar("XYZ789")).thenReturn(false);

        mockMvc.perform(post("/cars/rent/XYZ789"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(carRentalService).rentCar("XYZ789");
    }

    @Test
    void testReturnCar() throws Exception {
        mockMvc.perform(post("/cars/return/ABC123"))
                .andExpect(status().isOk());

        verify(carRentalService).returnCar("ABC123");
    }

    @Test
    void testAddCarSuccess() throws Exception {
        when(carRentalService.addCar(any(Car.class))).thenReturn(true);

        Car newCar = new Car("ABC123", "Peugeot", true);

        mockMvc.perform(post("/cars/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isOk())
                .andExpect(content().string("Car added successfully"));

        verify(carRentalService).addCar(any(Car.class));
    }

    @Test
    void testAddCarFailsIfDuplicate() throws Exception {
        when(carRentalService.addCar(any(Car.class))).thenReturn(false);

        Car duplicateCar = new Car("XYZ789", "Renault", true);

        mockMvc.perform(post("/cars/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateCar)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Car with same registration number already exists"));

        verify(carRentalService).addCar(any(Car.class));
    }

    @Test
    void testSearchCarByModelReturnsCars() throws Exception {
        Car car1 = new Car("123", "Tesla", true);
        Car car2 = new Car("456", "Tesla", false);
        when(carRentalService.findCarsByModel("Tesla")).thenReturn(Arrays.asList(car1, car2));

        mockMvc.perform(get("/cars/search").param("model", "Tesla"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].registrationNumber").value("123"))
                .andExpect(jsonPath("$[0].model").value("Tesla"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].registrationNumber").value("456"))
                .andExpect(jsonPath("$[1].model").value("Tesla"))
                .andExpect(jsonPath("$[1].available").value(false));

        verify(carRentalService).findCarsByModel("Tesla");
    }

    @Test
    void testSearchCarByModelReturnsEmptyList() throws Exception {
        when(carRentalService.findCarsByModel("Nonexistent")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cars/search").param("model", "Nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(carRentalService).findCarsByModel("Nonexistent");
    }

    @Test
    void testSearchCarsByModelWithoutParameter() throws Exception {
        mockMvc.perform(get("/cars/search"))
                .andExpect(status().isBadRequest());

        verify(carRentalService, never()).findCarsByModel(anyString());
    }
}