import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {
    private Map<Integer, Artist> artists = new HashMap<>();
    private List<Mention> mentions = new ArrayList<>();
    private Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
    private Map<String, Integer> mentionWeights = new HashMap<>();

    private Map<String, Integer> artistNameToId = new HashMap<>();
    private Map<Integer, String> artistIdToName = new HashMap<>();
    private Map<Integer, String> artistCategories = new HashMap<>();


    public Graph(String artistsFile, String mentionsFile) {
        loadArtists(artistsFile);
        loadMentions(mentionsFile);
    }

    private void loadArtists(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 2) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String category = parts.length > 2 ? parts[2] : "";

                    // Ajouter les artistes dans les structures de données
                    Artist artist = new Artist(id, name, category);
                    artists.put(id, artist);
                    adjacencyList.put(id, new ArrayList<>());

                    // 🔥 Correction : Remplir les maps nécessaires
                    artistNameToId.put(name, id);
                    artistIdToName.put(id, name);
                    artistCategories.put(id, category);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier des artistes: " + e.getMessage());
        }
    }


    private void loadMentions(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    int sourceId = Integer.parseInt(parts[0]);
                    int targetId = Integer.parseInt(parts[1]);
                    int weight = Integer.parseInt(parts[2]);

                   /* Créer un graphe dirigé (seulement de source vers cible)
                    adjacencyList.get(sourceId).add(targetId);

                    Stocker le poids de la mention
                    String edgeKey = sourceId + "-" + targetId;
                    mentionWeights.put(edgeKey, weight); */

                    Mention mention = new Mention(sourceId, targetId, weight);
                    mentions.add(mention);

                    adjacencyList.get(sourceId).add(targetId);
                    mentionWeights.put(sourceId + "-" + targetId, weight);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier des mentions: " + e.getMessage());
        }
    }

    public void trouverCheminLePlusCourt(String sourceArtist, String targetArtist) {
        Integer sourceId = artistNameToId.get(sourceArtist);
        Integer targetId = artistNameToId.get(targetArtist);

        if (sourceId == null || targetId == null) {
            System.out.println("Un des artistes n'existe pas dans le graphe.");
            return;
        }

        // Utilisation de BFS pour trouver le plus court chemin
        Map<Integer, Integer> parent = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(sourceId);
        visited.add(sourceId);
        parent.put(sourceId, null);

        boolean found = false;
        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (current == targetId) {
                found = true;
                break;
            }

            for (int neighbor : adjacencyList.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (!found) {
            System.out.println("Aucun chemin trouvé entre " + sourceArtist + " et " + targetArtist);
            return;
        }

        // Reconstruire le chemin
        List<Integer> path = new ArrayList<>();
        for (Integer at = targetId; at != null; at = parent.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        // Afficher le chemin et calculer le coût
        System.out.println("Longueur du chemin : " + (path.size() - 1));

        double coutTotal = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            int from = path.get(i);
            int to = path.get(i + 1);
            String edgeKey = from + "-" + to;
            int weight = mentionWeights.getOrDefault(edgeKey, 1); // Éviter la division par 0
            coutTotal += 1.0 / weight;
        }
        System.out.println("Coût total du chemin : " + coutTotal);

        System.out.println("Chemin :");
        for (int id : path) {
            System.out.println(artistIdToName.get(id) + " (" + artistCategories.get(id) + ")");
        }
    }

    public void trouverCheminMaxMentions(String sourceArtist, String targetArtist) {
        // Cette méthode est laissée vide comme demandé
    }
}