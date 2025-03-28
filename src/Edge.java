public class Edge {
    private int sourceId;
    private int targetId;
    private int weight;

    public Edge(int sourceId, int targetId, int weight) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.weight = weight;
    }

    public int getSourceId() { return sourceId; }
    public int getTargetId() { return targetId; }
    public int getWeight() { return weight; }

    public String getKey() {
        return sourceId + "-" + targetId;
    }

    public double getCost() {
        return 1.0 / weight;
    }

    @Override
    public String toString() {
        return sourceId + " -> " + targetId + " (weight: " + weight + ")";
    }
}