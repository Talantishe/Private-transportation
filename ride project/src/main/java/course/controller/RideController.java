package course.controller;

import course.entity.Ride;
import course.entity.User;
import course.repository.RideRepository;
import course.repository.UserRepository;
import course.enums.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public RideController(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Ride>> getAllRides() {
        List<Ride> rides = rideRepository.findAll();
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable Long id) {
        Optional<Ride> optionalRide = rideRepository.findById(id);
        if (optionalRide.isPresent()) {
            return ResponseEntity.ok(optionalRide.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<Ride> createRide(@RequestBody Ride ride) {
        Optional<User> driver = userRepository.findById(ride.getDriver().getId());
        Optional<User> passenger = userRepository.findById(ride.getPassenger().getId());

        if (driver.isPresent() && passenger.isPresent() && driver.get().getRole() == UserRole.DRIVER && passenger.get().getRole() == UserRole.PASSENGER) {
            Ride createdRide = rideRepository.save(ride);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRide);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ride> updateRide(@PathVariable Long id, @RequestBody Ride ride) {
        Optional<Ride> optionalRide = rideRepository.findById(id);
        if (optionalRide.isPresent()) {
            Ride existingRide = optionalRide.get();

            Optional<User> driver = userRepository.findById(ride.getDriver().getId());
            Optional<User> passenger = userRepository.findById(ride.getPassenger().getId());

            if (driver.isPresent() && passenger.isPresent() && driver.get().getRole() == UserRole.DRIVER && passenger.get().getRole() == UserRole.PASSENGER) {
                existingRide.setStartLocation(ride.getStartLocation());
                existingRide.setEndLocation(ride.getEndLocation());
                existingRide.setDepartureTime(ride.getDepartureTime());
                existingRide.setArrivalTime(ride.getArrivalTime());
                existingRide.setDistance(ride.getDistance());
                existingRide.setPrice(ride.getPrice());
                existingRide.setStatus(ride.getStatus());
                existingRide.setDriver(ride.getDriver());
                existingRide.setPassenger(ride.getPassenger());
                Ride updatedRide = rideRepository.save(existingRide);
                return ResponseEntity.ok(updatedRide);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        Optional<Ride> optionalRide = rideRepository.findById(id);
        if (optionalRide.isPresent()) {
            rideRepository.delete(optionalRide.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

