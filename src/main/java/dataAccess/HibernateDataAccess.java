package dataAccess;

import domain.Driver;
import domain.Ride;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.*;
import org.hibernate.query.Query;

public class HibernateDataAccess {

    public HibernateDataAccess() {
        System.out.println("HibernateDataAccess created");
    }

    /* ============================================================
           INITIALIZE DB
       ============================================================ */

    public void initializeDB() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Calendar today = Calendar.getInstance();
        int month = today.get(Calendar.MONTH) + 1;  
        int year = today.get(Calendar.YEAR);

        try {
            Driver driver1 = new Driver("driver1@gmail.com", "Aitor Fernandez");
            Driver driver2 = new Driver("driver2@gmail.com", "Ane Gaztañaga");
            Driver driver3 = new Driver("driver3@gmail.com", "Test driver");

            driver1.addRide("Donostia", "Bilbo", newDate(year, month, 15), 4, 7);
            driver1.addRide("Donostia", "Gasteiz", newDate(year, month, 6), 4, 8);
            driver1.addRide("Bilbo", "Donostia", newDate(year, month, 25), 4, 4);

            driver2.addRide("Donostia", "Bilbo", newDate(year, month, 15), 3, 3);
            driver2.addRide("Bilbo", "Donostia", newDate(year, month, 25), 2, 5);

            driver3.addRide("Bilbo", "Donostia", newDate(year, month, 14), 1, 3);

            session.persist(driver1);
            session.persist(driver2);
            session.persist(driver3);

            tx.commit();

            System.out.println("Hibernate database initialized");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }

        session.close();
    }

    private Date newDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /* ============================================================
           GET DEPART CITIES
       ============================================================ */
    public List<String> getDepartCities() {

        Session session = HibernateUtil.getSessionFactory().openSession();

        Query<String> query =
                session.createQuery("select distinct r.fromCity from Ride r order by r.fromCity", String.class);

        List<String> res = query.getResultList();
        session.close();
        return res;
    }

    /* ============================================================
           GET ARRIVAL CITIES
       ============================================================ */
    public List<String> getArrivalCities(String from) {

        Session session = HibernateUtil.getSessionFactory().openSession();

        Query<String> query =
                session.createQuery("select distinct r.toCity from Ride r where r.fromCity = :f order by r.toCity", String.class);

        query.setParameter("f", from);

        List<String> res = query.getResultList();
        session.close();
        return res;
    }

    /* ============================================================
           CREATE RIDE
       ============================================================ */
    public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
            throws RideAlreadyExistException, RideMustBeLaterThanTodayException {

        System.out.println(">> Hibernate: createRide " + from + " -> " + to + " driver=" + driverEmail);

        if (new Date().compareTo(date) > 0)
            throw new RideMustBeLaterThanTodayException("Ride date must be later than today");

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Driver driver = session.get(Driver.class, driverEmail);

            if (driver == null) {
                tx.rollback();
                session.close();
                return null;
            }

            // check if ride exists
            Query<Long> existsQuery = session.createQuery(
                    "select count(r) from Ride r " +
                            "where r.fromCity = :f and r.toCity = :t and r.date = :d " +
                            "and r.driver.email = :e",
                    Long.class);

            existsQuery.setParameter("f", from);
            existsQuery.setParameter("t", to);
            existsQuery.setParameter("d", date);
            existsQuery.setParameter("e", driverEmail);

            if (existsQuery.getSingleResult() > 0) {
                tx.rollback();
                throw new RideAlreadyExistException("A ride with same values exists for this driver");
            }

            // create ride using domain method
            Ride ride = driver.addRide(from, to, date, nPlaces, price);

            session.save(ride);
            session.update(driver);
            tx.commit();

            session.close();
            return ride;

        } catch (RideAlreadyExistException ex) {
            throw ex;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }

        session.close();
        return null;
    }

    /* ============================================================
           GET RIDES
       ============================================================ */
    public List<Ride> getRides(String from, String to, Date date) {

        Session session = HibernateUtil.getSessionFactory().openSession();

        Query<Ride> query = session.createQuery(
                "from Ride r where r.fromCity = :f and r.toCity = :t and r.date = :d",
                Ride.class);

        query.setParameter("f", from);
        query.setParameter("t", to);
        query.setParameter("d", cleanDate(date));

        List<Ride> rides = query.getResultList();
        session.close();
        return rides;
    }

    private Date cleanDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /* ============================================================
           GET DATES WITH RIDES FOR THE MONTH
       ============================================================ */
    public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {

        Date first = firstDayMonth(date);
        Date last = lastDayMonth(date);

        Session session = HibernateUtil.getSessionFactory().openSession();

        Query<Date> query =
                session.createQuery("select distinct r.date " +
                        "from Ride r " +
                        "where r.fromCity = :f and r.toCity = :t " +
                        "and r.date between :d1 and :d2", Date.class);

        query.setParameter("f", from);
        query.setParameter("t", to);
        query.setParameter("d1", first);
        query.setParameter("d2", last);

        List<Date> res = query.getResultList();
        session.close();
        return res;
    }

    private Date firstDayMonth(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cleanDate(cal.getTime());
    }

    private Date lastDayMonth(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cleanDate(cal.getTime());
    }
}
