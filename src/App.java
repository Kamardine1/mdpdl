import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.scene.control.TableCell;

import java.sql.ResultSet;
import java.sql.SQLException;

//@SuppressWarnings("unused")
public class App extends Application{

    private ObservableList<Compte> listeComptes = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage){
        Image logo = new Image(getClass().getResourceAsStream("resources/logo.png"));
        VBox root = new VBox();
        Scene scene = new Scene(root,750,400);
        
        TableView<Compte> table = new TableView<>();

        TableColumn<Compte, String> colSite = new TableColumn<>("Site / App");
        colSite.setCellValueFactory(new PropertyValueFactory<>("siteApp"));
        table.getColumns().add(colSite);

        TableColumn<Compte, String> colIdentifiant = new TableColumn<>("Identifiant");
        colIdentifiant.setCellValueFactory(new PropertyValueFactory<>("identifiant"));
        table.getColumns().add(colIdentifiant);

        TableColumn<Compte, String> colIdentifiant2 = new TableColumn<>("Identifiant 2");
        colIdentifiant2.setCellValueFactory(new PropertyValueFactory<>("identifiant2"));
        table.getColumns().add(colIdentifiant2);

        TableColumn<Compte,Void> colPwd = new TableColumn<>("Mot de passe");
        table.getColumns().add(colPwd);

        TableColumn<Compte, String> colReponse = new TableColumn<>("Reponse");
        colReponse.setCellValueFactory(new PropertyValueFactory<>("reponse"));
        table.getColumns().add(colReponse);

        Connexion connexion = new Connexion("mybdd.db");
        connexion.connect();

        Callback <TableColumn<Compte,Void>, TableCell<Compte,Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Compte,Void> call(final TableColumn<Compte,Void> param){
                return new TableCell<>(){
                    private final Button btn_copier_dans_presse_papier = new Button("Copier !");
                    
                    {
                        btn_copier_dans_presse_papier.setOnAction(e -> connexion.copierDansPressePapier((getTableView().getItems().get(getIndex())).getPwd()));
                    }

                    @Override
                    public void updateItem(Void item, boolean empty){
                        super.updateItem(item, empty);

                        if(empty){
                            setGraphic(null);
                        }else {
                            String pwd = (getTableView().getItems().get(getIndex())).getPwd();
                            if(pwd == null || pwd.isEmpty()){
                                setGraphic(null);
                            } else {
                                setGraphic(btn_copier_dans_presse_papier);
                            }
                        }
                    }
                };
            }
        };
        colPwd.setCellFactory(cellFactory);

        table.setItems(listeComptes);
        chargerDonnees(connexion);


        Button btn_ajouter_compte = new Button("Ajouter un compte");
        btn_ajouter_compte.setOnAction(actionAjouterCompte -> {
            VBox sous_fenetre_modif_compte = new VBox();
            Scene nouvelle_Scene = new Scene(sous_fenetre_modif_compte,500,300);

            Label lb_site_app_Field = new Label("Site/App :");
            TextField tf_site_app_Field = new TextField();
            Label lb_id_Field = new Label("Identifiant :");
            TextField tf_id_Field = new TextField();
            Label lb_id2_Field = new Label("Identifiant2 :");
            TextField tf_id2_Field = new TextField();
            Label lb_pwd_Field = new Label("Mot de passe :");
            TextField tf_pwd_Field = new TextField();
            Label lb_reponse_Field = new Label("Reponse :");
            TextField tf_reponse_Field = new TextField();
            
            Label lb_confirmation = new Label("");
            
            
            Button btn_valider = new Button("Valider");
             
            btn_valider.setOnAction(actionValider -> {
                
                connexion.addCompte(new Compte(0,tf_site_app_Field.getText(),tf_id_Field.getText(),tf_id2_Field.getText(),tf_pwd_Field.getText(),tf_reponse_Field.getText()));
                
                lb_confirmation.setText(
                    "Nouveau compte crée :\nSite/App : "+tf_site_app_Field.getText()+
                    "\nIdentifiant : "+tf_id_Field.getText()+
                    "\nIdentifiant2 : "+tf_id2_Field.getText()+
                    "\nMot de passe : "+tf_pwd_Field.getText()+
                    "\nReponse : "+tf_reponse_Field.getText()
                );
            });

            
            Button btn_fermer = new Button("Fermer");

            sous_fenetre_modif_compte.getChildren().addAll(
                //lb_compteId_Field,tf_compteId_Field,
                lb_site_app_Field,tf_site_app_Field,
                lb_id_Field,tf_id_Field,
                lb_id2_Field,tf_id2_Field,
                lb_pwd_Field,tf_pwd_Field,
                lb_reponse_Field,tf_reponse_Field,
                //lb_confirmation,
                btn_valider,btn_fermer);

            Stage sous_fenetre = new Stage();
            btn_fermer.setOnAction(actionFermer -> sous_fenetre.close());
            sous_fenetre.getIcons().add(logo);
            sous_fenetre.setTitle("Ajouter un compte");
            sous_fenetre.setScene(nouvelle_Scene);
            sous_fenetre.initModality(Modality.APPLICATION_MODAL); // Bloquer la fenetre principale
            sous_fenetre.showAndWait();
            chargerDonnees(connexion);
        });

        Button btn_supprimer_compte = new Button("Supprimer un compte");
        btn_supprimer_compte.setOnAction(actionSupprimerCompte -> {
            VBox sf_suppression_compte = new VBox();
            Scene nouvelle_Scene = new Scene(sf_suppression_compte,400,200);

            ComboBox<Compte> menuComptes = new ComboBox<>(listeComptes);

            menuComptes.setConverter(new StringConverter<Compte>() {
                @Override
                public String toString(Compte compte){
                    if (compte==null) return null;
                    return (compte.getSiteApp()+" • "+compte.getIdentifiant());
                }
                @Override
                public Compte fromString(String string){
                    return null;
                }
            });

            Label lb_compteId_Field = new Label("Compte :");
            Label lb_confirmation = new Label("");
            
            Button btn_valider = new Button("Valider");

            btn_valider.setOnAction(actionValider -> {
                connexion.removeCompte(menuComptes.getValue());
                lb_confirmation.setText("Suppression :\n"+"\nCompte : "+menuComptes.getValue().toString());
            });

            Button btn_fermer = new Button("Fermer");

            sf_suppression_compte.getChildren().addAll(lb_compteId_Field,menuComptes,lb_confirmation,btn_valider,btn_fermer);

            Stage sous_fenetre = new Stage();
            btn_fermer.setOnAction(actionFermer -> sous_fenetre.close());
            sous_fenetre.getIcons().add(logo);
            sous_fenetre.setTitle("Supprimer le compte");
            sous_fenetre.setScene(nouvelle_Scene);
            sous_fenetre.initModality(Modality.APPLICATION_MODAL); // Bloquer la fenetre principale
            sous_fenetre.showAndWait();
            chargerDonnees(connexion);

        });

        Button btn_modifier_compte = new Button("Modifier un compte");
        btn_modifier_compte.setOnAction(actionModifierCompte -> {
            VBox sous_fenetre_modif_compte = new VBox();
            Scene nouvelle_Scene = new Scene(sous_fenetre_modif_compte,450,250);

            ObservableList<String> element = FXCollections.observableArrayList("Site/App","Identifiant","Identifiant2","Mot de passe","Reponse");

            ComboBox<String> menuElement = new ComboBox<>(element);
            menuElement.setValue("Mot de passe");

            ComboBox<Compte> menuComptes = new ComboBox<>(listeComptes);

            menuComptes.setConverter(new StringConverter<Compte>() {
                @Override
                public String toString(Compte compte){
                    if (compte==null) return null;
                    return (compte.getSiteApp()+" • "+compte.getIdentifiant());
                }
                @Override
                public Compte fromString(String string){
                    return null;
                }
            });

            
            Label lb_compteId_Field = new Label("Compte :");
            Label lb_Element_Field = new Label("Élement à modifier :");
            Label lb_newVal_Field = new Label("Nouvelle valeur");
            TextField tf_newVal_Field = new TextField();

            Label lb_confirmation = new Label("");
            
            Button btn_valider = new Button("Valider");

            btn_valider.setOnAction(actionValider -> {
                switch (menuElement.getValue()) {
                    case "Site/App":
                        connexion.modifierCompte(menuComptes.getValue(),"site_app",tf_newVal_Field.getText());
                        break;
                    case "Identifiant":
                        connexion.modifierCompte(menuComptes.getValue(),"identifiant",tf_newVal_Field.getText());
                        break;
                    case "Identifiant2":
                        connexion.modifierCompte(menuComptes.getValue(),"identifiant2",tf_newVal_Field.getText());
                        break;
                    case "Mot de passe":
                        connexion.modifierCompte(menuComptes.getValue(),"pwd",tf_newVal_Field.getText());
                        break;
                    case "Reponse":
                        connexion.modifierCompte(menuComptes.getValue(),"reponse",tf_newVal_Field.getText());
                        break;
                    default:
                        break;
                }
                
                lb_confirmation.setText("Modification :\n"+"\nElement : "+ menuElement.getValue()+"\nNouvelle valeur : "+tf_newVal_Field.getText());
            });

            Button btn_fermer = new Button("Fermer");

            sous_fenetre_modif_compte.getChildren().addAll(lb_compteId_Field,menuComptes,lb_Element_Field,menuElement,lb_newVal_Field,tf_newVal_Field,lb_confirmation,btn_valider,btn_fermer);

            Stage sous_fenetre = new Stage();
            btn_fermer.setOnAction(actionFermer -> sous_fenetre.close());
            sous_fenetre.getIcons().add(logo);
            sous_fenetre.setTitle("Modifier le compte");
            sous_fenetre.setScene(nouvelle_Scene);
            sous_fenetre.initModality(Modality.APPLICATION_MODAL); // Bloquer la fenetre principale
            sous_fenetre.showAndWait();
            chargerDonnees(connexion);
        });

        HBox under_table = new HBox();
        

        Button btn_reset_clipboard = new Button("Vider le presse papier");
        btn_reset_clipboard.setOnAction(e -> connexion.copierDansPressePapier(""));

        Button btn_refresh_table = new Button("Actualiser la table");
        btn_refresh_table.setOnAction(e -> chargerDonnees(connexion));

        Button btn_fermer_app = new Button("Fermer l'application");
        btn_fermer_app.setOnAction(e -> {
            connexion.close();
            primaryStage.close();
        });

        Label lb_information = new Label("fa255@gm.com (1) • az987@gm.com • (2) • mk@gm.com (3)");
        
        //root.getChildren().addAll(table,btn_ajouter_compte,btn_modifier_compte,btn_supprimer_compte,btn_reset_clipboard,btn_refresh_table,btn_fermer_app,lb_information);
        under_table.getChildren().addAll(btn_ajouter_compte,btn_modifier_compte,btn_supprimer_compte,btn_reset_clipboard,btn_refresh_table,btn_fermer_app);
        root.getChildren().addAll(table,under_table,lb_information);


        primaryStage.getIcons().add(logo);
        primaryStage.setTitle("MDPDL");
        primaryStage.setScene(scene);
        //primaryStage.setMaximized(true);
        primaryStage.show();
        
    }

    //pour refresh l'affichage des données 
    public void chargerDonnees(Connexion connexion) {

        listeComptes.clear(); 

        ResultSet resultSet = connexion.query("SELECT * From Comptes");
        try {
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String site = resultSet.getString("site_app");
                String identifiant = resultSet.getString("identifiant");
                String identifiant2 = resultSet.getString("identifiant2");
                String pwd = resultSet.getString("pwd");
                String reponse = resultSet.getString("reponse"); 
                
                listeComptes.add(new Compte(id,site, identifiant, identifiant2, pwd, reponse));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}