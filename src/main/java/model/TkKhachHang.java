package model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TkKhachHang implements Serializable {
    private int id;
    private ThanhVien thanhVien;
    public TkKhachHang() {
        thanhVien=new ThanhVien();
    }
}
