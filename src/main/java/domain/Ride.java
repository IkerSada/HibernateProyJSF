package domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Ride implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rideNumber;

    @Column(name = "fromCity")
    private String fromCity;

    @Column(name = "toCity")
    private String toCity;

    private int nPlaces;

    @Temporal(TemporalType.DATE)
    private Date date;

    private float price;

    @ManyToOne
    @JoinColumn(name = "driver_email")
    private Driver driver;

    public Ride() {}

    public Ride(String from, String to, Date date, int nPlaces, float price, Driver driver) {
        this.fromCity = from;
        this.toCity = to;
        this.nPlaces = nPlaces;
        this.date = date;
        this.price = price;
        this.driver = driver;
    }

    public Integer getRideNumber() {
        return rideNumber;
    }

    public String getFrom() {
        return fromCity;
    }

    public void setFrom(String origin) {
        this.fromCity = origin;
    }

    public String getTo() {
        return toCity;
    }

    public void setTo(String destination) {
        this.toCity = destination;
    }

    public int getnPlaces() {
        return nPlaces;
    }

    public void setnPlaces(int nPlaces) {
        this.nPlaces = nPlaces;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    @Override
    public String toString() {
        return rideNumber + ";" + fromCity + ";" + toCity + ";" + date;
    }
}
