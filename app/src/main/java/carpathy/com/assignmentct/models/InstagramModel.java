package carpathy.com.assignmentct.models;

public class InstagramModel {
    int height;
    String media_id;
    String url;
    Boolean checked;


    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public int getHeight() {
        return height;
    }

    public String getMedia_id() {
        return media_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
