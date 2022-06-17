/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Model;
import it.polito.tdp.yelp.model.Review;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnMiglioramento"
    private Button btnMiglioramento; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCitta"
    private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

    @FXML // fx:id="cmbLocale"
    private ComboBox<Business> cmbLocale; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader
    
    @FXML
    void doRiempiLocali(ActionEvent event) {
    	this.cmbLocale.getItems().clear();
    	String citta = this.cmbCitta.getValue();
    	if(citta != null) {
    		List<Business> businesses = this.model.getBusinesses(citta);
    		Collections.sort(businesses);
    		
    		cmbLocale.getItems().addAll(businesses);
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	Business b = cmbLocale.getValue();
    	if(b == null) {
    		txtResult.setText("Seleziona un locale!");
    	} else {
    		List<Review> result = this.model.creaGrafo(b);
    		String graph = this.model.getGraphParameters();
    		int maxArchi = this.model.getMaxArchi();
    		
    		txtResult.setText(graph);
    		for(Review r: result) {
    			txtResult.appendText(r.getReviewId()+"     # ARCHI USCENTI: "+maxArchi+"\n");
    		}
    	}
    }

    @FXML
    void doTrovaMiglioramento(ActionEvent event) {
    	List<Review> result = this.model.trovaMiglioramento();
    	double daysApart = this.model.getDaysApart();
    	
    	txtResult.setText("Più lunga sequenza di recensioni a distanza di "+daysApart+" giorni:\n");
    	for(Review r: result) {
			txtResult.appendText(r+"\n");
		}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnMiglioramento != null : "fx:id=\"btnMiglioramento\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbLocale != null : "fx:id=\"cmbLocale\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	List<String> cities = this.model.getAllCities();
    	Collections.sort(cities);
    	
    	cmbCitta.getItems().clear();
    	cmbCitta.getItems().addAll(cities);
    }
}
