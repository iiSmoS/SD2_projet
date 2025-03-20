public class Artist {
    private int id;
    private String name;
    private String categories;

    public Artist(int id, String name, String categories) {
        this.id = id;
        this.name = name;
        this.categories = categories;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategories() { return categories; }

    @Override
    public String toString() {
        return name + " (" + categories + ")";
    }
}
