package DAO;

import lombok.Getter;
import lombok.Setter;
import model.ThanhVien;
import model.TkKhachHang;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@ManagedBean(name ="TkKHangDAO")
@SessionScoped
public class TkKhachHangDAO extends DAO implements Serializable{

    @Getter
    private TkKhachHang khachHang;
    private Integer row;
    @Setter
    private FacesMessage message;
    public TkKhachHangDAO() throws SQLException, IOException, ClassNotFoundException {
        super();
        khachHang = new TkKhachHang();
    }

    public void saveAccount() throws SQLException {
        Boolean res=false;
        String sql1 = "SELECT * FROM tblThanhVien WHERE username = ?";
        try (PreparedStatement ps1 = con.prepareStatement(sql1)) {
            ps1.setString(1, khachHang.getThanhVien().getUsername());
            try (ResultSet rs = ps1.executeQuery()) {
                if (rs.next()) {
                    res=true;
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ": UserName đã được sử dụng");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(res ==false){
            String sql = "INSERT INTO tblThanhVien(username, password, name, phone, address) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, khachHang.getThanhVien().getUsername());
                ps.setString(2, khachHang.getThanhVien().getPassword());
                ps.setString(3, khachHang.getThanhVien().getName());
                ps.setInt(4, khachHang.getThanhVien().getPhone());
                ps.setString(5, khachHang.getThanhVien().getAddress());
                row = ps.executeUpdate();
                if(row>0) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Đăng ký thành công!", null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    try {
                        FacesContext.getCurrentInstance().getExternalContext().redirect("/primefaces-chat/ThanhVien/gdMoDau.xhtml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public void kiemTraTT() throws SQLException {
        String sql= "SELECT * FROM tblThanhVien WHERE username = ? and password=?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1, khachHang.getThanhVien().getUsername());
            ps.setString(2, khachHang.getThanhVien().getPassword());
            try(ResultSet rs =ps.executeQuery()){
                if(rs.next()) {
                    setIdSession(mapToThanhVien(rs));
                    khachHang.setThanhVien(mapToThanhVien(rs));
                    if(rs.getString("ViTri").equals("QL")){
                        FacesContext.getCurrentInstance().getExternalContext().redirect("/primefaces-chat/NhanVien/gdChinhNV.xhtml");
                    }else{
                        FacesContext.getCurrentInstance().getExternalContext().redirect("/primefaces-chat/KhachHang/gdChinhKH.xhtml");
                    }
                }else {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "ERROR", "sai thông tin");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }

            }catch(SQLException | IOException e){
                e.printStackTrace();
            }
        }
    }

    private ThanhVien mapToThanhVien(ResultSet rs) throws SQLException {
        ThanhVien thanhVien = new ThanhVien();
        thanhVien.setUsername(rs.getString("username"));
        thanhVien.setPassword(rs.getString("password"));
        thanhVien.setName(rs.getString("name"));
        thanhVien.setPhone(rs.getInt("phone"));
        thanhVien.setAddress(rs.getString("address"));
        thanhVien.setVitri(rs.getString("ViTri"));
        return thanhVien;
    }
    private void setIdSession(ThanhVien thanhvien){
        FacesContext context= FacesContext.getCurrentInstance();
        HttpSession session=(HttpSession) context.getExternalContext().getSession(true);
        session.setAttribute("ThanhVien",thanhvien);

    }
    public ThanhVien getIdSession(){
        FacesContext context= FacesContext.getCurrentInstance();
        HttpSession session=(HttpSession) context.getExternalContext().getSession(true);
        return (ThanhVien) session.getAttribute("ThanhVien");
    }

    public void setTkKhachHang(TkKhachHang khachHang) {
        this.khachHang = khachHang;
    }
}
