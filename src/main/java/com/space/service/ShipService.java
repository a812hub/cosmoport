package com.space.service;

import com.space.exception.NotValidIdNumberException;
import com.space.exception.IdNotFoundException;
import com.space.exception.NotValidDataException;
import com.space.model.Ship;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShipService {
    List<Ship> findAll(Specification<Ship> spec, Pageable pageable);
    long count(Specification<Ship> spec);
    Ship findById(Long id) throws IdNotFoundException, NotValidIdNumberException;
    Ship addShip(Ship ship) throws NotValidDataException;
    Ship editShip(Ship ship, Long id) throws NotValidDataException, NotValidIdNumberException, IdNotFoundException;
    void deleteById(long id) throws NotValidIdNumberException, IdNotFoundException;
}
