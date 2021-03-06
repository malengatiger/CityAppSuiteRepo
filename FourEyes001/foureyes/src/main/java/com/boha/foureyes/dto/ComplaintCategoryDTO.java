package com.boha.foureyes.dto;

import java.util.List;

/**
 * Created by aubreyM on 15/04/18.
 */
public class ComplaintCategoryDTO {
    private static final long serialVersionUID = 1L;
    private Integer complaintCategoryID;
    private String complaintCategoryName;
    private List<ComplaintTypeDTO> complaintTypeList;
    private Integer municipalityID;

    public Integer getComplaintCategoryID() {
        return complaintCategoryID;
    }

    public void setComplaintCategoryID(Integer complaintCategoryID) {
        this.complaintCategoryID = complaintCategoryID;
    }

    public String getComplaintCategoryName() {
        return complaintCategoryName;
    }

    public void setComplaintCategoryName(String complaintCategoryName) {
        this.complaintCategoryName = complaintCategoryName;
    }

    public List<ComplaintTypeDTO> getComplaintTypeList() {
        return complaintTypeList;
    }

    public void setComplaintTypeList(List<ComplaintTypeDTO> complaintTypeList) {
        this.complaintTypeList = complaintTypeList;
    }

    public Integer getMunicipalityID() {
        return municipalityID;
    }

    public void setMunicipalityID(Integer municipalityID) {
        this.municipalityID = municipalityID;
    }
}
