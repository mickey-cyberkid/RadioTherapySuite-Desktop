package radiotherapysuite;

/**
*
* @author Mickey Cyberkid
*/

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javax.swing.JOptionPane;

import static radiotherapysuite.CalculationMethods.EquivalentFieldSize;
import static radiotherapysuite.CalculationMethods.ReducedFieldSize;
import static radiotherapysuite.CalculationMethods.calculate_dmax;
import static radiotherapysuite.CalculationMethods.exists;
import static radiotherapysuite.CalculationMethods.getDecayFactor;
import static radiotherapysuite.CalculationMethods.getIndexOf;
import static radiotherapysuite.CalculationMethods.getMinMax;
import static radiotherapysuite.CalculationMethods.lookupValue;
import static radiotherapysuite.CalculationMethods.calculate_time;
import static radiotherapysuite.CalculationMethods.scatterValue;
import static radiotherapysuite.CalculationMethods.scpValue;
import static radiotherapysuite.CalculationMethods.toDP;
/**
 * FXML Controller class
 *
 * @author OverComer
 */
public class LoadPageController implements Initializable {
    
    // Datasets
    public  HashMap<Integer, Double> decay_factors;
    public  HashMap<String, double[]> TMR_SHEET;
    public  HashMap<String, double[]> SCP_SHEET;
    public  HashMap<String, double[]> PDD_SHEET;
    public double SADBFACTOR = 1;
    public double SSDBFACTOR = 1;
    public double SADWEDGE = 1;
    public double SSDWEDGE = 1;
    @FXML
    private CheckBox sadnb_wnone1;
    private ScrollPane scrollFrame;
    private BorderPane bPane;
    @FXML
    private CheckBox sadb_trayfac1;
    @FXML
    private CheckBox sadb_belly1;
    @FXML
    private CheckBox sadb_nofac1;
            
   
   
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize stuff
        DataSheet dataSet = new DataSheet();
        TMR_SHEET = dataSet.TMR_SHEET;
        SCP_SHEET = dataSet.SCP_SHEET;
        PDD_SHEET = dataSet.PDD_SHEET;
        
        
      
        
        
        MainPane.getTabs().clear();
        
        MainPane.setMaxSize(0, 7);
        
        
        MainPane.getTabs().add(Home);
        MainPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if(newTab != null){
                fadeInContent(newTab.getContent());
            }
    });
          
    }
    
    
    public void showAlert(AlertType alertType, String title, String message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    //Animation methods
    private void fadeInContent(Node node){
            FadeTransition fade = new FadeTransition(Duration.millis(300), node);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
    }
    
    private void animate(Node node){
        MainPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if(newTab != null){
                fadeInContent(newTab.getContent());
            }
    });
    }
   
       
    @FXML
    private void qaCalculation(ActionEvent event) {
        // Quality Assurance Calculations
        try{
            final double PR = 101.32;
            final double TR = 20;
            final double CR = 12.32;

            // getting user inputs
            // temperature
            double temp1 = Double.valueOf(T1.getText().toString());
            double temp2 = Double.valueOf(T2.getText().toString());
            // pressure
            double pressure1 = Double.valueOf(P1.getText().toString());
            double pressure2 = Double.valueOf(P2.getText().toString());
            // neg3 values
            double Neg31 = Double.valueOf(neg31.getText().toString());
            double Neg32 = Double.valueOf(neg32.getText().toString());
            // Zero values
            double Zero_1 = Double.valueOf(zero1.getText().toString());
            double Zero_2 = Double.valueOf(zero2.getText().toString());
            //pos3 values
            double Pos3_1 = Double.valueOf(pos31.getText().toString());
            double Pos3_2 = Double.valueOf(pos32.getText().toString());



            // calculations
            double Temp_Avg = (temp1 + temp2)/2;
            double Pres_Avg = (pressure1 + pressure2)/2;
            double Neg3_Avg = (Neg31 + Neg32)/2;
            double Zero_Avg = (Zero_1 + Zero_2)/2;
            double Pos3_Avg = (Pos3_1 + Pos3_2)/2;
            double calc1 = (273.2 + Temp_Avg)/(273.2 + TR);
            double calc2 = (PR/Pres_Avg);
            double calc3 = calc1 * calc2;
            double C3 = (calc3*Pos3_Avg);
            double Co = (calc3*Zero_Avg);
            double C_o = Double.parseDouble(String.format("%.2f", Co));
            double CNeg = (calc3*Neg3_Avg);
            double PE = ((CR - C_o)/CR)*100;

            // Final answers

            c3.setText(String.format("%.2f", C3));
            c0.setText(String.format("%.2f", Co));
            negC.setText(String.format("%.2f", CNeg));
            ERROR.setText(String.format("%.2f", PE)+"%");
        }catch(Exception e){
            e.printStackTrace();
            showAlert(AlertType.ERROR,"Input Error","An error ocurred!\n Make sure all inputs are numerical values and not null.");
            
        }

    }

    // Method for calculating treatment time SAD Open field
    @FXML
    private void sadOpenFieldCalculation(ActionEvent event) {
        try{
            
            // Get values
            double X = Double.parseDouble(sadnb_x.getText());
            double Y = Double.parseDouble(sadnb_y.getText());
            double dose = Double.parseDouble(sadnb_dose.getText());
            // TODO :  Check if depth is not a decimal value
            String sadnbDepth = sadnb_depth.getText();
            
            // Calculate equivalent field size
            System.out.println("Calculating Field Size ...");
            double equivalent_field_size = EquivalentFieldSize(X,Y);
            System.out.println("Eqfs : "+ equivalent_field_size );
            
            // GET SCP
            System.out.println("Calculating SCP ...");
            double sadnbSCP = scpValue(equivalent_field_size, SCP_SHEET);
            System.out.println("Scp value is : "+sadnbSCP);
        
            // TODO : GET TMR
            System.out.println("Calculating TMR ...");
            double sadnbTMR;
            sadnbTMR = Double.parseDouble(toDP(lookupValue(equivalent_field_size, sadnbDepth, TMR_SHEET),"4"));
         
            // TODO: Calculate DMAX
            System.out.println("Calculating DMAX ...");
            double sadnbDmax = Double.parseDouble(toDP(calculate_dmax((float)dose, (float)sadnbTMR),"4"));
        
            // TODO : Calculate TIME
            System.out.println("Calculating TIME ...");
            double sadnbTime = calculate_time(sadnbDmax, sadnbSCP);
            
        
            // TODO: display results
            sadnb_eqfs.setText(toDP(equivalent_field_size,"1"));
            sadnb_scp.setText(toDP(sadnbSCP,"4"));
            sadnb_tmr.setText(String.valueOf(sadnbTMR));
            sadnb_dmax.setText(toDP(sadnbDmax, "4"));
            sadnb_time.setText(toDP(sadnbTime,"2"));
            
            
            
       }catch(Exception e){
           e.printStackTrace();
           showAlert(AlertType.ERROR,"Input Error","An error ocurred!\n Make sure all inputs are numerical values and not null.");

       }
        
    }

    // Method for calculating treatment time SAD blocked field
    @FXML
    private void sadBlockCalculation(ActionEvent event) {
        try{
            
            // TODO: Work on input validations for all methods
            if(!sadb_trayfac.isSelected() & !sadb_belly.isSelected()  & !sadb_nofac.isSelected()){
                JOptionPane.showMessageDialog(null, "No Factor Selected !");
                return;
            }
            
            
            // Get values
            double X = Double.parseDouble(sadb_x.getText());
            double Y = Double.parseDouble(sadb_y.getText());
            double dose = Double.parseDouble(sadb_dose.getText());
            double area = Double.parseDouble(sadb_area.getText());
            
            // TODO :  Check if depth is not a decimal value
            String sadbDepth = sadb_depth.getText();
            
            // Calculate equivalent field size
            System.out.println("Calculating Field Sizes ...");
            double equivalent_field_size = EquivalentFieldSize(X,Y);
            double reducedfield = ReducedFieldSize(X,Y,area);
            
            System.out.println("Eqfs : "+ equivalent_field_size );
            System.out.println("Reduced Filed : "+ equivalent_field_size );
            
            // GET SCP
            //get sc and sp and multiply
            System.out.println("Calculating SCP ...");
            double sadbSCP = 0;
            // Scatter from collimator
            double sadbSC = scatterValue(equivalent_field_size, SCP_SHEET, "sc");
            double sadbSP = scatterValue(equivalent_field_size, SCP_SHEET, "sp");
            //Scatter from patient
            System.out.println("Sc value is : "+sadbSC);
            System.out.println("Sc value is : "+sadbSP);
            // Calculate SCP
            sadbSCP = sadbSC*sadbSP; 
            
            // TODO : GET TMR
            System.out.println("Calculating TMR ...");
            double sadbTMR;
            sadbTMR = Double.parseDouble(toDP(lookupValue(reducedfield, sadbDepth, TMR_SHEET),"4"));
         
            // TODO: Calculate DMAX
            System.out.println("Calculating DMAX ...");
            double sadbDmax = Double.parseDouble(toDP(calculate_dmax((float)dose, (float)sadbTMR),"4"));
        
            // TODO : Calculate TIME
            System.out.println("Calculating TIME ...");
            
            double sadbTime = calculate_time(sadbDmax, (sadbSCP * SADBFACTOR));
            
        
            // TODO: display results
            sadb_eqfs.setText(toDP(equivalent_field_size,"1"));
            sadb_reduced.setText(toDP(reducedfield,"1"));
            sadb_scp.setText(toDP(sadbSCP,"4"));
            sadb_tmr.setText(String.valueOf(sadbTMR));
            sadb_dmax.setText(toDP(sadbDmax, "4"));
            sadb_time.setText(toDP(sadbTime,"2"));
            
            
            
       }catch(Exception e){
           e.printStackTrace();
           showAlert(AlertType.ERROR,"Input Error","An error ocurred!\n Make sure all inputs are numerical values and not null.");

       }
    }

    // Method for calculating treatment time SSD Blocked field
    @FXML
    private void ssdBlockCalculation(ActionEvent event) {
        
        try{
            
            // TODO: Work on input validations for all methods
            if(!ssdb_trayfac.isSelected() & !ssdb_belly.isSelected()  & !ssdb_nofac.isSelected()){
                showAlert(AlertType.INFORMATION,"Select Block Factors","No Factor Selected !");
                return;
            }
            
            
            // Get values
            double X = Double.parseDouble(ssdb_x.getText());
            double Y = Double.parseDouble(ssdb_y.getText());
            double dose = Double.parseDouble(ssdb_dose.getText());
            double area = Double.parseDouble(ssdb_area.getText());
            
            // TODO :  Check if depth is not a decimal value
            String sadbDepth = ssdb_depth.getText();
            
            // Calculate equivalent field size
            System.out.println("Calculating Field Sizes ...");
            double equivalent_field_size = EquivalentFieldSize(X,Y);
            double reducedfield = ReducedFieldSize(X,Y,area);
            
            System.out.println("Eqfs : "+ equivalent_field_size );
            System.out.println("Reduced Filed : "+ equivalent_field_size );
            
            // GET SCP
            //get sc and sp and multiply
            System.out.println("Calculating SCP ...");
            double ssdbSCP = 0;
            // Scatter from collimator
            double ssdbSC = scatterValue(equivalent_field_size, SCP_SHEET, "sc");
            double ssdbSP = scatterValue(reducedfield, SCP_SHEET, "sp");
            //Scatter from patient
            System.out.println("Sc value is : "+ssdbSC);
            System.out.println("Sc value is : "+ssdbSP);
            // Calculate SCP
            ssdbSCP = ssdbSC*ssdbSP; 
            System.out.println("SCP : "+ssdbSCP);
            
            // TODO : GET TMR
            System.out.println("Calculating TMR ...");
            double ssdbPDD;
            ssdbPDD = Double.parseDouble(toDP(lookupValue(reducedfield, sadbDepth, PDD_SHEET),"4"));
         
            // TODO: Calculate DMAX
            
            double sadbDmax = Double.parseDouble(toDP(calculate_dmax((float)dose, (float)ssdbPDD),"4")) * 100;
            System.out.println("Calculated DMAX :"+ sadbDmax);
        
            // TODO : Calculate TIME
            double sadbTime = calculate_time(sadbDmax, (ssdbSCP * SSDBFACTOR));
            System.out.println("Calculated TIME :" + sadbTime);
            
            
            
        
            // TODO: display results
            ssdb_eqfs.setText(String.valueOf(toDP(equivalent_field_size,"1")));
            ssdb_reduced.setText(String.valueOf(toDP(reducedfield,"1")));
            ssdb_scp.setText(String.valueOf(toDP(ssdbSCP, "4")));
            ssdb_pdd.setText(String.valueOf(toDP(ssdbPDD, "4")));
            ssdb_dmax.setText(String.valueOf(toDP(sadbDmax, "4")));
            ssdb_time.setText(String.valueOf(toDP(sadbTime,"2")));
            
            
            
       }catch(Exception e){
           e.printStackTrace();
           showAlert(AlertType.ERROR,"Input Error","An error ocurred!\n Make sure all inputs are numerical values and not null.");

       }
    }

    

    // Method for calculating treatment time SSD Open field
    @FXML
    private void ssdOpenFieldCalculation(ActionEvent event) {
         try{
            
            // Get values
            double X = Double.parseDouble(ssdnb_x.getText());
            double Y = Double.parseDouble(ssdnb_y.getText());
            double dose = Double.parseDouble(ssdnb_dose.getText());
            // TODO :  Check if depth is not a decimal value
            String ssdnbDepth = ssdnb_depth.getText();
            System.out.println("Depth : "+ ssdnbDepth);
            // Calculate equivalent field size
            System.out.println("Calculating Field Size ...");
            double equivalent_field_size = EquivalentFieldSize(X,Y);
            System.out.println("Eqfs : "+ equivalent_field_size );
            
            // GET SCP
            System.out.println("Calculating SCP ...");
            double ssdnbSCP = scpValue(equivalent_field_size, SCP_SHEET);
            System.out.println("Scp value is : "+ssdnbSCP);
        
            // TODO : GET PDD
            double ssdnbPDD;
            double pdd = lookupValue(equivalent_field_size, ssdnbDepth, PDD_SHEET);
            ssdnbPDD = Double.parseDouble(toDP(pdd,"4"));
            
            System.out.println("Calculated PDD : "+ssdnbPDD);
         
            // TODO: Calculate DMAX
            double ssdnbDmax = Double.parseDouble(toDP(calculate_dmax((float)dose, (float)ssdnbPDD),"4")) * 100;
            System.out.println("Calculated DMAX : "+ ssdnbDmax);
        
            // TODO : Calculate TIME
            double ssdnbTime = calculate_time(ssdnbDmax, ssdnbSCP);
            System.out.println("Calculated TIME : "+ ssdnbTime);
            
        
            // TODO: display results
            ssdnb_eqfs.setText(String.valueOf(toDP(equivalent_field_size,"1")));
            ssdnb_scp.setText(String.valueOf(ssdnbSCP));
            ssdnb_pdd.setText(String.valueOf(ssdnbPDD));
            ssdnb_dmax.setText(String.valueOf(ssdnbDmax));
            ssdnb_time.setText(toDP(ssdnbTime, "2"));
            
            
            
       }catch(Exception e){
           e.printStackTrace();
           showAlert(AlertType.ERROR,"Input Error","An error ocurred!\n Make sure all inputs are numerical values and not null.");

       }
        
    }

    @FXML
    private void sadOpenWedgeFieldCalculation(ActionEvent event) {
        try{
            
            if(!sadnb_wnone1.isSelected() & !sadnb_w301.isSelected()  & 
                    !sadnb_w151.isSelected() & !sadnb_w601.isSelected() & !sadnb_w451.isSelected()){
                JOptionPane.showMessageDialog(null, "No Factor Selected !");
                return;
            }
            // Get values
            double X = Double.parseDouble(sadnbw_x.getText());
            double Y = Double.parseDouble(sadnbw_y.getText());
            double dose = Double.parseDouble(sadnbw_dose.getText());
            // TODO :  Check if depth is not a decimal value
            String sadnbDepth = sadnbw_depth.getText();
            
            // Calculate equivalent field size
            System.out.println("Calculating Field Size ...");
            double equivalent_field_size = EquivalentFieldSize(X,Y);
            System.out.println("Eqfs : "+ equivalent_field_size );
            
            // GET SCP
            System.out.println("Calculating SCP ...");
            double sadnbwSCP = scpValue(equivalent_field_size, SCP_SHEET);
            System.out.println("Scp value is : "+sadnbwSCP);
        
            // TODO : GET TMR
            System.out.println("Calculating TMR ...");
            double sadnbwTMR;
            sadnbwTMR = Double.parseDouble(toDP(lookupValue(equivalent_field_size, sadnbDepth, TMR_SHEET),"4"));
         
            // TODO: Calculate DMAX
            System.out.println("Calculating DMAX ...");
            double sadnbwDmax = Double.parseDouble(toDP(calculate_dmax((float)dose, (float)sadnbwTMR),"4"));
        
            // TODO : Calculate TIME
            System.out.println("Calculating TIME ...");
            double sadnbwTime = calculate_time(sadnbwDmax, (sadnbwSCP * SADWEDGE));
            
        
            // TODO: display results
            sadnbw_eqfs.setText(String.valueOf(toDP(equivalent_field_size,"1")));
            sadnbw_scp.setText(String.valueOf(sadnbwSCP));
            sadnbw_tmr.setText(String.valueOf(sadnbwTMR));
            sadnbw_dmax.setText(String.valueOf(sadnbwDmax));
            sadnbw_time.setText(toDP(sadnbwTime,"2"));
            
            
            
       }catch(Exception e){
           e.printStackTrace();
           showAlert(AlertType.ERROR,"Input Error","An error ocurred!\n Make sure all inputs are numerical values and not null.");
       }
    }
    
    @FXML
    private void clearLinac(ActionEvent event) {
        T1.clear();
        T2.clear();
        P1.clear();
        P2.clear();
        c0.clear();
        c3.clear();
        negC.clear();
        neg31.clear();
        neg32.clear();
        pos31.clear();
        pos32.clear();
        ERROR.clear();
        zero1.clear();
        zero2.clear();
    }

    @FXML
    private void clearSADNB(ActionEvent event) {
        sadnb_x.clear();
        sadnb_y.clear();
        sadnb_eqfs.clear();
        sadnb_scp.clear();
        sadnb_depth.clear();
        sadnb_dmax.clear();
        sadnb_dose.clear();
        sadnb_tmr.clear();
        sadnb_time.clear();
    }

    @FXML
    private void clearSADBlock(ActionEvent event) {
        sadb_area.clear();
        sadb_reduced.clear();
        sadb_belly.setSelected(false);
        sadb_nofac.setSelected(false);
        sadb_trayfac.setSelected(false);
        SADBFACTOR = 1;
        sadb_y.clear();
        sadb_x.clear();
        sadb_eqfs.clear();
        sadb_scp.clear();
        sadb_depth.clear();
        sadb_dmax.clear();
        sadb_dose.clear();
        sadb_tmr.clear();
        sadb_time.clear();
    }

    @FXML
    private void clearSSDNB(ActionEvent event) {
        ssdnb_x.clear();
        ssdnb_y.clear();
        ssdnb_eqfs.clear();
        ssdnb_scp.clear();
        ssdnb_depth.clear();
        ssdnb_dmax.clear();
        ssdnb_dose.clear();
        ssdnb_pdd.clear();
        ssdnb_time.clear();
    }

    @FXML
    private void clearSSDB(ActionEvent event) {
        ssdb_area.clear();
        ssdb_reduced.clear();
        ssdb_belly.setSelected(false);
        ssdb_nofac.setSelected(false);
        ssdb_trayfac.setSelected(false);
        SSDBFACTOR = 1;
        ssdb_y.clear();
        ssdb_x.clear();
        ssdb_eqfs.clear();
        ssdb_scp.clear();
        ssdb_depth.clear();
        ssdb_dmax.clear();
        ssdb_dose.clear();
        ssdb_pdd.clear();
        ssdb_time.clear();
    }

    @FXML
    private void clearSADNNBW(ActionEvent event) {
        sadnbw_y.clear();
        sadnbw_x.clear();
        sadnbw_eqfs.clear();
        sadnbw_scp.clear();
        sadnbw_depth.clear();
        sadnbw_dmax.clear();
        sadnbw_dose.clear();
        sadnbw_tmr.clear();
        sadnbw_time.clear();
    }


    @FXML
    private void addLinac(ActionEvent event) {
        if(MainPane.getTabs().contains(qaTab) == false){
            MainPane.getTabs().add(qaTab);
            qaTab.getContent().setFocusTraversable(true);
        }else{
            MainPane.getSelectionModel().select(qaTab);
        }
    }

    @FXML
    private void addSADNB(ActionEvent event) {
        if(MainPane.getTabs().contains(SADNB) == false){
            MainPane.getTabs().add(SADNB);
            SADNB.getContent().setFocusTraversable(true);
        }else{
            MainPane.getSelectionModel().select(SADNB);
            
        }
    }

    @FXML
    private void addSADB(ActionEvent event) {
        if(MainPane.getTabs().contains(SADB) == false){
            MainPane.getTabs().add(SADB);
            SADB.getContent().setFocusTraversable(true);
        }else{
            MainPane.getSelectionModel().select(SADB);
        }
    }

    @FXML
    private void addSSDNB(ActionEvent event) {
        if(MainPane.getTabs().contains(SSDNB) == false){
            MainPane.getTabs().add(SSDNB);
            SSDNB.getContent().setFocusTraversable(true);
        }else{
            MainPane.getSelectionModel().select(SSDNB);
        }
    }

    @FXML
    private void addSSDB(ActionEvent event) {
        if(MainPane.getTabs().contains(SSDB) == false){
            MainPane.getTabs().add(SSDB);
            SSDB.getContent().setFocusTraversable(true);
        }else{
            MainPane.getSelectionModel().select(SSDB);
        }
    }

    @FXML
    private void addSADNBW(ActionEvent event) {
        if(MainPane.getTabs().contains(SADNBW) == false){
            MainPane.getTabs().add(SADNBW);
            SADNBW.getContent().setFocusTraversable(true);
        }else{
            MainPane.getSelectionModel().select(SADNBW);
        }
    }
    
    @FXML
    private void setSadbFactor(ActionEvent event) {
         
        if(sadb_trayfac.isSelected()){
                SADBFACTOR = 0.957;
                System.out.println("SAD Factor :"+SADBFACTOR);
                sadb_nofac.setSelected(false);
                sadb_belly.setSelected(false);
        }
        
        if(sadb_belly.isSelected()){
                SADBFACTOR = 0.978;
                System.out.println("SAD Factor :"+SADBFACTOR);
                sadb_nofac.setSelected(false);
                sadb_trayfac.setSelected(false);
        }
        
        if(sadb_nofac.isSelected()){
                SADBFACTOR = 1;
                System.out.println("SAD Factor :"+SADBFACTOR);
                sadb_belly.setSelected(false);
                sadb_trayfac.setSelected(false);
        }
    }
    
    @FXML
    private void setSsdbFactor(ActionEvent event) {
        if(ssdb_trayfac.isSelected()){
                SSDBFACTOR = 0.957;
                System.out.println("SSD Factor :"+SSDBFACTOR);
                ssdb_trayfac.setSelected(true);
                ssdb_nofac.setSelected(false);
                ssdb_belly.setSelected(false);
        }
        
        if(ssdb_belly.isSelected()){
                SSDBFACTOR = 0.978;
                System.out.println("SSD Factor :"+SSDBFACTOR);
                ssdb_belly.setSelected(true);
                ssdb_nofac.setSelected(false);
                ssdb_trayfac.setSelected(false);
        }
        
        if(ssdb_nofac.isSelected()){
                SSDBFACTOR = 1;
                System.out.println("SSD Factor :"+SSDBFACTOR);
                ssdb_nofac.setSelected(true);
                ssdb_belly.setSelected(false);
                ssdb_trayfac.setSelected(false);
        }
    }
    
    
    
    @FXML
    private void setSADWedgeValue(ActionEvent event) {
        if(sadnb_w151.isSelected()){
                SADWEDGE = 0.767;
                sadnb_w151.setSelected(true);
                sadnb_w301.setSelected(false);
                sadnb_w601.setSelected(false);
                sadnb_wnone1.setSelected(false);
                sadnb_w451.setSelected(false);
        }
        
        if(sadnb_w301.isSelected()){
                SADWEDGE = 0.636;
                sadnb_w301.setSelected(true);
                sadnb_w601.setSelected(false);
                sadnb_w151.setSelected(false);
                sadnb_wnone1.setSelected(false);
                sadnb_w451.setSelected(false);
        }
        
        if(sadnb_w451.isSelected()){
                SADWEDGE = 0.487;
                sadnb_w451.setSelected(true);
                sadnb_w301.setSelected(false);
                sadnb_w151.setSelected(false);
                sadnb_wnone1.setSelected(false);
                sadnb_w601.setSelected(false);
        }
        if(sadnb_w601.isSelected()){
                SADWEDGE = 0.261;
                sadnb_w601.setSelected(true);
                sadnb_w301.setSelected(false);
                sadnb_w151.setSelected(false);
                sadnb_wnone1.setSelected(false);
                sadnb_w451.setSelected(false);
        }
        if(sadnb_wnone1.isSelected()){
                SADWEDGE = 1;
                sadnb_wnone1.setSelected(true);
                sadnb_w301.setSelected(false);
                sadnb_w151.setSelected(false);
                sadnb_w601.setSelected(false);
                sadnb_w451.setSelected(false);
        }
    }
    
    //VARIABLES
    
    // Main Pane
    @FXML
    private TabPane MainPane;

    // HOME TAB
    @FXML
    private Tab Home;

    // QA TAB
    @FXML
    private Tab qaTab;
    @FXML
    private TextField T1;
    @FXML
    private TextField T2;
    @FXML
    private TextField P1;
    @FXML
    private TextField P2;
    @FXML
    private TextField neg31;
    @FXML
    private TextField neg32;
    @FXML
    private TextField zero1;
    @FXML
    private TextField zero2;
    @FXML
    private TextField pos31;
    @FXML
    private TextField pos32;
    @FXML
    private TextField negC;
    @FXML
    private TextField c0;
    @FXML
    private TextField c3;
    @FXML
    private TextField ERROR;

    // SAD NO BLOCK TAB
    @FXML
    private Tab SADNB; 
    @FXML
    private TextField sadnb_x;
    @FXML
    private TextField sadnb_y;
    @FXML
    private TextField sadnb_depth;
    @FXML
    private TextField sadnb_dose;
    @FXML
    private TextField sadnb_eqfs;
    @FXML
    private TextField sadnb_scp;
    @FXML
    private TextField sadnb_tmr;
    @FXML
    private TextField sadnb_dmax;
    @FXML
    private TextField sadnb_time;
    
    // SAD BLOCK TAB
    @FXML
    private Tab SADB;
    
    @FXML
    private TextField sadb_x;
    @FXML
    private TextField sadb_y;
    @FXML
    private TextField sadb_depth;
    @FXML
    private TextField sadb_dose;
    @FXML
    private TextField sadb_eqfs;
    @FXML
    private TextField sadb_scp;
    @FXML
    private TextField sadb_tmr;
    @FXML
    private TextField sadb_dmax;
    @FXML
    private TextField sadb_time;
    @FXML
    private TextField sadb_reduced;
    @FXML
    private TextField sadb_area;
    @FXML
    private CheckBox sadb_trayfac;
    @FXML
    private CheckBox sadb_belly;
    @FXML
    private CheckBox sadb_nofac;
    
    // SSD BLOCK TAB
    @FXML
    private Tab SSDB;
    @FXML
    private TextField ssdb_x;
    @FXML
    private TextField ssdb_y;
    @FXML
    private TextField ssdb_depth;
    @FXML
    private TextField ssdb_dose;
    @FXML
    private TextField ssdb_eqfs;
    @FXML
    private TextField ssdb_scp;
    @FXML
    private TextField ssdb_pdd;
    @FXML
    private TextField ssdb_dmax;
    @FXML
    private TextField ssdb_time;
    @FXML
    private TextField ssdb_reduced;
    @FXML
    private TextField ssdb_area;
    @FXML
    private CheckBox ssdb_trayfac;
    @FXML
    private CheckBox ssdb_belly;
    @FXML
    private CheckBox ssdb_nofac;
    
    // SSD NO BLOCK TAB
    @FXML
    private Tab SSDNB;
    @FXML
    private TextField ssdnb_x;
    @FXML
    private TextField ssdnb_y;
    @FXML
    private TextField ssdnb_depth;
    @FXML
    private TextField ssdnb_dose;
    @FXML
    private TextField ssdnb_eqfs;
    @FXML
    private TextField ssdnb_scp;
    @FXML
    private TextField ssdnb_pdd;
    @FXML
    private TextField ssdnb_dmax;
    @FXML
    private TextField ssdnb_time;
    
    // SAD NO BLOCK WEDGE
    @FXML
    private Tab SADNBW;
    @FXML
    private TextField sadnbw_x;
    @FXML
    private TextField sadnbw_y;
    @FXML
    private TextField sadnbw_depth;
    @FXML
    private TextField sadnbw_dose;
    @FXML
    private TextField sadnbw_eqfs;
    @FXML
    private TextField sadnbw_scp;
    @FXML
    private TextField sadnbw_tmr;
    @FXML
    private TextField sadnbw_dmax;
    @FXML
    private TextField sadnbw_time;
    private CheckBox ssdnb_wnone1;
    
    private CheckBox ssdnb_w3011;
    private CheckBox ssdnb_w1511;
    private CheckBox ssdnb_w4511;
    private CheckBox ssdnb_w6011;
    @FXML
    private CheckBox sadnb_w301;
    @FXML
    private CheckBox sadnb_w151;
    @FXML
    private CheckBox sadnb_w451;
    @FXML
    private CheckBox sadnb_w601;

    
    
    
}
