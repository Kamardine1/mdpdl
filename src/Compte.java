/**
 * Pour gerer les contenu de chaque ligne de la BDD
 */
public class Compte {

    private int id;
    private String site_app;
    private String identifiant;
    private String identifiant2;
    private String pwd;
    private String reponse;

    public Compte(int id,String site_app,String identifiant, String identifiant2, String pwd, String reponse){
        this.id = id;
        this.site_app = site_app;
        this.identifiant = identifiant;
        this.identifiant2 = identifiant2;
        this.pwd = pwd;
        this.reponse = reponse;
    }

    public void setId(int new_value){this.id = new_value;}
    public void setSiteApp(String new_value){this.site_app = new_value;}
    public void setIdentifiant(String new_value){this.identifiant = new_value;}
    public void setIdentifiant2(String new_value){this.identifiant2 = new_value;}
    public void setPwd(String new_value){this.pwd = new_value;}
    public void setReponse(String new_value){this.reponse = new_value;}
    public int getId(){return this.id;}
    public String getSiteApp(){return this.site_app;}
    public String getIdentifiant(){return this.identifiant;}
    public String getIdentifiant2(){return this.identifiant2;}
    public String getPwd(){return this.pwd;}
    public String getReponse(){return this.reponse;}

}
