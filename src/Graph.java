import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {


    private Map<Integer, Artiste> artistes;
    private Map<String, Mention> mentions;
    private Map<Integer, List<Integer>> listeAdjacence;

    private Map<String, Integer> nomArtisteVersId;
    private Map<Integer, String> idArtisteVersNom;

    public Graph(String fichierArtistes,String fichierMentions) {

        artistes= new HashMap<>();
        mentions= new HashMap<>();
        listeAdjacence= new HashMap<>();
        nomArtisteVersId =new HashMap<>();
        idArtisteVersNom =new HashMap<>();

        chargerArtistes(fichierArtistes);
        chargerMentions(fichierMentions );

    }


    private void chargerArtistes(String fichierNom) {

        try (BufferedReader br = new BufferedReader(new FileReader(fichierNom))) {

            String ligne;

            while ((ligne = br.readLine()) != null) {
                String[] parties=ligne.split(",", 3);
                if (parties.length >= 2) {

                    int id=Integer.parseInt(parties[0] );
                    String name =parties[1];
                    String categories =parties.length > 2 ? parties[2] : "" ;

                    Artiste artiste = new Artiste(id, name, categories);
                    artistes.put(id,artiste) ;
                    listeAdjacence.put( id, new ArrayList<>());


                    nomArtisteVersId.put(name,id);
                    idArtisteVersNom.put(id,name);


                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier des artistes: " + e.getMessage());
        }
    }

    private void chargerMentions(String fichierNom) {

        try (BufferedReader br = new BufferedReader(new FileReader(fichierNom))){

            String ligne;

            while ((ligne = br.readLine()) != null) {
                String[]  parties=ligne.split(",");
                if (parties.length>=3) {

                    int sourceId=Integer.parseInt(parties[0] );
                    int cibleId =Integer.parseInt(parties[1]);
                    int poids = Integer.parseInt(parties[2]);

                    Mention mention = new Mention(sourceId,cibleId,poids);

                    mentions.put(mention.getClef(), mention) ;
                    listeAdjacence.get(sourceId).add(cibleId) ;



                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier des artistes: " + e.getMessage());
        }
    }

    public void trouverCheminLePlusCourt(String artisteSource, String artisteCible) {


        Integer sourceId = nomArtisteVersId.get(artisteSource);

        Integer cibleId = nomArtisteVersId.get(artisteCible);

        if (sourceId ==null || cibleId== null) {

            System.out.println("Un des artistes n'est pas trouvé dans le graphe.");

            return;

        }


        Map<Integer, Integer> parent =new HashMap<>();
        Queue<Integer> file =new LinkedList<>();
        Set<Integer> visite = new HashSet<>();


        file.add(sourceId);
        visite.add(sourceId);
        parent.put(sourceId, null);


        boolean trouve=false;
        while (!file.isEmpty()) {

            int courant =file.poll();

            if (courant== cibleId) {

                trouve=true ;
                break;


            }

            for (int voisin:  listeAdjacence.getOrDefault(courant,  Collections.emptyList())) {

                if (!visite.contains(voisin) ) {

                    visite.add(voisin);
                    parent.put(voisin,courant) ;
                    file.add(voisin);


                }
            }


        }

        if (!trouve) {
            System.out.println("Aucun chemin trouvé entre "  + artisteSource + " et " + artisteCible );

            return;
        }



        afficherChemin(cibleId,parent);

    }

    private void afficherChemin(int cibleId, Map<Integer, Integer> parent) {


        List<Integer> chemin =new ArrayList<>();
        for (Integer actuel =cibleId; actuel!= null; actuel=parent.get(actuel)) {

            chemin.add(actuel);

        }
        Collections.reverse(chemin);


        System.out.println("Longueur du chemin : " + (chemin.size() - 1));



        double coutTotal = 0.0;

        for (int i = 0; i < chemin.size() - 1; i++) {

            int depuis =chemin.get(i);
            int vers=chemin.get(i + 1);
            Mention mention=  mentions.get(depuis + "-" +  vers);
            coutTotal += mention.getCout();


        }

        System.out.println("Coût total du chemin : " + coutTotal);


        System.out.println("Chemin :");

        for (int id :chemin) {
            Artiste artiste = artistes.get(id);

            System.out.println(artiste);

        }

    }


    public void trouverCheminMaxMentions(String artisteSource,String artisteCible) {


        Integer sourceId = nomArtisteVersId.get(artisteSource);
        Integer cibleId = nomArtisteVersId.get(artisteCible);


        if (sourceId == null || cibleId == null) {

            System.out.println("Un des artistes n'est pas trouvé  dans le graphe.");

            return;
        }



        Map<Integer, Double> distance = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        PriorityQueue<MentionAvecCout> priorityQueue = new PriorityQueue<>(
                Comparator.comparingDouble(MentionAvecCout::getCout));
        Set<Integer> visites= new HashSet<>();



        for (int id: artistes.keySet()) {

            distance.put(id,Double.MAX_VALUE);
        }
        distance.put(sourceId, 0.0);

        priorityQueue.add(new MentionAvecCout(sourceId,0.0));


        while (!priorityQueue.isEmpty()) {

            MentionAvecCout courant= priorityQueue.poll();
            int courantId=courant.getId();

            if (courantId ==cibleId) {

                break;

            }

            if (visites.contains(courantId)) {

                continue;

            }


            visites.add(courantId);

            for (int IdVoisin :listeAdjacence.getOrDefault(courantId,Collections.emptyList())) {

                Mention mention = mentions.get(courantId +"-" +IdVoisin);

                if (mention != null) {

                    double newDistance= distance.get(courantId) +mention.getCout();

                    if (newDistance <distance.get(IdVoisin)) {

                        distance.put(IdVoisin, newDistance);
                        parent.put(IdVoisin, courantId) ;
                        priorityQueue.add(new MentionAvecCout(IdVoisin,  newDistance));

                    }

                }
            }


        }

        if (!parent.containsKey(cibleId)) {

            System.out.println("Aucun chemin trouvé entre "+artisteSource+" et " +artisteCible);
            return;

        }


        afficherChemin( cibleId, parent);
    }


    private static class MentionAvecCout {

        private int id;
        private double cout;

        public MentionAvecCout(int id,double cout) {

            this.id = id;
            this.cout= cout;

        }

        public int getId() {
            return id;
        }


        public double getCout() {
            return cout;
        }

    }


}