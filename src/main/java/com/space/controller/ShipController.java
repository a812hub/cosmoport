package com.space.controller;

import com.space.exception.NotValidIdNumberException;
import com.space.exception.IdNotFoundException;
import com.space.exception.NotValidDataException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import com.space.service.ShipSpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@ResponseBody
public class ShipController {

    @Autowired
    public ShipService shipService;

    @GetMapping(value = "/rest/ships")
    public List<Ship> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String planet,
            @RequestParam(required = false) ShipType shipType,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean isUsed,
            @RequestParam(required = false) Double minSpeed,
            @RequestParam(required = false) Double maxSpeed,
            @RequestParam(required = false) Integer minCrewSize,
            @RequestParam(required = false) Integer maxCrewSize,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,

            @RequestParam(defaultValue = "ID") ShipOrder order,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "3") Integer pageSize
    ) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Specification<Ship> spec = getSpec(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating);
        return shipService.findAll(spec, pageRequest);
    }

    @GetMapping("/rest/ships/count")
    public long getCount(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String planet,
            @RequestParam(required = false) ShipType shipType,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean isUsed,
            @RequestParam(required = false) Double minSpeed,
            @RequestParam(required = false) Double maxSpeed,
            @RequestParam(required = false) Integer minCrewSize,
            @RequestParam(required = false) Integer maxCrewSize,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating
    ) {
        Specification<Ship> spec = getSpec(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating);
        return shipService.count(spec);
    }

    @GetMapping(value = "/rest/ships/{id}")
    public Ship getById(@PathVariable(value = "id") Long id) {
        try {
            return shipService.findById(id);
        } catch (NotValidIdNumberException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is not valid", e);
        } catch (IdNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ship not found", e);
        }
    }

    @DeleteMapping(value = "/rest/ships/{id}")
    public void deleteById(@PathVariable(value = "id") Long id) {
        try {
            shipService.deleteById(id);
        } catch (NotValidIdNumberException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Valid Id Number", e);
        } catch (IdNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ship not found", e);
        }
    }

    @PostMapping(value = "/rest/ships/")
    public Ship create(@RequestBody Ship ship) {
        try {
            return shipService.addShip(ship);
        } catch (NotValidDataException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Valid Data", e);
        }
    }

    @PostMapping(value = "/rest/ships/{id}")
    @ResponseBody
    public Ship update(@RequestBody Ship ship, @PathVariable(value = "id") Long id) {
        try {
            return shipService.editShip(ship, id);
        } catch (NotValidDataException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Valid Data", e);
        } catch (NotValidIdNumberException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Valid Id Number", e);
        } catch (IdNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ship not found", e);
        }
    }

    private Specification<Ship> getSpec(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String planet,
            @RequestParam(required = false) ShipType shipType,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean isUsed,
            @RequestParam(required = false) Double minSpeed,
            @RequestParam(required = false) Double maxSpeed,
            @RequestParam(required = false) Integer minCrewSize,
            @RequestParam(required = false) Integer maxCrewSize,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating) {
        ShipSpecificationBuilder builder = new ShipSpecificationBuilder();

        if (name != null) {
            builder.with("name", ":", name);
        }
        if (planet != null) {
            builder.with("planet", ":", planet);
        }
        if (shipType != null) {
            builder.with("shipType", ":", shipType);
        }
        if (after != null) {
            builder.with("prodDate", ">", after);
        }
        if (before != null) {
            builder.with("prodDate", "<", before);
        }
        if (isUsed != null) {
            builder.with("isUsed", ":", isUsed);
        }
        if (minSpeed != null) {
            builder.with("speed", ">", minSpeed);
        }
        if (maxSpeed != null) {
            builder.with("speed", "<", maxSpeed);
        }
        if (minCrewSize != null) {
            builder.with("crewSize", ">", minCrewSize);
        }
        if (maxCrewSize != null) {
            builder.with("crewSize", "<", maxCrewSize);
        }
        if (minRating != null) {
            builder.with("rating", ">", minRating);
        }
        if (maxRating != null) {
            builder.with("rating", "<", maxRating);
        }

        return builder.build();
    }
}
