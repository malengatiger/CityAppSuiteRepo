
package com.boha.library.dto;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author aubreyM
 */
public class MunicipalityImageDTO implements Serializable, ImageInterface {
    private static final long serialVersionUID = 1L;
    private Integer municipalityImageID;
    private String fileName, localFilepath;
    private Long dateUploaded;
    private Long dateTaken;
    private Double latitude;
    private Double longitude;
    private Integer municipalityID;
    private Boolean activeFlag;
    private String url;
    private String secureUrl;
    private String signature;
    private String eTag;
    private Integer height;
    private Integer width;
    private Integer bytes;

    public String getUrl() {
        return url;
    }
    @Override
    public File getFile() {
        if (localFilepath != null) {
            return new File(localFilepath);
        }
        return null;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecureUrl() {
        return secureUrl;
    }

    public void setSecureUrl(String secureUrl) {
        this.secureUrl = secureUrl;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getBytes() {
        return bytes;
    }

    public void setBytes(Integer bytes) {
        this.bytes = bytes;
    }
    public String getLocalFilepath() {
        return localFilepath;
    }

    public void setLocalFilepath(String localFilepath) {
        this.localFilepath = localFilepath;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public MunicipalityImageDTO() {
    }

    public MunicipalityImageDTO(Integer municipalityImageID) {
        this.municipalityImageID = municipalityImageID;
    }


    public Boolean isActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    
    public Integer getMunicipalityID() {
        return municipalityID;
    }

    public void setMunicipalityID(Integer municipalityID) {
        this.municipalityID = municipalityID;
    }

    public Integer getMunicipalityImageID() {
        return municipalityImageID;
    }

    public void setMunicipalityImageID(Integer municipalityImageID) {
        this.municipalityImageID = municipalityImageID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Long dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public Long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

   

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (municipalityImageID != null ? municipalityImageID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MunicipalityImageDTO)) {
            return false;
        }
        MunicipalityImageDTO other = (MunicipalityImageDTO) object;
        if ((this.municipalityImageID == null && other.municipalityImageID != null) || (this.municipalityImageID != null && !this.municipalityImageID.equals(other.municipalityImageID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.smartcity.data.MunicipalityImage[ municipalityImageID=" + municipalityImageID + " ]";
    }
    
}
