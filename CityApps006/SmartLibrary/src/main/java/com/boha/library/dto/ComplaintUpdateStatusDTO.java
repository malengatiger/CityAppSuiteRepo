
package com.boha.library.dto;

import java.io.Serializable;

/**
 *
 * @author aubreyM
 */
public class ComplaintUpdateStatusDTO implements Serializable {
    private Boolean customerOKFlag;
    private String customerComment;
    private static final long serialVersionUID = 1L;
    private Integer complaintUpdateStatusID;
    private Long dateUpdated;
    private boolean resolvedFlag;
    private String remarks;
    private Integer complaintID;
    private Integer id;
    private String status;
    private MunicipalityStaffDTO municipalityStaff;

    public ComplaintUpdateStatusDTO() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getComplaintUpdateStatusID() {
        return complaintUpdateStatusID;
    }

    public void setComplaintUpdateStatusID(Integer complaintUpdateStatusID) {
        this.complaintUpdateStatusID = complaintUpdateStatusID;
    }

    public Boolean isCustomerOKFlag() {
        return customerOKFlag;
    }

    public Long getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Long dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public boolean isResolvedFlag() {
        return resolvedFlag;
    }

    public boolean getResolvedFlag() {
        return resolvedFlag;
    }

    public void setResolvedFlag(boolean resolvedFlag) {
        this.resolvedFlag = resolvedFlag;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getComplaintID() {
        return complaintID;
    }

    public void setComplaintID(Integer complaintID) {
        this.complaintID = complaintID;
    }


    public MunicipalityStaffDTO getMunicipalityStaff() {
        return municipalityStaff;
    }

    public void setMunicipalityStaff(MunicipalityStaffDTO municipalityStaff) {
        this.municipalityStaff = municipalityStaff;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (complaintUpdateStatusID != null ? complaintUpdateStatusID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComplaintUpdateStatusDTO)) {
            return false;
        }
        ComplaintUpdateStatusDTO other = (ComplaintUpdateStatusDTO) object;
        if ((this.complaintUpdateStatusID == null && other.complaintUpdateStatusID != null) || (this.complaintUpdateStatusID != null && !this.complaintUpdateStatusID.equals(other.complaintUpdateStatusID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.smartcity.data.ComplaintUpdateStatus[ complaintUpdateStatusID=" + complaintUpdateStatusID + " ]";
    }

    public Boolean getCustomerOKFlag() {
        return customerOKFlag;
    }

    public void setCustomerOKFlag(Boolean customerOKFlag) {
        this.customerOKFlag = customerOKFlag;
    }

    public String getCustomerComment() {
        return customerComment;
    }

    public void setCustomerComment(String customerComment) {
        this.customerComment = customerComment;
    }
    
}
