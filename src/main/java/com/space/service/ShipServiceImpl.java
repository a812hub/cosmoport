package com.space.service;

import com.space.exception.NotValidIdNumberException;
import com.space.exception.IdNotFoundException;

import com.space.exception.NotValidDataException;
import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipRepository shipRepository;

    @Override
    public List<Ship> findAll(Specification<Ship> spec, Pageable pageable) {
        return shipRepository.findAll(spec, pageable).getContent();
    }

    @Override
    public long count(Specification<Ship> spec) {
        return shipRepository.count(spec);
    }

    @Override
    public Ship findById(Long id) throws IdNotFoundException, NotValidIdNumberException {
        if (id <= 0) {
            throw new NotValidIdNumberException();
        }
        if (!shipRepository.findById(id).isPresent()) {
            throw new IdNotFoundException();
        }
        return shipRepository.findById(id).get();
    }

    @Override
    public Ship addShip(Ship ship) throws NotValidDataException {

        nullCheck(ship);
        validDataCheck(ship);
        if (ship.isUsed() == null) {
            ship.setUsed(false);
        }
        ship.setRating(countRating(ship));

        return shipRepository.save(ship);
    }

    @Override
    public Ship editShip(Ship newShip, Long id) throws NotValidDataException, NotValidIdNumberException, IdNotFoundException {
        Ship oldShip = findById(id);

        newShip.setId(id);

        if (newShip.getName() == null) {
            newShip.setName(oldShip.getName());
        }
        if (newShip.getPlanet() == null) {
            newShip.setPlanet(oldShip.getPlanet());
        }
        if (newShip.getShipType() == null) {
            newShip.setShipType(oldShip.getShipType());
        }
        if (newShip.getProdDate() == null) {
            newShip.setProdDate(oldShip.getProdDate());
        }
        if (newShip.isUsed() == null) {
            newShip.setUsed(oldShip.isUsed());
        }
        if (newShip.getSpeed() == null) {
            newShip.setSpeed(oldShip.getSpeed());
        }
        if (newShip.getCrewSize() == null) {
            newShip.setCrewSize(oldShip.getCrewSize());
        }

        validDataCheck(newShip);
        newShip.setRating(countRating(newShip));

        return shipRepository.save(newShip);
    }

    @Override
    public void deleteById(long id) throws NotValidIdNumberException, IdNotFoundException {
        findById(id);
        shipRepository.deleteById(id);
    }

    private double countRating(Ship ship) {
        double k = ship.isUsed() ? 0.5 : 1;
        double rating = 80 * ship.getSpeed() * k / (3019 - (ship.getProdDate().getYear() + 1900) + 1);
        return new BigDecimal(rating).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private void validDataCheck(Ship ship) throws NotValidDataException {
        if (ship.getName().length() > 50 || ship.getName().length() == 0) {
            throw new NotValidDataException();
        }
        if (ship.getPlanet().length() > 50 || ship.getPlanet().length() == 0) {
            throw new NotValidDataException();
        }
        if (ship.getSpeed() > 0.99 || ship.getSpeed() < 0.01) {
            throw new NotValidDataException();
        }
        if (ship.getCrewSize() > 9999 || ship.getCrewSize() < 1) {
            throw new NotValidDataException();
        }
        if (ship.getProdDate().getTime() < 0) {
            throw new NotValidDataException();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        if (calendar.get(Calendar.YEAR) < 2800 || calendar.get(Calendar.YEAR) > 3019) {
            throw new NotValidDataException();
        }
    }

    private void nullCheck(Ship ship) throws NotValidDataException {
        if (ship.getName() == null || ship.getPlanet() == null || ship.getShipType() == null
                || ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null) {
            throw new NotValidDataException();
        }
    }
}
