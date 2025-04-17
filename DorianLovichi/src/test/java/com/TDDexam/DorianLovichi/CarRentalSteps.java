package com.TDDexam.DorianLovichi;


import com.TDDexam.DorianLovichi.model.Car;
import com.TDDexam.DorianLovichi.repository.CarRepository;
import com.TDDexam.DorianLovichi.service.CarRentalService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

@CucumberContextConfiguration
public class CarRentalSteps {

    private CarRentalService carRentalService;
    private CarRepository carRepository; // mocké à la main

    private List<Car> cars;
    private Car car;

    public CarRentalSteps() {
        // Création manuelle du mock
        carRepository = mock(CarRepository.class);
        carRentalService = new CarRentalService(carRepository);
    }

    @Given("des voitures sont disponibles")
    public void des_voitures_sont_disponibles() {
        car = new Car("123", "Toyota", true);
        cars = List.of(car);
        when(carRepository.getAllCars()).thenReturn(cars);
    }

    @When("je demande la liste des voitures")
    public void je_demande_la_liste_des_voitures() {
        cars = carRentalService.getAllCars();
    }

    @Then("toutes les voitures sont affichées")
    public void toutes_les_voitures_sont_affichées() {
        assertFalse(cars.isEmpty());
        assertEquals("Toyota", cars.get(0).getModel());
    }

    @Given("une voiture est disponible")
    public void une_voiture_est_disponible() {
        car = new Car("123", "Toyota", true);
        when(carRepository.findByRegistrationNumber("123")).thenReturn(Optional.of(car));
    }

    @When("je loue cette voiture")
    public void je_loue_cette_voiture() {
        carRentalService.rentCar(car.getRegistrationNumber());
        car.setAvailable(false);
    }

    @Then("la voiture n'est plus disponible")
    public void la_voiture_n_est_plus_disponible() {
        verify(carRepository).updateCar(car);
        assertFalse(car.isAvailable());
    }

    @Given("une voiture est louée")
    public void une_voiture_est_louée() {
        car = new Car("123", "Toyota", false);
        when(carRepository.findByRegistrationNumber("123")).thenReturn(Optional.of(car));
    }

    @When("je retourne cette voiture")
    public void je_retourne_cette_voiture() {
        carRentalService.returnCar(car.getRegistrationNumber());
        car.setAvailable(true);
    }

    @Then("la voiture est marquée comme disponible")
    public void la_voiture_est_marquée_comme_disponible() {
        verify(carRepository).updateCar(car);
        assertTrue(car.isAvailable());
    }
}