package con.chin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class SizeAndOption {

    private Long id;

    private String optionName;
    private String optionValue;
    private String chinese1;
    private String chinese2;
    private String chinese3;
    private String chinese4;
    private String chinese5;
    private String chinese6;
    private String chinese7;
    private String chinese8;
    private String chinese9;
    private String chinese10;
    private String japanese;
    private String english;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
