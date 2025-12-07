package eredua.bean;

import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;
import dataAccess.HibernateDataAccess;

public class FacadeBean {

    private static FacadeBean singleton = new FacadeBean();
    private static BLFacade facadeInterface;

    private FacadeBean() {
        try {
            System.out.println("=== CREATING FACADE BEAN ===");
            facadeInterface = new BLFacadeImplementation(new HibernateDataAccess());
            
            // Inicializar la BD aquí
            System.out.println("=== INITIALIZING DATABASE ===");
            facadeInterface.initializeBD();
            System.out.println("=== DATABASE INITIALIZED SUCCESSFULLY ===");
            
        } catch (Exception e) {
            System.out.println("FacadeBean: error creando la lógica de negocio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static BLFacade getBusinessLogic() {
        return facadeInterface;
    }
}