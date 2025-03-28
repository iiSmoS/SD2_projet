

public class Artiste {



    private int id;
    private String nom;
    private String categories;

    public Artiste(int id, String nom, String categories) {
        this.id = id;
        this.nom = nom;
        this.categories = categories;

    }

    public int getId() {
        return id;
    }


    @Override
    public String toString() {

        return nom + " (" + categories + ")";
    }
}