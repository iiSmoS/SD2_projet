import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {
    private Map<Integer, Artist> artists;
    private Map<String, Edge> edges;
    private Map<Integer, List<Integer>> adjacencyList;

    private Map<String, Integer> artistNameToId;
    private Map<Integer, String> artistIdToName;

    public Graph(String artistsFile, String mentionsFile) {
        artists = new HashMap<>();
        edges = new HashMap<>();
        adjacencyList = new HashMap<>();
        artistNameToId = new HashMap<>();
        artistIdToName = new HashMap<>();

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
                    String categories = parts.length > 2 ? parts[2] : "";

                    Artist artist = new Artist(id, name, categories);
                    artists.put(id, artist);
                    adjacencyList.put(id, new ArrayList<>());

                    artistNameToId.put(name, id);
                    artistIdToName.put(id, name);
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

                    Edge edge = new Edge(sourceId, targetId, weight);
                    edges.put(edge.getKey(), edge);

                    adjacencyList.get(sourceId).add(targetId);
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

        // Reconstruire et afficher le chemin
        afficherChemin(sourceId, targetId, parent);
    }

    private void afficherChemin(int sourceId, int targetId, Map<Integer, Integer> parent) {
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
            Edge edge = edges.get(from + "-" + to);
            coutTotal += edge.getCost();
        }
        System.out.println("Coût total du chemin : " + coutTotal);

        System.out.println("Chemin :");
        for (int id : path) {
            Artist artist = artists.get(id);
            System.out.println(artist);
        }
    }

    public void trouverCheminMaxMentions(String sourceArtist, String targetArtist) {
        Integer sourceId = artistNameToId.get(sourceArtist);
        Integer targetId = artistNameToId.get(targetArtist);

        if (sourceId == null || targetId == null) {
            System.out.println("Un des artistes n'existe pas dans le graphe.");
            return;
        }

        // Initialisation de Dijkstra
        Map<Integer, Double> distance = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        PriorityQueue<NodeWithCost> priorityQueue = new PriorityQueue<>(
                Comparator.comparingDouble(NodeWithCost::getCost));
        Set<Integer> visited = new HashSet<>();

        // Initialiser les distances à l'infini sauf pour la source
        for (int id : artists.keySet()) {
            distance.put(id, Double.MAX_VALUE);
        }
        distance.put(sourceId, 0.0);
        priorityQueue.add(new NodeWithCost(sourceId, 0.0));

        while (!priorityQueue.isEmpty()) {
            NodeWithCost current = priorityQueue.poll();
            int currentId = current.getId();

            if (currentId == targetId) {
                break; // Nous avons trouvé le chemin le plus court vers la cible
            }

            if (visited.contains(currentId)) {
                continue; // Éviter de traiter deux fois le même nœud
            }

            visited.add(currentId);

            // Pour chaque voisin du nœud actuel
            for (int neighborId : adjacencyList.getOrDefault(currentId, Collections.emptyList())) {
                Edge edge = edges.get(currentId + "-" + neighborId);
                if (edge != null) {
                    double newDistance = distance.get(currentId) + edge.getCost();

                    // Si nous avons trouvé un chemin plus court
                    if (newDistance < distance.get(neighborId)) {
                        distance.put(neighborId, newDistance);
                        parent.put(neighborId, currentId);
                        priorityQueue.add(new NodeWithCost(neighborId, newDistance));
                    }
                }
            }
        }

        if (!parent.containsKey(targetId)) {
            System.out.println("Aucun chemin trouvé entre " + sourceArtist + " et " + targetArtist);
            return;
        }

        // Reconstruire et afficher le chemin
        afficherChemin(sourceId, targetId, parent);
    }

    // Classe auxiliaire pour Dijkstra
    private static class NodeWithCost {
        private int id;
        private double cost;

        public NodeWithCost(int id, double cost) {
            this.id = id;
            this.cost = cost;
        }

        public int getId() {
            return id;
        }

        public double getCost() {
            return cost;
        }
    }
}