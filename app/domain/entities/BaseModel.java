package domain.entities;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAttribute;
import java.sql.Timestamp;

@MappedSuperclass
public class BaseModel extends Model {
    @Id
    private Long id;
    @WhenCreated
    private Timestamp whenCreated;

    public Long getId() { return id; }
    public Timestamp getWhenCreated() { return whenCreated; }

    public void setId(Long id) { this.id = id; }
    public void setWhenCreated(Timestamp whenCreated) { this.whenCreated = whenCreated; }
}