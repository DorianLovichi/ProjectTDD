package com.TDDexam.DorianLovichi.controller;


import com.TDDexam.DorianLovichi.model.Car;
import com.TDDexam.DorianLovichi.service.CarRentalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;


import java.util.List;

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
    void testAddCarSuccess() throws Exception {
        when(carRentalService.addCar(any(Car.class))).thenReturn(true);

        Car newCar = new Car("ABC123", "Peugeot", true);
        mockMvc.perform(post("/cars/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isOk())
                .andExpect(content().string("Car added successfully"));
    }

    @Test
    void testAddCarFailsIfDuplicate() throws Exception {
        Car duplicateCar = new Car("XYZ789", "Renault", true);
        when(carRentalService.addCar(duplicateCar)).thenReturn(false);

        mockMvc.perform(post("/cars/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateCar)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Car with same registration number already exists"));
    }

    @Test
    void testSearchCarByModelReturnsCars() throws Exception {
        Car car1 = new Car("123", "Tesla", true);
        Car car2 = new Car("456", "Tesla", false);
        when(carRentalService.findCarsByModel("Tesla")).thenReturn(List.of(car1, car2));

        mockMvc.perform(get("/cars/search?model=Tesla"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].registrationNumber").value("123"))
                .andExpect(jsonPath("$[1].registrationNumber").value("456"));
    }

    @Test
    void testSearchCarByModelReturnsEmptyList() throws Exception {
        when(carRentalService.findCarsByModel("Nonexistent")).thenReturn(List.of());

        mockMvc.perform(get("/cars/search?model=Nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
