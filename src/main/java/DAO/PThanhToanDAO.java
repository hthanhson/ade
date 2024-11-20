package DAO;

import lombok.Getter;
import lombok.Setter;
import model.*;
import org.primefaces.PrimeFaces;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ManagedBean(name="pTTDAO")
@SessionScoped
public class PThanhToanDAO extends DAO implements Serializable {
    @Getter @Setter
    private List<DonYCThue> donycthue;
    @Getter @Setter
    private String ttThue;
    @Getter @Setter
    private XeDcChon xeChon;
    @Getter @Setter
    private List<Xe>car;
    @Getter @Setter
    private HashMap<XeDcChon,List<Xe>> XeAndXeChon;
    @Getter @Setter
    private String note;
    @Getter @Setter
    private BigInteger num;
    @Getter @Setter
    private String phat;
    @Getter @Setter
    private String message;
    @Getter @Setter
    private List<PThanhToan> pthanhtoan;
    @Getter @Setter
    private PThanhToan pttd;
    private String donYcThueId;
    private String[] list;
    private ThanhVien thanhvien;
    public PThanhToanDAO () throws SQLException, IOException, ClassNotFoundException {
        super();
        phat="";
        note="";
    }
    // lấy đơn thuê theo id
    public void searchYCThue(){
        donycthue= new ArrayList<>();
        String sql="select * from tblDonYCThue where Id like ? or userId like ?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1,"%"+ttThue+"%");
            ps.setString(2,"%"+ttThue+"%");
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                donycthue.add(mapToDonYCThue(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //lấy thông tin thuê xe theo id khi tạo phiếu thuê - Nv hoặc xem phiếu Thanh toán - kh
    public void searchXe(String xId){
        num=BigInteger.ZERO;
        car= new ArrayList<>();
        XeAndXeChon= new HashMap<>();
        donYcThueId=xId;
        String sql="select * from tblXeDcChon where Id like ?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1,xId);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
               xeChon=mapToXeDcChon(rs);
                list=rs.getString("listIdXe").split(" ");
                for(String li:list){
                     car.add(searchdsxe(li));

                }
                XeAndXeChon.put(mapToXeDcChon(rs),car);
//
            }
            PrimeFaces.current().executeScript("PF('dialog').show();");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //lấy ds xe
    public Xe searchdsxe(String cars){
        String sql="select * from tblXe where Id like ?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1,cars);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                String a=rs.getString("giaThue");
                BigInteger value = BigInteger.valueOf(Long.parseLong(a));
                num=num.add(value);
                return mapToXe(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public void savePTT(){
        // xóa thời gian thuê của xe
        for(String li:list){
            deleteTimeXe(li);
        }
        //cập nhật trạng thái thuê
        String sql1="Update tblDonYCThue set TT=? where tblXeDcChonId like ?";
        try(PreparedStatement ps=con.prepareStatement(sql1)){
            ps.setString(1,"Đã trả");
            ps.setString(2,donYcThueId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //tạo phiếu tt
        int phats;
        if(phat != null && !phat.trim().isEmpty()){
            phats=0;
            phats=Integer.parseInt(phat);
        }else{
            phats=0;
        }
        if(note.equals("")){
            note+="bình thường";
        }
        String sql="Insert into tblPThanhToan(xedcchonid,fee,phat,TT,note)value(?,?,?,?,?)";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1,donYcThueId);
            ps.setString(2, String.valueOf(num));
            ps.setInt(3, phats);
            ps.setString(4,"Chưa thanh toán");
            ps.setString(5,note);
            int rs=ps.executeUpdate();
            if(rs>0){
                message = "Tạo phiếu thanh toán thành công ";
                PrimeFaces.current().executeScript("PF('dialog2').show();");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //lấy danh sách phiếu TT - khách hàng
    public void getPTT(){
        pthanhtoan=new ArrayList<>();
        thanhvien=getThanhVienSession();
        String sql="select * from tblPThanhToan p left join tblDonYcThue d on p.xedcchonid=d.Id where userId like ?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1,thanhvien.getUsername());
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                pthanhtoan.add(mapToPTT(rs));
            }
            FacesContext.getCurrentInstance().getExternalContext().redirect("gdDSPhieuTT.xhtml");
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    //lấy chi tiết 1 phiếu tt
    public void getChiTietpTT(String data){
        String sql="select p.* ,x.* from tblPThanhToan p join tblXeDcChon x on p.XeDcChonId=x.Id where p.Id like ?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1,data);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                pttd=mapToPTT(rs);
                searchXe(pttd.getXedcchonid());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //chuyển tt thanh toán
    public void valdiPTT(String id){
        String sql="update tblPThanhToan set TT = ? where Id like ?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1,"Đã thanh toán");
            ps.setString(2,id);
            int rs=ps.executeUpdate();
            if(rs>0){
                message = "Thanh toán thành công ";
                PrimeFaces.current().executeScript("PF('dialog2').show();");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private void deleteTimeXe(String li){
        String sql="delete from tblXeThue where XeId like ? and timeStart like ? and timeEnd like ?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1,li);
            ps.setDate(2, (Date) xeChon.getTimeStart());
            ps.setDate(3, (Date) xeChon.getTimeEnd());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private PThanhToan mapToPTT(ResultSet rs) throws SQLException {
        PThanhToan ptt=new PThanhToan();
        ptt.setId(rs.getInt("Id"));
        ptt.setXedcchonid(rs.getString("XeDcChonId"));
        ptt.setFee(rs.getString("fee"));
        ptt.setPhat(rs.getString("phat"));
        ptt.setNote(rs.getString("note"));
        ptt.setTT(rs.getString("TT"));
        return ptt;
    }
    private XeDcChon mapToXeDcChon(ResultSet rs)throws SQLException{
        XeDcChon bil=new XeDcChon();
        bil.setId(rs.getInt("Id"));
        bil.setListIdXe(rs.getString("listIdXe"));
        bil.setUserId(rs.getString("userId"));
        bil.setTimeStart(rs.getDate("timeStart"));
        bil.setTimeEnd(rs.getDate("timeEnd"));
        return bil;

    }
    private DonYCThue mapToDonYCThue(ResultSet rs)throws SQLException{
        DonYCThue bill=new DonYCThue();
        bill.setId(rs.getInt("Id"));
        bill.setUserId(rs.getString("userId"));
        bill.setTblXeDcChonId(rs.getString("tblXeDcChonId"));
        bill.setTT(rs.getString("TT"));
        return bill;
    }
    private Xe mapToXe(ResultSet rs)throws SQLException{
        Xe xe=new Xe();
        xe.setId(rs.getInt("Id"));
        xe.setName(rs.getString("name"));
        xe.setBienSo(rs.getString("bienSo"));
        xe.setGiaThue(rs.getString("giaThue"));
        return xe;
    }
    public ThanhVien getThanhVienSession(){
        HttpSession session=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        thanhvien= (ThanhVien)session.getAttribute("ThanhVien");
        return thanhvien;
    }

}
