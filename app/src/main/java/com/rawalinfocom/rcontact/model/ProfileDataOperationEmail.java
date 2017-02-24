package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationEmail implements Serializable {

    private int emId;
    private String emEmailId;
    private String emType;
    private int emPublic;
    private String emRcpType;
//    private int emRcpType;

    @JsonProperty("em_id")
    public int getEmId() {
        return this.emId;
    }

    public void setEmId(int emId) {
        this.emId = emId;
    }

    @JsonProperty("em_email_id")
    public String getEmEmailId() {
        return StringUtils.defaultString(this.emEmailId);
    }

    public void setEmEmailId(String emEmailId) {
        this.emEmailId = emEmailId;
    }

    @JsonProperty("em_type")
    public String getEmType() {
        return StringUtils.defaultString(this.emType);
    }

    public void setEmType(String emType) {
        this.emType = emType;
    }

    @JsonProperty("em_public")
    public int getEmPublic() {
        return this.emPublic;
    }

    public void setEmPublic(int emPublic) {
        this.emPublic = emPublic;
    }

    /*@JsonProperty("em_rcp_type")
    public int getEmRcpType() {
        return emRcpType;
    }

    public void setEmRcpType(int emRcpType) {
        this.emRcpType = emRcpType;
    }*/

    @JsonProperty("em_rcp_type")
    public String getEmRcpType() {
        return emRcpType;
    }

    public void setEmRcpType(String emRcpType) {
        this.emRcpType = emRcpType;
    }
}