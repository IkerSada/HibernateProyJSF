package domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Driver implements Serializable {

    @Id
    private String email;

    private String name;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    public Driver() {}

    public Driver(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }

    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }

    public List<Ride> getRides() { return rides; }

    public Ride addRide(String from, String to, Date date, int nPlaces, float price) {
        Ride ride = new Ride(from, to, date, nPlaces, price, this);
        rides.add(ride);
        return ride;
    }

    public boolean doesRideExists(String from, String to, Date date) {
        return rides.stream().anyMatch(r ->
                r.getFrom().equals(from) &&
                r.getTo().equals(to) &&
                r.getDate().equals(date)
        );
    }

    @Override
    public String toString() {
        return email + ";" + name;
    }
}
