package DAO;

import lombok.Getter;
import lombok.Setter;
import model.ThanhVien;
import model.Xe;
import model.XeThue;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
@ManagedBean(name ="XeDAO")
@SessionScoped
public class XeDAO extends DAO implements Serializable {
    @Getter @Setter
    private ThanhVien thanhvien;
    @Getter @Setter
    private Xe xe;
    @Getter @Setter
    private String filter;
    @Getter @Setter
    private List<String> listFilter;
    @Getter @Setter
    private List<Xe> dsXe;
    @Setter @Getter
    private String search;
    @Getter @Setter
    private XeThue thueXe;
    public XeDAO() throws SQLException, IOException, ClassNotFoundException {
        super();
        xe=new Xe();
        thueXe=new XeThue();
        listFilter = new ArrayList<>();
        Field[] fields = Xe.class.getDeclaredFields();
        for(Field f:fields){
            if(!f.getName().equals("Id")){
                listFilter.add(f.getName());
            }
        }
    }
//    public void getAllXe(){
//        String sql = "select * from tblxe";
//        try(PreparedStatement ps = con.prepareStatement(sql)){
//            ResultSet rs = ps.executeQuery();
//            dsXe=new ArrayList<>();
//            while(rs.next()){
//                dsXe.add(mapToXe(rs));
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
    //tìm xe theo thời gian và điều kiện
    public void searchXe() throws SQLException {
        PreparedStatement ps;
        String sql = "select DISTINCT * from tblxe x LEFT join tblXeThue t on x.Id=t.Id ";
        if(filter!=null && search!=null){
            if(filter.equals("name"))sql+=" where x.name like ?";
            if(filter.equals("bienSo")) sql+=" where x.bienSo like ?";
            if(filter.equals("giaThue"))sql+=" where x.giaThue like ?";

            try {
                sql+=" and (t.Id IS NULL or t.timeEnd < ? or t.timeStart > ?) ";
                ps = con.prepareStatement(sql);
                ps.setDate(2, new java.sql.Date(thueXe.getTimeStart().getTime()));
                ps.setDate(3, new java.sql.Date(thueXe.getTimeEnd().getTime()));
                ps.setString(1,"%"+search+"%");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            sql+=" where t.timeEnd < ? or t.timeStart > ? ";
            ps = con.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(thueXe.getTimeStart().getTime()));
            ps.setDate(2, new java.sql.Date(thueXe.getTimeEnd().getTime()));

        }
        ResultSet rs=ps.executeQuery();
        dsXe=new ArrayList<>();
        while(rs.next()){
            dsXe.add(mapToXe(rs));
        }
    }
    private Xe mapToXe(ResultSet rs) throws SQLException {
        Xe xe = new Xe();
        xe.setId(rs.getInt("id"));
        xe.setName(rs.getString("name"));
        xe.setBienSo(rs.getString("bienSo"));
        xe.setGiaThue(rs.getString("giaThue"));
        return xe;

    }

}
