package model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class XeDcChon {
    private int id;
    private String listIdXe;
    private String userId;
    private Date timeStart;
    private Date timeEnd;
}
