package businessLogic;

import java.util.Date;
import java.util.List;

import dataAccess.HibernateDataAccess;
import domain.Driver;
import domain.Ride;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.RideAlreadyExistException;

public class BLFacadeImplementation implements BLFacade {

    private HibernateDataAccess dbManager;

    public BLFacadeImplementation() {
        System.out.println("Creating BLFacadeImplementation with HIBERNATE");
        dbManager = new HibernateDataAccess();
    }

    public BLFacadeImplementation(HibernateDataAccess da) {
        System.out.println("Creating BLFacadeImplementation with injected Hibernate DAO");
        dbManager = da;
    }

    
    @Override
    public List<Driver> getAllDrivers() {
        return dbManager.getAllDrivers();
    }

    @Override
    public List<String> getDepartCities() {
        return dbManager.getDepartCities();
    }

    @Override
    public List<String> getDestinationCities(String from) {
        return dbManager.getArrivalCities(from);
    }

    @Override
    public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
            throws RideMustBeLaterThanTodayException, RideAlreadyExistException {

        return dbManager.createRide(from, to, date, nPlaces, price, driverEmail);
    }

    @Override
    public List<Ride> getRides(String from, String to, Date date) {
        return dbManager.getRides(from, to, date);
    }
    @Override
    public List<Ride> getRidesByDriver(String driverEmail) {
        return dbManager.getRidesByDriver(driverEmail);
    }

    @Override
    public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
        return dbManager.getThisMonthDatesWithRides(from, to, date);
    }

    @Override
    public void initializeBD() {
        dbManager.initializeDB();
    }
/*
    @Override
    public void close() {
        // Si quieres podrías cerrar el SessionFactory:
        // HibernateUtil.shutdown();
    }*/
}
