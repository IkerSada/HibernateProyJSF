package eredua.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	        
	        // Validaciones manuales
	        if (origin == null || origin.trim().isEmpty()) {
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	                "Depart city is required"));
	            return;
	        }
	        
	        if (destination == null || destination.trim().isEmpty()) {
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	                "Arrival city is required"));
	            return;
	        }
	        
	        if (seats == null || seats <= 0) {
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	                "Number of seats must be greater than 0"));
	            return;
	        }
	        
	        if (seats > 500) {
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	                "Number of seats cannot exceed 500"));
	            return;
	        }
	        
	        if (price <= 0) {
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	                "Price must be greater than 0"));
	            return;
	        }
	        
	        if (price > 500) {
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	                "Price cannot exceed €500"));
	            return;
	        }
	        
	        if (date == null) {
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	                "Date is required"));
	            return;
	        }
	        
	        // Validar que la fecha sea futura
	        Date today = new Date();
	        if (date.before(today)) {
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	                "Ride date must be in the future"));
	            return;
	        }
	        
	        if (driverEmail == null || driverEmail.trim().isEmpty()) {
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	                "Driver email is required"));
	            return;
	        }
	        
	        // Crear el ride
	        Ride newRide = facadeBL.createRide(origin, destination, date, seats, price, driverEmail);
	        
	        if (newRide != null) {
	            // Crear mensaje de éxito
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            String successMessage = String.format(
	                "Ride created successfully!<br/>" +
	                "From: %s<br/>" +
	                "To: %s<br/>" +
	                "Date: %s<br/>" +
	                "Seats: %d<br/>" +
	                "Price: €%.2f<br/>" +
	                "Driver: %s",
	                origin, destination, sdf.format(date), seats, price, driverEmail);
	            
	            this.datua = successMessage;
	            
	            // Mostrar mensaje en la interfaz
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", 
	                "Ride created successfully!"));
	            
	            System.out.println("✅ Ride created successfully: " + newRide);
	            System.out.println("✅ Ride ID: " + newRide.getRideNumber());
	            
	            // NO limpiar el formulario para ver los datos
	        } else {
	            System.out.println("❌ Failed to create ride (returned null)");
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	                "Failed to create ride"));
	        }
	        
	        System.out.println("=====================");

	    } catch (RideAlreadyExistException e) {
	        System.out.println("❌ RideAlreadyExistException: " + e.getMessage());
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	            "A ride with same values already exists for this driver"));
	        
	    } catch (RideMustBeLaterThanTodayException e) {
	        System.out.println("❌ RideMustBeLaterThanTodayException: " + e.getMessage());
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	            "Ride date must be later than today"));
	        
	    } catch (Exception e) {
	        System.out.println("❌ ERROR creating ride: " + e.getMessage());
	        e.printStackTrace();
	        
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
	            "An error occurred: " + e.getMessage()));
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

	// Formularioa garbitzeko metodoa (aukerakoa)
	public void clearForm() {
		this.origin = null;
		this.destination = null;
		this.seats = null;
		this.price = 0;
		this.date = new Date();
		this.datua = null;
	}
}