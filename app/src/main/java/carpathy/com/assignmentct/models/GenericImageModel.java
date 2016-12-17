package carpathy.com.assignmentct.models;


public class GenericImageModel {

    int selectedAtWhatNumber;
    int selectedCount;
    Long order_id;
    String source_path;
    int count;
    int source_type;
    String source_id;
    Boolean imageSelected;
    int whereIsSaved;
    Boolean imageSelectedAtPreview;
    Boolean isUploaded;
    Boolean updateHapperned;
    long image_size;


    public void setImage_size(long image_size) {
        this.image_size = image_size;
    }


    public Boolean getImageSelectedAtPreview() {
        return imageSelectedAtPreview;
    }

    public void setImageSelectedAtPreview(Boolean imageSelectedAtPreview) {
        this.imageSelectedAtPreview = imageSelectedAtPreview;
    }


    public void setIsUploaded(Boolean isUploaded) {
        this.isUploaded = isUploaded;
    }


    public void setUpdateHapperned(Boolean updateHapperned) {
        this.updateHapperned = updateHapperned;
    }

    public void setWhereIsSaved(int whereIsSaved) {
        this.whereIsSaved = whereIsSaved;
    }


    public int getSelectedAtWhatNumber() {
        return selectedAtWhatNumber;
    }

    public void setSelectedAtWhatNumber(int selectedAtWhatNumber) {
        this.selectedAtWhatNumber = selectedAtWhatNumber;
    }


    public Boolean getImageSelected() {
        return imageSelected;
    }

    public void setImageSelected(Boolean imageSelected) {
        this.imageSelected = imageSelected;
    }

    public void setSelectedCount(int selectedCount) {
        this.selectedCount = selectedCount;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public void setSource_type(int source_type) {
        this.source_type = source_type;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public String getSource_path() {
        return source_path;
    }

    public void setSource_path(String source_path) {
        this.source_path = source_path;
    }

    public void setCount(int count) {
        this.count = count;
    }

}

