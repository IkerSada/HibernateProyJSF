package eredua.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import businessLogic.BLFacade;
import domain.Ride;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("queryRidesBean")
@SessionScoped
public class QueryRidesBean implements Serializable {
    private String selectedDepartCity;
    private String selectedArrivalCity;
    private Date selectedDate;
    private List<Ride> filteredRides;
    
    private List<String> departCities;
    private List<String> arrivalCities;
    private List<Date> datesWithRides;

    public QueryRidesBean() {
        this.selectedDate = getDateWithoutTime(new Date());
        System.out.println("QueryRidesBean initialized with date: " + selectedDate);
        loadInitialData();
    }

    // Data baten orduak kentzeko metodoa
    private Date getDateWithoutTime(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void loadInitialData() {
        try {
            BLFacade facadeBL = FacadeBean.getBusinessLogic();
            
            this.departCities = facadeBL.getDepartCities();
            System.out.println("Loaded depart cities: " + departCities);
            
            if (!departCities.isEmpty()) {
                this.selectedDepartCity = departCities.get(0);
                System.out.println("Auto-selected depart city: " + selectedDepartCity);
                loadArrivalCities();
            }
            
        } catch (Exception e) {
            showError("Error loading initial data");
            e.printStackTrace();
        }
    }


    private void loadArrivalCities() {
        try {
            if (selectedDepartCity != null && !selectedDepartCity.isEmpty()) {
                BLFacade facadeBL = FacadeBean.getBusinessLogic();
                this.arrivalCities = facadeBL.getDestinationCities(selectedDepartCity);
                System.out.println("Loaded arrival cities for " + selectedDepartCity + ": " + arrivalCities);
                
                if (!arrivalCities.isEmpty()) {
                    this.selectedArrivalCity = arrivalCities.get(0);
                    System.out.println("Auto-selected arrival city: " + selectedArrivalCity);
                    refreshAllData(); // ALDATU (aurretik loadDatesWithRides() eta searchRides() deitzen zen)
                } else {
                    this.selectedArrivalCity = null;
                    this.datesWithRides = null;
                    this.filteredRides = null;
                }
            }
        } catch (Exception e) {
            showError("Error loading arrival cities");
            e.printStackTrace();
        }
    }

    private void loadDatesWithRides() {
        try {
            if (selectedDepartCity != null && !selectedDepartCity.isEmpty() &&
                selectedArrivalCity != null && !selectedArrivalCity.isEmpty()) {
                
                BLFacade facadeBL = FacadeBean.getBusinessLogic();
                
                // Datuak lortu (hautatutako datarekin, ez gaurkoarekin)
                this.datesWithRides = facadeBL.getThisMonthDatesWithRides(
                    selectedDepartCity, selectedArrivalCity, selectedDate);
                
                System.out.println("Raw dates with rides for " + selectedDepartCity + " to " + selectedArrivalCity + ": " + datesWithRides);
                
                // Debug informazioa
                if (datesWithRides != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    System.out.println("Formatted dates with rides:");
                    for (Date d : datesWithRides) {
                        System.out.println("  - " + sdf.format(d));
                    }
                }
            } else {
                this.datesWithRides = null;
            }
        } catch (Exception e) {
            showError("Error loading dates with rides");
            e.printStackTrace();
        }
    }

    // UI-rako data formaturik onena itzultzeko metodoa
    public List<String> getDatesWithRidesFormatted() {
        List<String> formattedDates = new ArrayList<>();
        if (datesWithRides != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Date date : datesWithRides) {
                formattedDates.add(sdf.format(date));
            }
        }
        return formattedDates;
    }




    // Datuak debug egiteko metodoa
    public void debugDates() {
        System.out.println("=== DATE DEBUG INFO ===");
        System.out.println("Selected Date: " + selectedDate);
        System.out.println("Selected Date (clean): " + getDateWithoutTime(selectedDate));
        
        if (datesWithRides != null) {
            System.out.println("Dates with rides count: " + datesWithRides.size());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdfSimple = new SimpleDateFormat("yyyy-MM-dd");
            
            for (int i = 0; i < datesWithRides.size(); i++) {
                Date d = datesWithRides.get(i);
                System.out.println("Date " + i + ": " + sdf.format(d) + " -> " + sdfSimple.format(d));
            }
        } else {
            System.out.println("No dates with rides");
        }
        
        if (filteredRides != null) {
            System.out.println("Filtered rides count: " + filteredRides.size());
            SimpleDateFormat sdfSimple = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0; i < filteredRides.size(); i++) {
                Ride ride = filteredRides.get(i);
                System.out.println("Ride " + i + " date: " + sdfSimple.format(ride.getDate()));
            }
        }
        System.out.println("======================");
    }

    private void showError(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    private void showInfo(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
    }

    // Getter eta Setter metodoak
    public String getSelectedDepartCity() { return selectedDepartCity; }
    public void setSelectedDepartCity(String selectedDepartCity) { 
        this.selectedDepartCity = selectedDepartCity;
    }

    public String getSelectedArrivalCity() { return selectedArrivalCity; }
    public void setSelectedArrivalCity(String selectedArrivalCity) { 
        this.selectedArrivalCity = selectedArrivalCity;
    }

    public Date getSelectedDate() { return selectedDate; }
    public void setSelectedDate(Date selectedDate) { 
        this.selectedDate = getDateWithoutTime(selectedDate);
    }

    public List<Ride> getFilteredRides() { return filteredRides; }
    public void setFilteredRides(List<Ride> filteredRides) { this.filteredRides = filteredRides; }

    public List<String> getDepartCities() { return departCities; }
    public void setDepartCities(List<String> departCities) { this.departCities = departCities; }

    public List<String> getArrivalCities() { return arrivalCities; }
    public void setArrivalCities(List<String> arrivalCities) { this.arrivalCities = arrivalCities; }

    public List<Date> getDatesWithRides() { return datesWithRides; }
    public void setDatesWithRides(List<Date> datesWithRides) { this.datesWithRides = datesWithRides; }
    
    
    
    
    public void onDepartCityChange() {
        System.out.println("Depart city changed to: " + selectedDepartCity);
        loadArrivalCities();
    }

    public void onArrivalCityChange() {
        System.out.println("Arrival city changed to: " + selectedArrivalCity);
        if (selectedArrivalCity != null && !selectedArrivalCity.isEmpty()) {
            refreshData();
        }
    }

    public void onDateSelect() {
        System.out.println("Date selected: " + selectedDate);
        
        // Data garbitu
        this.selectedDate = getDateWithoutTime(selectedDate);
        System.out.println("Cleaned date: " + selectedDate);
        
        // Solo buscar rides si tenemos ciudades seleccionadas
        if (selectedDepartCity != null && selectedArrivalCity != null) {
            searchRides();
        }
    }

    private void refreshData() {
        if (selectedDepartCity != null && selectedArrivalCity != null) {
            loadDatesWithRides();
            
            // Solo buscar rides si hay una fecha seleccionada
            if (selectedDate != null) {
                searchRides();
            }
        }
    }

    public void searchRides() {
        try {
            this.filteredRides = null;
            
            if (selectedDepartCity != null && !selectedDepartCity.isEmpty() &&
                selectedArrivalCity != null && !selectedArrivalCity.isEmpty() &&
                selectedDate != null) {
                
                System.out.println("Searching rides for: " + selectedDepartCity + 
                                 " to " + selectedArrivalCity + " on " + selectedDate);
                
                BLFacade facadeBL = FacadeBean.getBusinessLogic();
                
                // Datuak lortzeko data garbitua erabili
                Date cleanDate = getDateWithoutTime(selectedDate);
                this.filteredRides = facadeBL.getRides(selectedDepartCity, selectedArrivalCity, cleanDate);
                
                System.out.println("Found " + (filteredRides != null ? filteredRides.size() : 0) + " rides");
                
                if (filteredRides == null || filteredRides.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
                        "No rides found for the selected criteria"));
                }
            }
        } catch (Exception e) {
            showError("Error searching rides: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
 // Datuak guztiz eguneratzeko metodoa GEHITU
    public void refreshAllData() {
        System.out.println("Refreshing all data...");
        loadDatesWithRides();
        searchRides();
    }
}