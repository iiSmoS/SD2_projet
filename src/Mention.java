public class Mention {
    private int sourceId;
    private int targetId;
    private int weight;

    public Mention(int sourceId, int targetId, int weight) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.weight = weight;
    }

    public int getSourceId() { return sourceId; }
    public int getTargetId() { return targetId; }
    public int getWeight() { return weight; }
}
