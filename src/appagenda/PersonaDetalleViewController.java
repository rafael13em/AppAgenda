/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

import entidades.Persona;
import entidades.Provincia;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.RollbackException;

/**
 * FXML Controller class
 *
 * @author rafae
 */
public class PersonaDetalleViewController implements Initializable {

    private Pane rootAgendaView;
    private TableView tableViewPrevio;
    private Persona persona;
    private EntityManager entityManager;
    private boolean nuevaPersona;
    public static final char CASADO='C';
    public static final char SOLTERO='S';
    public static final char VIUDO='V';
    public static final String CARPETA_FOTOS="src/appagenda/Fotos";

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;
    @FXML
    private DatePicker datePickerFnac;
    @FXML
    private RadioButton rdBtnSoltero;
    @FXML
    private RadioButton rdButtonCasado;
    @FXML
    private RadioButton rdButtonViudo;
    @FXML
    private TextField txtSalario;
    @FXML
    private CheckBox checkBoxJubilacion;
    @FXML
    private Button btnFoto;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private AnchorPane rootPersonaDetalleView;
    @FXML
    private TextField txtNumHij;
    @FXML
    private ComboBox<Provincia> cbBoxProvincia;
    @FXML
    private ImageView imageViewFoto;
    @FXML
    private Button btnSuprimir;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);
        rootAgendaView.setVisible(true);
        
        boolean errorFormato = false;

        persona.setNombre(txtNombre.getText());
        persona.setApellidos(txtApellidos.getText());
        persona.setTelefono(txtTelefono.getText());
        persona.setEmail(txtEmail.getText());
        
        if (persona.getNumHijos() != null){
            txtNumHij.setText(persona.getNumHijos().toString());
        }
        if (!txtNumHij.getText().isEmpty()){
            try {
            persona.setNumHijos(Short.valueOf(txtNumHij.getText()));
            } catch(NumberFormatException ex){
                errorFormato = true;
                Alert alert = new Alert(AlertType.INFORMATION, "Número de hijos no válido");
                alert.showAndWait();
                txtNumHij.requestFocus();
            }
        }
        
        if (!txtSalario.getText().isEmpty()){
            try {
            persona.setSalario(BigDecimal.valueOf(Double.valueOf(txtSalario.getText()).doubleValue()));
            } catch(NumberFormatException ex) {
            errorFormato = true;
            Alert alert = new Alert(AlertType.INFORMATION, "Salario no válido");
            alert.showAndWait();
            txtSalario.requestFocus();
            }
        }
        
        persona.setJubilado(checkBoxJubilacion.isSelected());
        
        if (rdButtonCasado.isSelected()){
            persona.setEstadoCivil(CASADO);
        } 
        else if (rdBtnSoltero.isSelected()){
            persona.setEstadoCivil(SOLTERO);
        } 
        else if (rdButtonViudo.isSelected()){
            persona.setEstadoCivil(VIUDO);
        }
        
        if (datePickerFnac.getValue() != null){
            LocalDate localDate = datePickerFnac.getValue();
            ZonedDateTime zonedDateTime =
            localDate.atStartOfDay(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            Date date = Date.from(instant);
            persona.setFechaNacimiento(date);
        } 
        else {
            persona.setFechaNacimiento(null);
        }
        
        if (cbBoxProvincia.getValue() != null){
            persona.setProvincia(cbBoxProvincia.getValue());
        } 
        else {
            Alert alert = new Alert(AlertType.INFORMATION,"Debe indicar una provincia");
            alert.showAndWait();
            errorFormato = true;
        }
        
        
        
        if (!errorFormato) { // Los datos introducidos son correctos
            try {
                if (nuevaPersona)
                    entityManager.persist(persona);
                else
                    entityManager.merge(persona);

                entityManager.getTransaction().commit();

                int numFilaSeleccionada;
                if (nuevaPersona){
                    tableViewPrevio.getItems().add(persona);
                    numFilaSeleccionada = tableViewPrevio.getItems().size()- 1;
                    tableViewPrevio.getSelectionModel().select(numFilaSeleccionada);
                    tableViewPrevio.scrollTo(numFilaSeleccionada);
                } 
                else{
                    numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex();
                    tableViewPrevio.getItems().set(numFilaSeleccionada,persona);
                }
                TablePosition pos = new TablePosition(tableViewPrevio,
                numFilaSeleccionada,null);
                tableViewPrevio.getFocusModel().focus(pos);
                tableViewPrevio.requestFocus();
            } catch (RollbackException ex) { // Los datos introducidos no cumplen requisitos de BD
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText("No se han podido guardar los cambios. " + "Compruebe que los datos cumplen los requisitos");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }
        }    
    }

    @FXML
    private void onActionButtonCancelar(ActionEvent event) {
        StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);
        rootAgendaView.setVisible(true);

        entityManager.getTransaction().rollback();
        int numFilaSeleccionada =
        tableViewPrevio.getSelectionModel().getSelectedIndex();
        TablePosition pos = new TablePosition(tableViewPrevio,
        numFilaSeleccionada,null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();
    }
    
    public void setRootAgendaView(Pane rootAgendaView){
        this.rootAgendaView = rootAgendaView;
    }
    
    public void setTableViewPrevio(TableView tableViewPrevio){
        this.tableViewPrevio=tableViewPrevio;
    }
    
    public void setPersona(EntityManager entityManager, Persona persona, Boolean nuevaPersona){
        this.entityManager = entityManager;
        entityManager.getTransaction().begin();
        if (!nuevaPersona)
            this.persona=entityManager.find(Persona.class,persona.getId());
        else
            this.persona=persona;
        
        this.nuevaPersona=nuevaPersona;
    }
    
    public void mostrarDatos(){
        txtNombre.setText(persona.getNombre());
        txtApellidos.setText(persona.getApellidos());
        txtTelefono.setText(persona.getTelefono());
        txtEmail.setText(persona.getEmail());
        if (persona.getEstadoCivil() != null){
            switch(persona.getEstadoCivil()){
                case CASADO:
                    rdButtonCasado.setSelected(true);
            break;
            case SOLTERO:
                rdBtnSoltero.setSelected(true);
            break;
            case VIUDO:
                rdButtonViudo.setSelected(true);
            break;
            }
        }
        
        if (persona.getSalario() != null){
            txtSalario.setText(persona.getSalario().toString());
        }
        
        
        if (persona.getJubilado() != null){
            checkBoxJubilacion.setSelected(persona.getJubilado());
        }
        

        if (persona.getFechaNacimiento() != null){
            Date date=persona.getFechaNacimiento();
            Instant instant=date.toInstant();
            ZonedDateTime zdt=instant.atZone(ZoneId.systemDefault());
            LocalDate localDate=zdt.toLocalDate();
            datePickerFnac.setValue(localDate);
        }
        
        Query queryProvinciaFindAll = entityManager.createNamedQuery("Provincia.findAll");
        List listProvincia = queryProvinciaFindAll.getResultList();
        cbBoxProvincia.setItems(FXCollections.observableList(listProvincia));
        
        if (persona.getProvincia() != null){
            cbBoxProvincia.setValue(persona.getProvincia());
        }
        
        cbBoxProvincia.setConverter(new StringConverter<Provincia>(){
            public String toString(Provincia provincia){
                if (provincia == null)
                    return null;
                else
                    return provincia.getCodigo()+"-"+provincia.getNombre();
            }
            public Provincia fromString(String userId){
                return null;
            }
        });
        
        if (persona.getFoto() != null){
            String imageFileName=persona.getFoto();
            File file = new File(CARPETA_FOTOS+"/"+imageFileName);
            if (file.exists())
            {
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } 
            else
            {
                Alert alert=new Alert(AlertType.INFORMATION,"No se encuentra la imagen en "+file.toURI().toString());
                alert.showAndWait();
            }
        }  
    }

    @FXML
    private void onActionButtonExaminar(ActionEvent event) {
        File carpetaFotos = new File(CARPETA_FOTOS);
        if (!carpetaFotos.exists()){
            carpetaFotos.mkdir();
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.jpg","*.png"),
        new FileChooser.ExtensionFilter("Todos los archivos","*.*"));
        File file = fileChooser.showOpenDialog(rootPersonaDetalleView.getScene().getWindow());
        if (file != null){
            try {
                Files.copy(file.toPath(),new File(CARPETA_FOTOS+
                "/"+file.getName()).toPath());
                persona.setFoto(file.getName());
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } catch (FileAlreadyExistsException ex){
                Alert alert = new Alert(AlertType.WARNING,"Nombre de archivo duplicado");
                alert.showAndWait();
            } catch (IOException ex){
                Alert alert = new Alert(AlertType.WARNING,"No se ha podido guardar la imagen");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onActionSuprimirFoto(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar supresión de imagen");
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen,\n"+ "quitar la foto pero MANTENER el archivo, \no CANCELAR la operación?");
        alert.setContentText("Elija la opción deseada:");
        ButtonType buttonTypeEliminar = new ButtonType("Suprimir");
        ButtonType buttonTypeMantener = new ButtonType("Mantener");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeMantener,buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeEliminar){
            String imageFileName = persona.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
            if (file.exists())
                file.delete();
       
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        } 
        else if (result.get() == buttonTypeMantener) {
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        } 
    }
}
