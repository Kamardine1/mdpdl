import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;


/**
 * Pour gerer la connection, deconnection, la recherche d'element, l'ajout d'un element, la suppression.
 */
public class Connexion {
    private String DBpath = "Path vers la BDD";
    private Connection connection = null;
    private Statement statement = null;

    public Connexion(String path){
        this.DBpath = path;
    }

    public void connect(){
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DBpath);
            statement = connection.createStatement();
            System.out.println("Connexion à "+ DBpath +" avec succès");
        } catch (ClassNotFoundException notFoundException){
            notFoundException.printStackTrace();
            System.out.println("Erreur de connexion 1");
        } catch (SQLException sqlException){
            sqlException.printStackTrace();
            System.out.println("Erreur de connexion 2");
        }
    }

    public void close(){
        try{
            connection.close();
            statement.close();
            System.out.println("BDD deconnecté avec succès.");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ResultSet query(String request){
        
        ResultSet resultat = null;
        try{
            resultat = statement.executeQuery(request);
            System.out.println("Requete executé avec succès.");
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Erreur dans la requete :"+request);
        }
        return resultat;
    }

    public void addCompte(Compte compte){

        String requeteSQL = "INSERT INTO Comptes (site_app,identifiant,identifiant2,pwd,reponse) VALUES (?,?,?,?,?)";

        try(PreparedStatement pstmt = connection.prepareStatement(requeteSQL,Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, compte.getSiteApp());
            pstmt.setString(2, compte.getIdentifiant());
            pstmt.setString(3, compte.getIdentifiant2());
            pstmt.setString(4, compte.getPwd());
            pstmt.setString(5, compte.getReponse());

            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) { //on recupere les id générés par l'execution de "pstmt.executeUpdate();" qui sont renvoyés sous forme de ResultSet comme si on faisait un SELECT
                if (generatedKeys.next()) { //Si true alors un ID a été généré , on peut le lire
                    int nouvelId = generatedKeys.getInt(1); // on lit la valeur de la premiere colonne (qui correspond à l'id créé par SQLITE)
                    
                    // On met à jour l'objet Java avec ce nouvel ID
                    compte.setId(nouvelId); 
                    
                    System.out.println("Compte '" + compte.getSiteApp() + "' créé avec succès (ID BDD = " + nouvelId + ")");
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    
    }

    public void removeCompte(Compte compte){
        String query = "DELETE FROM Comptes Where id= "+compte.getId()+";";
        try{
            statement.executeUpdate(query);
            System.out.println("Compte d'id = "+compte.getId()+" supprimé.");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void modifierCompte(Compte compte,String element,String new_val){
        String query = "UPDATE Comptes SET "+element+" = '"+new_val+"' WHERE id ="+compte.getId()+";";
        try{
            statement.executeUpdate(query);
            System.out.println("Compte d'id = "+compte.getId()+" modifié avec succès avec "+element+" = "+new_val+".");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void copierDansPressePapier(String msg){
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(msg);
        cb.setContents(ss, null);
        System.out.println(msg+"copié dans le presse papier.");
    }

    public void chargerDonnees(){
        
    }
    
}
