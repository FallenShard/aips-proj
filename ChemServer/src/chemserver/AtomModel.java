package chemserver;
import java.io.Serializable;

/**
 *
 * @author FallenShard
 */
public class AtomModel implements Serializable
{
    private int id = -1;
    
    private int x;
    private int y;
    private String type;
    
    private int documentId;
    
    public AtomModel()
    {
        x = 0;
        y = 0;
        type = "";
    }
    
    public AtomModel(String type, int x, int y)
    {
        this.type = type;
        this.x = x;
        this.y = y;
    }
    
    public AtomModel(String type, int x, int y, int documentId)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.documentId = documentId;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }
    
    @Override
    public String toString() {
        return "X:" + x + " Y:" + y + " DocID:" + documentId + " T: " + type + " ID:" + id;
    }
}
