package model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class XeThue {
    private int id;
    private int xeId;
    private Date timeStart;
    private Date timeEnd;
    public XeThue() {}
}
