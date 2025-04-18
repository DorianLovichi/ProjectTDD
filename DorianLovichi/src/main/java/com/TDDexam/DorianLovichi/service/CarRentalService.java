package com.TDDexam.DorianLovichi.service;
import com.TDDexam.DorianLovichi.model.Car;
import com.TDDexam.DorianLovichi.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarRentalService {

    private final CarRepository carRepository;

    @Autowired
    public CarRentalService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> getAllCars() {
        return carRepository.getAllCars();
    }

    public boolean rentCar(String registrationNumber) {
        Optional<Car> car = carRepository.findByRegistrationNumber(registrationNumber);
        if (car.isPresent() && car.get().isAvailable()) {
            car.get().setAvailable(false);
            carRepository.updateCar(car.get());
            return true;
        }
        return false;
    }

    public void returnCar(String registrationNumber) {
        Optional<Car> car = carRepository.findByRegistrationNumber(registrationNumber);
        car.ifPresent(c -> {
            c.setAvailable(true);
            carRepository.updateCar(c);
        });
    }

    public boolean addCar(Car car) {
        Optional<Car> existing = carRepository.findByRegistrationNumber(car.getRegistrationNumber());
        if (existing.isPresent()) {
            return false;
        }
        carRepository.addCar(car);
        return true;
    }


    public List<Car> findCarsByModel(String model) {
        return carRepository.getAllCars().stream()
                .filter(c -> c.getModel().equalsIgnoreCase(model))
                .toList();
    }

}