package com.rawalinfocom.rcontact.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Aniruddh on 04/10/17.
 */

public class RelationRecommendationType implements Serializable {

    String firstName;
    String lastName;
    String number;
    String dateAndTime;

    ArrayList<IndividualRelationRecommendationType> individualRelationRecommendationTypeArrayList;

    public ArrayList<IndividualRelationRecommendationType> getIndividualRelationRecommendationTypeArrayList() {
        return individualRelationRecommendationTypeArrayList;
    }

    public void setIndividualRelationRecommendationTypeArrayList(ArrayList<IndividualRelationRecommendationType> individualRelationRecommendationTypeArrayList) {
        this.individualRelationRecommendationTypeArrayList = individualRelationRecommendationTypeArrayList;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
}
