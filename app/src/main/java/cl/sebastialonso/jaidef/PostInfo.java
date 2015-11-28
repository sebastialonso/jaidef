package cl.sebastialonso.jaidef;

/**
 * Created by seba on 10/7/15.
 */
class Post {
    String title;
    String description;
    String imageUrl;
    String type;
    double id;

    Post(String _title, String _description, String _imageUrl, String _type, double _id){
        this.title = _title;
        this.description = _description;
        this.imageUrl = _imageUrl;
        this.type = _type;
        this.id = _id;
    }
}