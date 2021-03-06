package com.rawalinfocom.rcontact.model;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiProfileItem {

    private String personName;
    private String personImage;
    private String notiInfo;
    private String notiRequestTime;
    private Integer profileNotiType;
    private String pmRawId;
    private String ppmTag;

    public String getRcpUserPmId() {
        return rcpUserPmId;
    }

    public void setRcpUserPmId(String rcpUserPmId) {
        this.rcpUserPmId = rcpUserPmId;
    }

    private String rcpUserPmId;


    public String getCardCloudId() {
        return cardCloudId;
    }

    public void setCardCloudId(String cardCloudId) {
        this.cardCloudId = cardCloudId;
    }

    private String cardCloudId;

    public Integer getProfileNotiType() {
        return profileNotiType;
    }

    public void setProfileNotiType(Integer profileNotiType) {
        this.profileNotiType = profileNotiType;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setNotiInfo(String notiInfo) {
        this.notiInfo = notiInfo;
    }

    public void setNotiRequestTime(String notiRequestTime) {
        this.notiRequestTime = notiRequestTime;
    }

    public String getPersonName() {
        return personName;
    }

    public String getNotiInfo() {
        return notiInfo;
    }

    public String getNotiRequestTime() {
        return notiRequestTime;
    }

    public String getPersonImage() {
        return personImage;
    }

    public void setPersonImage(String personImage) {
        this.personImage = personImage;
    }

    public String getPmRawId() {
        return StringUtils.defaultString(pmRawId);
    }

    public void setPmRawId(String pmRawId) {
        this.pmRawId = pmRawId;
    }

    public String getPpmTag() {
        return ppmTag;
    }

    public void setPpmTag(String ppmTag) {
        this.ppmTag = ppmTag;
    }
}
