package con.chin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class SizeAndOption {

    private Long id;

    private String optionNameChinese;
    private String optionValueChinese;
    private String optionNameJapanese;
    private String optionValueJapanese;
    private String optionNameEnglish;
    private String optionValueEnglish;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
