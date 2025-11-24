package eredua.bean;
import java.io.Serializable;
import java.util.Date;

import org.primefaces.event.SelectEvent;

import businessLogic.BLFacade;
import domain.Ride;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("createRideBean")
@SessionScoped
public class CreateRideBean implements Serializable {
	private Ride ride;
	private String origin;
	private String destination;
	private Date date;
	private Integer seats;
	private float price;
	private String datua; 
	private BLFacade facadeBL;
	private String driverEmail = "driver1@gmail.com";

	public CreateRideBean() {
		this.date = new Date();
	}
	
	public String getDriverEmail() { return driverEmail; }
	public void setDriverEmail(String driverEmail) { this.driverEmail = driverEmail; }

	public Integer getSeats() { return seats; }
	public void setSeats(Integer seats) { this.seats = seats; }

	public float getPrice() { return price; }
	public void setPrice(float price) { this.price = price; }

	public String getOrigin() { return origin; }
	public void setOrigin(String origin) { this.origin = origin; }

	public String getDestination() { return destination; }
	public void setDestination(String destination) { this.destination = destination; }

	public Date getDate() { 
		if (date == null) {
			date = new Date();
		}
		return date; 
	}
	public void setDate(Date date) { this.date = date; }

	public String getDatua() { return datua; }
	public void setDatua(String datua) { this.datua = datua; }

	public void createRide() {
	    try {
	        this.facadeBL = FacadeBean.getBusinessLogic();
	        
	        // DEBUG: Datuak erakutsi
	        System.out.println("=== CREATING RIDE ===");
	        System.out.println("Origin: " + origin);
	        System.out.println("Destination: " + destination);
	        System.out.println("Date: " + date);
	        System.out.println("Seats: " + seats);
	        System.out.println("Price: " + price);
	        System.out.println("Driver Email: " + driverEmail);
	        
	        Ride newRide = facadeBL.createRide(origin, destination, date, seats, price, driverEmail);

	        // DEBUG: Ride-a sortu ondoren
	        System.out.println("Ride created successfully: " + newRide);
	        System.out.println("Ride ID: " + (newRide != null ? newRide.getRideNumber() : "null"));
	        System.out.println("=====================");

	        // Rest of the code...
	    } catch (Exception e) {
	        System.out.println("ERROR creating ride: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	public String close() {
		// Itxi logikoa hemen
		return "main"; // edo nabigazio emaitza egokia
	}

	public void test() {
		String datuak = "Ride is Created. " + origin + " --> " + destination + 
				",   date: " + date + ", number of seats: " + seats + ",  price: " + price;
		System.out.println(datuak);

		// Datuak gorde datua atributuan
		this.datua = datuak;
	}

	public void onDateSelect(SelectEvent event) {
		this.date = (Date) event.getObject();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Data aukeratua: "+ this.date));
	}

	// Formularioa garbitzeko metodoa (aukerakoa)
	public void clearForm() {
		this.origin = null;
		this.destination = null;
		this.seats = null;
		this.price = (Float) null;
		this.date = new Date();
		this.datua = null;
	}
}