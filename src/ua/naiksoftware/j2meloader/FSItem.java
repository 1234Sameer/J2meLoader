package ua.naiksoftware.j2meloader;

/**
 * 
 * @author Naik
 */
public class FSItem implements SortItem {

    private int imageId;
    Type type;
    private String name, descr;

    public FSItem(int imageId, String name, String descr, Type type) {
        this.imageId = imageId;
        this.name = name;
        this.descr = descr;
        this.type = type;
    }

    public void setType(Type t) {
        type = t;
    }

    public Type getType() {
        return type;
    }

    public void setName(String header) {
        this.name = header;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String subheader) {
        this.descr = subheader;
    }

    public String getDescription() {
        return descr;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public String getSortField() {
        return name;
    }
	
	public enum Type {

		Folder, File, Back
	}
}
