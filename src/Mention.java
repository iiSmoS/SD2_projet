public class Mention {


    private int sourceId;
    private int cibleId;
    private int poids;

    public Mention(int sourceId, int cibleId, int poids) {
        this.sourceId = sourceId;
        this.cibleId =cibleId;
        this.poids= poids;

    }


    public String getClef() {
        return sourceId +  "-"  +cibleId;
    }

    public double getCout() {
        return 1.0 / poids;
    }

    @Override
    public String toString() {
        return sourceId +" -> " +cibleId + " (weight: " +poids + ")";
    }
}
