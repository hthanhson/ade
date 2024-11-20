package DAO;

import lombok.Getter;
import lombok.Setter;
import model.ThanhVien;
import model.Xe;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ManagedBean(name ="XeDcDAO")
@SessionScoped
public class XeDcChonDAO extends DAO implements Serializable {
    @Getter @Setter
    private Xe xe;
    @Getter @Setter
    private HashMap<Integer,String> acid;
    @Getter @Setter
    private Integer bac;
    @Getter @Setter
    private List<Xe>listXe;
    private String billId;
    private String listIdXe;
    @Getter @Setter
    private List<Xe> dsXe;
    public XeDcChonDAO() throws SQLException, IOException, ClassNotFoundException {
        super();
        acid= new HashMap<>();
        listXe= new ArrayList<>();
    }
    // lấy danh sach xe đã chọn để chuẩn bị thuê
    public void getInfXe() throws SQLException {
        listXe.clear();
        for(Integer xeId: acid.keySet()){
            if(Integer.valueOf(acid.get(xeId)).equals(1)){
                ResultSet rs;
                String sql="select * from tblxe where Id like ?";
                try(PreparedStatement ps= con.prepareStatement(sql)){
                    ps.setInt(1, xeId);
                    rs=ps.executeQuery();
                    if(rs.next()){
                        listXe.add(mapToXe(rs));
                    }
                }
            }
        }
        if(!listXe.isEmpty()){
            setSaveSessionXe(listXe);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/primefaces-chat/KhachHang/gdXacNhan1.xhtml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //lấy chi  tiết phiếu thuê
//    public void listDsXe(){
//        dsXe=new ArrayList<>();
//        listXe= new ArrayList<>();
//        billId=FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("billId");
//        String sql="select * from tblXeDcChon c join tblDonYCThue t on t.tblXeDcChonId = c.Id where t.Id like ? ";
//        try(PreparedStatement ps= con.prepareStatement(sql)){
//            ps.setString(1,billId);
//            ResultSet rs = ps.executeQuery();
//            if(rs.next()){
//                listIdXe=rs.getString("listIdXe");
//
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        String []idXe=listIdXe.split(" ");
//        for(String xe:idXe){
//           String sql1="select * from tblXe where tblxe.Id like ?";
//           try(PreparedStatement ps=con.prepareStatement(sql1)){
//               ps.setString(1,xe);
//               ResultSet rs = ps.executeQuery();
//               if(rs.next()){
//                   dsXe.add(mapToXe(rs));
//               }
//           } catch (SQLException e) {
//               throw new RuntimeException(e);
//           }
//        }
//    }
    private void setSaveSessionXe(List<Xe> listXe){
        FacesContext context=FacesContext.getCurrentInstance();
        HttpSession session=(HttpSession) context.getExternalContext().getSession(true);
        session.setAttribute("listXe",listXe);
    }
    private Xe mapToXe(ResultSet rs) throws SQLException {
        xe = new Xe();
        xe.setId(rs.getInt("id"));
        xe.setName(rs.getString("name"));
        xe.setBienSo(rs.getString("bienSo"));
        xe.setGiaThue(rs.getString("giaThue"));
        return xe;

    }
}
