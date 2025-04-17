package com.TDDexam.DorianLovichi.controller;

import com.TDDexam.DorianLovichi.model.Car;
import com.TDDexam.DorianLovichi.service.CarRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    CarRentalService carRentalService;

    @GetMapping
    public List<Car> getAllCars() {
        return carRentalService.getAllCars();
    }

    @PostMapping("/rent/{registrationNumber}")
    public boolean rentCar(@PathVariable String registrationNumber) {
        return carRentalService.rentCar(registrationNumber);
    }

    @PostMapping("/return/{registrationNumber}")
    public void returnCar(@PathVariable String registrationNumber) {
        carRentalService.returnCar(registrationNumber);
    }

    // Fix these two endpoints by removing the duplicate /cars prefix
    @PostMapping("/add")
    public ResponseEntity<String> addCar(@RequestBody Car car) {
        boolean added = carRentalService.addCar(car);
        if (added) {
            return ResponseEntity.ok("Car added successfully");
        } else {
            return ResponseEntity.badRequest().body("Car with same registration number already exists");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Car>> searchCarsByModel(@RequestParam String model) {
        List<Car> cars = carRentalService.findCarsByModel(model);
        return ResponseEntity.ok(cars);
    }
}
