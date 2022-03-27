package con.chin.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity@Data
public class Config {

    private Long id; //id
    private String code;//识别id
    private String type;//设置种类
    private String value1;//设置值
    private String value2;//设置值
    private String value3;//设置值
    private String value4;//设置值
    private String value5;//设置值
    private String value6;//设置值
    private String value7;//设置值
    private String value8;//设置值


    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
