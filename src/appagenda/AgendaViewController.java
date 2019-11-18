package appagenda;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import entidades.Persona;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * FXML Controller class
 *
 * @author rafae
 */
public class AgendaViewController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Persona personaSeleccionada;
    private EntityManager entityManager;
    @FXML
    private TableView<Persona> tableview1;
    @FXML
    private TableColumn<Persona, String> nombre;
    @FXML
    private TableColumn<Persona, String> apellido;
    @FXML
    private TableColumn<Persona, String> email;
    @FXML
    private TableColumn<Persona, String> provincia;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnSuprimir;
    @FXML
    private AnchorPane rootAgendaView;
    
    public void setEntityManager(EntityManager entityManager){
        this.entityManager=entityManager;
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellido.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        provincia.setCellValueFactory(cellData->{
            SimpleStringProperty property=new SimpleStringProperty();
            if (cellData.getValue().getProvincia()!=null){
                property.setValue(cellData.getValue().getProvincia().getNombre());
            }
            return property;
        });
        tableview1.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)->{
            personaSeleccionada=newValue;
            if (personaSeleccionada != null){
                txtNombre.setText(personaSeleccionada.getNombre());
                txtApellido.setText(personaSeleccionada.getApellidos());
            } 
            else {
            txtNombre.setText("");
            txtApellido.setText("");
            }
        });
    }
    
    public void cargarTodasPersonas(){
        Query queryPersonaFindAll=
        entityManager.createNamedQuery("Persona.findAll");
        List<Persona> listPersona=queryPersonaFindAll.getResultList();
        tableview1.setItems(FXCollections.observableArrayList(listPersona)
    );
    }

    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        if (personaSeleccionada != null){
            personaSeleccionada.setNombre(txtNombre.getText());
            personaSeleccionada.setApellidos(txtApellido.getText());
            entityManager.getTransaction().begin();
            entityManager.merge(personaSeleccionada);
            entityManager.getTransaction().commit();
            int numFilaSeleccionada =
            tableview1.getSelectionModel().getSelectedIndex();
            tableview1.getItems().set(numFilaSeleccionada,personaSeleccionada);
            TablePosition pos = new
            TablePosition(tableview1,numFilaSeleccionada,null);
            tableview1.getFocusModel().focus(pos);
            tableview1.requestFocus();
            
        }
    }

    @FXML
    private void onActionButtonNuevo(ActionEvent event) {
        try
        {
            FXMLLoader fxmlLoader=new
            FXMLLoader(getClass().getResource("PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();
            PersonaDetalleViewController personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);
            // Ocultar la vista de la lista
            rootAgendaView.setVisible(false);

            personaDetalleViewController.setTableViewPrevio(tableview1);
            personaSeleccionada = new Persona();
            personaDetalleViewController.setPersona(entityManager,personaSeleccionada,true);
             //Añadir la vista detalle al StackPane principal para que se muestre
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);
            
            personaDetalleViewController.mostrarDatos();
        }catch (IOException ex){
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE,null,ex);
        }
    }

    @FXML
    private void onActionEditar(ActionEvent event) {
        try
        {
            FXMLLoader fxmlLoader=new
            FXMLLoader(getClass().getResource("PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();
            PersonaDetalleViewController personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);
            // Ocultar la vista de la lista
            rootAgendaView.setVisible(false);
            
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);
            
            personaDetalleViewController.setTableViewPrevio(tableview1);
            personaDetalleViewController.setPersona(entityManager,personaSeleccionada,false);
            
            personaDetalleViewController.mostrarDatos();
        }catch (IOException ex){
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE,null,ex);}
    }

    @FXML
    private void onActionButtonSuprimir(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        alert.setContentText(personaSeleccionada.getNombre() + " " + personaSeleccionada.getApellidos());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK)
        {
         // Acciones a realizar si el usuario acepta
            entityManager.getTransaction().begin();
            entityManager.merge(personaSeleccionada);
            entityManager.remove(personaSeleccionada);
            entityManager.getTransaction().commit();
            tableview1.getItems().remove(personaSeleccionada);
            tableview1.getFocusModel().focus(null);
            tableview1.requestFocus();
        } 
        else 
        {
         // Acciones a realizar si el usuario cancela
            int numFilaSeleccionada = tableview1.getSelectionModel().getSelectedIndex();
            tableview1.getItems().set(numFilaSeleccionada,personaSeleccionada);
            TablePosition pos = new TablePosition(tableview1,numFilaSeleccionada,null);
            tableview1.getFocusModel().focus(pos);
            tableview1.requestFocus();
        }

    }
}
