package model;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ThanhVien {
     private int id;
     private String username;
     private String password;
     private String name;
     private Integer phone;
     private String address;
     private String vitri;
     public ThanhVien(){}
}
