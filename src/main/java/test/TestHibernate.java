package test;
import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;
import domain.Driver;
import domain.Ride;
import dataAccess.HibernateDataAccess;

import java.util.Date;
import java.util.List;

public class TestHibernate {
    public static void main(String[] args) {
        try {
            // Inicializar la capa de negocio con Hibernate
            BLFacade bl = new BLFacadeImplementation(new HibernateDataAccess());

            // Inicializar la BD (crea drivers y rides de ejemplo)
            bl.initializeBD();

            // Probar listado de ciudades de origen
            List<String> departCities = bl.getDepartCities();
            System.out.println("Depart Cities: " + departCities);

            // Probar listado de destinos desde Donostia
            List<String> arrivalCities = bl.getDestinationCities("Donostia");
            System.out.println("Arrival Cities from Donostia: " + arrivalCities);

            // Probar creación de un ride nuevo
            Ride ride = bl.createRide(
                "Bilbo",
                "Donostia",
                new Date(System.currentTimeMillis() + 86400000), // mañana
                4,
                10.0f,
                "driver1@gmail.com"
            );
            System.out.println("Ride created: " + ride);

            // Probar búsqueda de rides
            List<Ride> rides = bl.getRides("Bilbo", "Donostia", new Date(System.currentTimeMillis() + 86400000));
            System.out.println("Rides found: " + rides);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
