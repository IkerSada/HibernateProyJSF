package eredua.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

import businessLogic.BLFacade;
import domain.Driver;
import domain.Ride;

@Named("bidaiGuztiakBean")
@SessionScoped
public class BidaiGuztiak implements Serializable {

    private String selectedDriver;
    private List<Driver> allDrivers;
    private List<Ride> driverRides;  

    public BidaiGuztiak() {
        loadDrivers();
    }

    public void loadDrivers() {
        BLFacade facade = FacadeBean.getBusinessLogic();
        this.allDrivers = facade.getAllDrivers();
    }

    public void loadRidesOfDriver() {
        if (selectedDriver != null && !selectedDriver.isEmpty()) {
            BLFacade facade = FacadeBean.getBusinessLogic();
            this.driverRides = facade.getRidesByDriver(selectedDriver);
        }
    }

    // GETTERS / SETTERS
    public String getSelectedDriver() { return selectedDriver; }
    public void setSelectedDriver(String selectedDriver) { this.selectedDriver = selectedDriver; }

    public List<Driver> getAllDrivers() { return allDrivers; }

    public List<Ride> getDriverRides() { return driverRides; }
}
