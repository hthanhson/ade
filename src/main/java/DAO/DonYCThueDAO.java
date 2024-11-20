package DAO;

import lombok.Getter;
import lombok.Setter;
import model.DonYCThue;
import model.ThanhVien;
import model.Xe;
import org.primefaces.PrimeFaces;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name ="billDAO")
@ViewScoped
public class DonYCThueDAO extends DAO implements Serializable {
    @Getter @Setter
    private List<Xe> xeList;
    @Setter @Getter
    private ThanhVien thanhvien;
    private int id;
    @Setter @Getter
    public String message;
    @Setter @Getter
    private List<DonYCThue> listThue;
    @Setter @Getter
    private DonYCThue bill;
    private Date start;
    private Date end;
    public DonYCThueDAO() throws SQLException, IOException, ClassNotFoundException {
        super();
        xeList=new ArrayList<>();
        xeList=getSaveSessionXe();
        bill=new DonYCThue();
        thanhvien=getThanhVienSession();
        getTimeStartEnd();
    }
    public List<Xe> getSaveSessionXe(){
        HttpSession session=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        List<Xe>xeLi=(List<Xe>)session.getAttribute("listXe");
        return xeLi;
    }
    public ThanhVien getThanhVienSession(){
        HttpSession session=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        thanhvien= (ThanhVien)session.getAttribute("ThanhVien");
        return thanhvien;
    }
    public void getTimeStartEnd(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        HttpSession session=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        XeDAO xeDAO= (XeDAO)session.getAttribute("XeDAO");
        start=new java.sql.Date((long) xeDAO.getThueXe().getTimeStart().getTime());
        end=new java.sql.Date((long) xeDAO.getThueXe().getTimeEnd().getTime());
    }
    public void SaveDonYCThue(){
        String listIdXe = "";
        for(Xe xe:xeList){
            changeTTXe(xe.getId());
            listIdXe += xe.getId().toString()+" ";
        }
        try {
            String sql = "Insert Into tblXeDcChon(userId,listIdXe,timeStart,timeEnd) value (?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, thanhvien.getUsername());
                ps.setString(2, listIdXe);
                ps.setDate(3, (java.sql.Date) start);
                ps.setDate(4, (java.sql.Date) end);
                ps.executeUpdate();
            }
            String sql1 = "select * from tblXeDcChon where userId like ? and listIdXe like ?";
            try (PreparedStatement ps1 = con.prepareStatement(sql1)) {
                ps1.setString(1, thanhvien.getUsername());
                ps1.setString(2, listIdXe);
                ResultSet rs = ps1.executeQuery();
                if (rs.next()) {
                    id = rs.getInt("Id");
                }
            }
            String sql2 = "Insert Into tblDonYCThue(userId,tblXeDcChonId,TT) value (?,?,?)";
            try (PreparedStatement ps2 = con.prepareStatement(sql2)) {
                ps2.setString(1, thanhvien.getUsername());
                ps2.setInt(2, id);
                ps2.setString(3, "Đang thuê");
                Integer rs1 =ps2.executeUpdate();

            }
            message = "Thuê xe thành công";
            PrimeFaces.current().executeScript("PF('dialog').show();");
        }catch (SQLException e) {
            message = "Thuê xe không thành công";
            PrimeFaces.current().executeScript("PF('dialog').show();");
            throw new RuntimeException(e);
        }
    }
    private void changeTTXe(int xeid){
        String sql="insert into tblXeThue(xeId,timeStart,timeEnd) value(?,?,?)";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setInt(1, xeid);
            ps.setDate(2,(java.sql.Date) start);
            ps.setDate(3,(java.sql.Date) end);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
//    public void getDonYCThue(){
//        listThue=new ArrayList<>();
//        String tt="DANG THUE";
//        String sql="select * from tblDonYCThue where userId like ? and TT like ?";
//        try(PreparedStatement ps= con.prepareStatement(sql)){
//            ps.setString(1,thanhvien.getUsername());
//            ps.setString(2,tt);
//            ResultSet rs = ps.executeQuery();
//            while(rs.next()){
//                listThue.add(mapToBill(rs));
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private DonYCThue mapToBill(ResultSet rs) throws SQLException {
        DonYCThue bill=new DonYCThue();
        bill.setId(rs.getInt("id"));
        bill.setUserId(rs.getString("userId"));
        bill.setTblXeDcChonId(rs.getString("tblXeDcChonId"));
        bill.setTT(rs.getString("tt"));
        return bill;

    }


}
