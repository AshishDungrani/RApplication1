package com.rawalinfocom.rcontact.relation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.BuildConfig;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncGetWebServiceCall;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.EditProfileActivity;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableRelationMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ExistingRelationRequest;
import com.rawalinfocom.rcontact.model.FamilyRelationMaster;
import com.rawalinfocom.rcontact.model.IndividualRelationType;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.Relation;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;
import com.rawalinfocom.rcontact.model.RelationRequest;
import com.rawalinfocom.rcontact.model.RelationResponse;
import com.rawalinfocom.rcontact.model.RelationUserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 03/10/17.
 */

public class AddNewRelationActivity extends BaseActivity implements WsResponseListener, View.OnClickListener {

    @BindView(R.id.relative_root_new_relation)
    LinearLayout relativeRootNewRelation;
    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.image_add_new)
    ImageView imageAddNew;
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.input_value_add_name)
    EditText inputValueAddName;
    @BindView(R.id.img_clear)
    ImageView imgClear;
    @BindView(R.id.input_value_business)
    EditText inputValueBusiness;
    @BindView(R.id.input_value_family)
    EditText inputValueFamily;
    @BindView(R.id.checkbox_friend)
    CheckBox checkboxFriend;
    @BindView(R.id.button_name_done)
    Button buttonNameDone;
    @BindView(R.id.button_name_cancel)
    Button buttonNameCancel;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_title_business)
    TextView txtTitleBusiness;
    @BindView(R.id.txt_title_family)
    TextView txtTitleFamily;
    @BindView(R.id.txt_title_friend)
    TextView txtTitleFriend;
    @BindView(R.id.linear_business_relation)
    LinearLayout linearBusinessRelation;
    @BindView(R.id.img_business_clear)
    ImageView imgBusinessClear;
    @BindView(R.id.img_family_clear)
    ImageView imgFamilyClear;
    @BindView(R.id.input_value_friend)
    EditText inputValueFriend;
    @BindView(R.id.img_friend_clear)
    ImageView imgFriendClear;
    @BindView(R.id.linear_single_business_relation)
    LinearLayout linearSingleBusinessRelation;

    private TableRelationMaster tableRelationMaster;

    private Activity activity;
    private String isFrom = "";

    public static int orgPosition = -1;
    public static int businessRelationPosition = -1;
    public static int familyRelationPosition = -1;

    private Dialog businessRelationDialog;
    private String pmId = "", contactName = "", contactNumber = "", profileImage = "", organizationName = "",
            businessRelationName = "", strGender = "", familyRelation = "", friendRelation = "", rcpGender = "";
    private int businessRelationId, familyRelationId, organizationId;
    private ArrayList<ProfileDataOperationOrganization> arrayListOrganization;
    private int colorPineGreen;

    //    private RelationRecommendationType recommendationType;
    private ArrayList<IndividualRelationType> arrayList;
    private ArrayList<String> arrayListOrgName, arrayListOrgId;
    private int businessPosition;
    private boolean isAdd = false, isFamilyAlreadyAdded = false;

    private ArrayList<Relation> familyRelationList;
    private boolean isSelectedOrgVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_relation);

        ButterKnife.bind(this);
        getIntentDetails(getIntent());
        initToolbar();
        init();
    }

    private void initToolbar() {
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textToolbarTitle.setText(getResources().getString(R.string.add_relation_toolbar_title));
        imageActionBack = ButterKnife.findById(includeToolbar, R.id.image_action_back);
        imageActionBack.setOnClickListener(this);
        imageAddNew = ButterKnife.findById(includeToolbar, R.id.image_add_new);
        imageAddNew.setVisibility(View.GONE);
    }

    private void init() {

        tableRelationMaster = new TableRelationMaster(databaseHandler);

        if (tableRelationMaster.getRelationCount() == 0) {
            tableRelationMaster.insertData();
        }

        activity = AddNewRelationActivity.this;
        arrayListOrganization = new ArrayList<>();

        colorPineGreen = ContextCompat.getColor(activity, R.color.colorAccent);

        inputValueAddName.setTypeface(Utils.typefaceRegular(this));
        inputValueBusiness.setTypeface(Utils.typefaceRegular(this));
        inputValueFamily.setTypeface(Utils.typefaceRegular(this));

        inputValueAddName.setFocusable(false);
        inputValueBusiness.setFocusable(false);
        inputValueFamily.setFocusable(false);

        inputValueAddName.setOnClickListener(this);
        inputValueBusiness.setOnClickListener(this);
        inputValueFamily.setOnClickListener(this);

        txtTitle.setTypeface(Utils.typefaceRegular(this));
        txtTitleBusiness.setTypeface(Utils.typefaceRegular(this));
        txtTitleFamily.setTypeface(Utils.typefaceRegular(this));
        txtTitleFriend.setTypeface(Utils.typefaceRegular(this));

        buttonNameDone.setTypeface(Utils.typefaceRegular(this));
        buttonNameDone.setOnClickListener(this);
        buttonNameCancel.setTypeface(Utils.typefaceRegular(this));
        buttonNameCancel.setOnClickListener(this);

        arrayList = new ArrayList<>();
        arrayListOrgName = new ArrayList<>();
        arrayListOrgId = new ArrayList<>();
        familyRelationList = new ArrayList<>();

        imgBusinessClear.setOnClickListener(this);
        imgClear.setOnClickListener(this);
        imgFamilyClear.setOnClickListener(this);

        if (isFrom.equalsIgnoreCase("existing")) {

            inputValueAddName.setEnabled(false);
            inputValueAddName.setText(Html.fromHtml("<font color='#00796B'> " + contactName +
                    "</font><br/>" + contactNumber));

            getRCPExistingRelation();

        } else {

            Glide.with(activity)
                    .load(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .into(imageProfile);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGender();
        getOrganizationsList();
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        if (error == null) {

            //<editor-fold desc="REQ_SEND_RELATION_REQUEST">
            if (serviceType.equals(WsConstants.REQ_SEND_RELATION_REQUEST)) {
                WsResponseObject sendRelationRequestObject = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (sendRelationRequestObject != null && StringUtils.equalsIgnoreCase
                        (sendRelationRequestObject.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ArrayList<RelationRequest> relationRequestResponse = sendRelationRequestObject.
                            getArrayListRelationRequestResponse();

                    Utils.showSuccessSnackBar(activity, relativeRootNewRelation,
                            "New Relation Added Successfully!!!");

//                    Utils.setBooleanPreference(AddNewRelationActivity.this,
//                            AppConstants.PREF_GET_RELATION, false);

                    finish();

                } else {
                    if (sendRelationRequestObject != null) {
                        Log.e("error response", sendRelationRequestObject.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootNewRelation,
                                sendRelationRequestObject.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "sendRelationRequestResponse null");
                        Utils.showErrorSnackBar(this, relativeRootNewRelation, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            //<editor-fold desc="REQ_GET_RELATION_MASTER">
            if (serviceType.equals(WsConstants.REQ_GET_RELATION_MASTER)) {
                WsResponseObject getFamilyRelationObject = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (getFamilyRelationObject != null && StringUtils.equalsIgnoreCase
                        (getFamilyRelationObject.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ArrayList<FamilyRelationMaster> familyRelation = getFamilyRelationObject.getFamilyRelationList();

                    for (int i = 0; i < familyRelation.size(); i++) {

                        Relation relation = new Relation();
                        relation.setRmId(familyRelation.get(i).getId());
                        relation.setRmRelationName(familyRelation.get(i).getRmParticular());
                        relation.setRmRelationType(String.valueOf(familyRelation.get(i).getRrmType()));

                        familyRelationList.add(relation);
                    }

                    dialogFamilyRelation();

                } else {
                    if (getFamilyRelationObject != null) {
                        Log.e("error response", getFamilyRelationObject.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootNewRelation,
                                getFamilyRelationObject.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "getFamilyRelationObject null");
                        Utils.showErrorSnackBar(this, relativeRootNewRelation, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            //<editor-fold desc="REQ_GET_RELATION">
            if (serviceType.equals(WsConstants.REQ_GET_RELATION)) {
                WsResponseObject getRelationRequestObject = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (getRelationRequestObject != null && StringUtils.equalsIgnoreCase
                        (getRelationRequestObject.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ArrayList<ExistingRelationRequest> allExistingRelationList = getRelationRequestObject.getAllExistingRelationList();
                    ArrayList<ExistingRelationRequest> recommendationsRelationList = getRelationRequestObject.getRecommendationsRelationList();

                    setData(allExistingRelationList, recommendationsRelationList);

//                    Utils.setBooleanPreference(AddNewRelationActivity.this,
//                            AppConstants.PREF_GET_RELATION, false);

                } else {
                    if (getRelationRequestObject != null) {
                        Log.e("error response", getRelationRequestObject.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootNewRelation,
                                getRelationRequestObject.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "getRelationRequestObject null");
                        Utils.showErrorSnackBar(this, relativeRootNewRelation, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
            Utils.showErrorSnackBar(this, relativeRootNewRelation, "" + error
                    .getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.input_value_family:

                if (StringUtils.isEmpty(strGender)) {
                    startActivityIntent(activity, EditProfileActivity.class, null);
                } else {

                    if (familyRelationList.size() > 0) {
                        dialogFamilyRelation();
                    } else {
                        getFamilyRelation();
                    }
                }
                break;

            case R.id.input_value_add_name:
                Intent intent = new Intent(activity, RContactsListActivity.class);
                startActivityForResult(intent, 101);// Activity is started with requestCode
                break;
            case R.id.input_value_business:

                if (arrayListOrganization.size() > 0) {
                    dialogBusinessRelation();
                } else {
                    startActivityIntent(activity, EditProfileActivity.class, null);
                }
                break;

            case R.id.image_action_back:
                finish();
                break;
            case R.id.button_name_done:

                if (StringUtils.isEmpty(contactName)) {
                    Utils.showErrorSnackBar(activity, relativeRootNewRelation,
                            "Please select User to establish relation!!");
                } else if (!StringUtils.isEmpty(organizationName) || checkboxFriend.isChecked() ||
                        !StringUtils.isEmpty(familyRelation)) {

                    ArrayList<RelationRequest> relationRequests = new ArrayList<>();

                    RelationRequest friendRelationRequest = new RelationRequest();

                    if (friendRelation.equalsIgnoreCase("")) {
                        if (checkboxFriend.isChecked()) {
                            friendRelationRequest.setRcRelationMasterId(1);
                            friendRelationRequest.setRrmToPmId(Integer.parseInt(pmId));
                            friendRelationRequest.setRrmType(1);

                            relationRequests.add(friendRelationRequest);
                        }
                    }

                    RelationRequest familyRelationRequest = new RelationRequest();

                    if (!StringUtils.isBlank(familyRelation)) {

                        if (!isFamilyAlreadyAdded) {
                            familyRelationRequest.setRcRelationMasterId(familyRelationId);
                            familyRelationRequest.setRrmToPmId(Integer.parseInt(pmId));
                            familyRelationRequest.setRrmType(2);

                            relationRequests.add(familyRelationRequest);
                        }
                    }

                    if (linearBusinessRelation.getVisibility() == View.VISIBLE) {

                        for (int i = 0; i < arrayList.size(); i++) {

                            if (arrayList.get(i).getIsInUse().equalsIgnoreCase("0")) {
                                RelationRequest businessRelationRequest = new RelationRequest();

                                businessRelationRequest.setRcRelationMasterId(Integer.parseInt(
                                        arrayList.get(i).getRelationId()));
                                businessRelationRequest.setRrmToPmId(Integer.parseInt(pmId));
                                businessRelationRequest.setRcOrgId(Integer.parseInt(
                                        arrayList.get(i).getOrganizationId()));
                                businessRelationRequest.setRrmType(3);
                                businessRelationRequest.setOmName(arrayList.get(i).getOrganizationName());

                                relationRequests.add(businessRelationRequest);
                            }
                        }
                    } else {

                        RelationRequest businessRelationRequest = new RelationRequest();

                        if (!StringUtils.isBlank(organizationName)) {
                            businessRelationRequest.setRcRelationMasterId(businessRelationId);
                            businessRelationRequest.setRrmToPmId(Integer.parseInt(pmId));
                            businessRelationRequest.setRcOrgId(organizationId);
                            businessRelationRequest.setRrmType(3);
                            businessRelationRequest.setOmName(organizationName);

                            relationRequests.add(businessRelationRequest);
                        }
                    }

                    sendRelationRequest(relationRequests);

                } else {
                    Utils.showErrorSnackBar(activity, relativeRootNewRelation,
                            "Please select any one to establish relation!!");
                }
                break;
            case R.id.button_name_cancel:
                finish();
                break;

            case R.id.img_clear:

                // clear selected rcp data
                pmId = "";
                contactName = "";
                profileImage = "";
                imgClear.setVisibility(View.GONE);
                inputValueAddName.setText(contactName);

                Glide.with(activity)
                        .load(R.drawable.home_screen_profile)
                        .bitmapTransform(new CropCircleTransformation(activity))
                        .into(imageProfile);

                rcpGender = "";
                familyRelationList.clear();

                clearBusinessData();
                clearFamilyData();
                clearFriendData();

                // TODO : Hardik
                if (arrayList.size() > 0) {
                    linearSingleBusinessRelation.setVisibility(View.VISIBLE);
                    linearBusinessRelation.setVisibility(View.GONE);
                    arrayList.clear();
                    arrayListOrgId.clear();
                    arrayListOrgName.clear();
                }

                break;

            case R.id.img_business_clear:

                clearBusinessData();

                break;

            case R.id.img_family_clear:

                clearFamilyData();

                break;
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == 101) {

            Utils.hideKeyBoard(activity);

            if (data != null) {
                if (data.getStringExtra("isBack").equalsIgnoreCase("0")) {
                    //If everything went Ok, change to another activity.
                    pmId = data.getStringExtra("pmId");
                    contactName = data.getStringExtra("contactName");
                    contactNumber = data.getStringExtra("contactNumber");
                    profileImage = data.getStringExtra("profileImage");
                    rcpGender = data.getStringExtra("rcpGender");

                    inputValueAddName.setText(Html.fromHtml("<font color='#00796B'> " + contactName + "</font><br/>" + contactNumber));

                    Glide.with(activity)
                            .load(profileImage)
                            .placeholder(R.drawable.home_screen_profile)
                            .error(R.drawable.home_screen_profile)
                            .bitmapTransform(new CropCircleTransformation(activity))
                            .override(512, 512)
                            .into(imageProfile);

                    imgClear.setVisibility(View.VISIBLE);
                    familyRelationList.clear();

                    getRCPExistingRelation();

                } else {
                    Utils.showErrorSnackBar(this, relativeRootNewRelation, "You didn't select any RContact!");
                }
            } else {
                Utils.showErrorSnackBar(this, relativeRootNewRelation, "You didn't select any RContact!");
            }
        }
    }

    private void clearBusinessData() {

        // clear business relation
        orgPosition = -1;
        businessRelationPosition = -1;

        organizationId = -1;
        organizationName = "";
        businessRelationName = "";

        imgBusinessClear.setVisibility(View.GONE);
        inputValueBusiness.setText(organizationName);

    }

    private void clearFamilyData() {

        // clear family relation
        isFamilyAlreadyAdded = false;

        familyRelation = "";
        familyRelationId = -1;
        familyRelationPosition = -1;

        imgFamilyClear.setVisibility(View.GONE);
        inputValueFamily.setText(familyRelation);
        inputValueFamily.setFocusable(false);
        inputValueFamily.setEnabled(true);
    }

    private void clearFriendData() {

        // clear friend relation
        friendRelation = "";
        inputValueFriend.setText(friendRelation);
        checkboxFriend.setVisibility(View.VISIBLE);
        inputValueFriend.setVisibility(View.GONE);
        imgFriendClear.setVisibility(View.GONE);
    }

    private void getIntentDetails(Intent intent) {

        if (intent != null) {

//            if (intent.hasExtra(AppConstants.EXTRA_EXISTING_RELATION_DETAILS)) {
//                recommendationType = (RelationRecommendationType) getIntent().getSerializableExtra
//                        (AppConstants.EXTRA_EXISTING_RELATION_DETAILS);
//            } else {
//                recommendationType = null;
//            }

            if (intent.hasExtra(AppConstants.EXTRA_IS_FROM)) {
                isFrom = getIntent().getStringExtra(AppConstants.EXTRA_IS_FROM);
            } else {
                isFrom = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_FAMILY_RELATION)) {
                familyRelation = getIntent().getStringExtra(AppConstants.EXTRA_FAMILY_RELATION);
            } else {
                familyRelation = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_FRIEND_RELATION)) {
                friendRelation = getIntent().getStringExtra(AppConstants.EXTRA_FRIEND_RELATION);
            } else {
                friendRelation = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_PM_ID)) {
                pmId = intent.getStringExtra(AppConstants.EXTRA_PM_ID);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_NAME)) {
                contactName = intent.getStringExtra(AppConstants.EXTRA_CONTACT_NAME);
            }

            if (intent.hasExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL)) {
                profileImage = intent.getStringExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_NUMBER)) {
                contactNumber = intent.getStringExtra(AppConstants.EXTRA_CONTACT_NUMBER);
            }

            if (intent.hasExtra(AppConstants.EXTRA_GENDER)) {
                rcpGender = intent.getStringExtra(AppConstants.EXTRA_GENDER);
            }
        }
    }

    private void setData(ArrayList<ExistingRelationRequest> allExistingRelationList, ArrayList<ExistingRelationRequest> recommendationsRelationList) {

        clearFamilyData();
        clearFriendData();

        arrayList.clear();
        arrayListOrgName.clear();
        arrayListOrgId.clear();

        linearSingleBusinessRelation.setVisibility(View.GONE);
        linearBusinessRelation.setVisibility(View.VISIBLE);
        linearBusinessRelation.removeAllViews();

        if (allExistingRelationList.size() > 0 || recommendationsRelationList.size() > 0) {

            if (allExistingRelationList.size() > 0) {

                ExistingRelationRequest existingRelation = allExistingRelationList.get(0);

                RelationUserProfile relationUserProfile = existingRelation.getRelationUserProfile();

                RelationRecommendationType recommendationType = new RelationRecommendationType();
                recommendationType.setFirstName(relationUserProfile.getPmFirstName());
                recommendationType.setLastName(relationUserProfile.getPmLastName());
                recommendationType.setNumber("+" + relationUserProfile.getMobileNumber());
                recommendationType.setPmId(String.valueOf(allExistingRelationList.get(0).getRrmToPmId()));
                recommendationType.setDateAndTime("");
                recommendationType.setProfileImage(relationUserProfile.getProfilePhoto());
                recommendationType.setGender(relationUserProfile.getPbGender());

                // familyRelation
                ArrayList<RelationResponse> familyRecommendation = existingRelation.getFamilyRelationList();

                if (!Utils.isArraylistNullOrEmpty(familyRecommendation)) {

                    isFamilyAlreadyAdded = true;

                    familyRelationId = familyRecommendation.get(0).getRcRelationMasterId();
                    familyRelation = familyRecommendation.get(0).getRmParticular();
                    inputValueFamily.setText(familyRelation);

                    imgFamilyClear.setVisibility(View.VISIBLE);
                    imgFamilyClear.setImageResource(R.drawable.ico_relation_lock_svg);
                    imgFamilyClear.setEnabled(false);
                    inputValueFamily.setEnabled(false);

                }

                // friendRelation
                ArrayList<RelationResponse> friendRecommendation = existingRelation.getFriendRelationList();

                if (!Utils.isArraylistNullOrEmpty(friendRecommendation)) {

                    friendRelation = "Friend";
                    inputValueFriend.setText(friendRelation);

                    imgFriendClear.setVisibility(View.VISIBLE);
                    imgFriendClear.setImageResource(R.drawable.ico_relation_lock_svg);
                    imgFriendClear.setEnabled(false);
                    inputValueFriend.setEnabled(false);
                    checkboxFriend.setVisibility(View.GONE);
                    inputValueFriend.setVisibility(View.VISIBLE);
                }

                // businessRelation
                ArrayList<RelationResponse> businessRelation = existingRelation.getBusinessRelationList();

                if (!Utils.isArraylistNullOrEmpty(businessRelation)) {

                    for (int j = 0; j < businessRelation.size(); j++) {

//                        organizationName = businessRelation.get(j).getOrganization().getRmParticular();

                        IndividualRelationType relationType = new IndividualRelationType();
                        relationType.setRelationId(String.valueOf(businessRelation.get(j).getRcRelationMasterId()));
                        relationType.setRelationName(businessRelation.get(j).getRmParticular());
                        relationType.setOrganizationName(businessRelation.get(j).getOrgName());
                        relationType.setIsOrgVerified(MoreObjects.firstNonNull(businessRelation
                                .get(j).getOmIsVerified(), 0));
                        relationType.setOrganizationId(String.valueOf(businessRelation.get(j).getRcOrgId()));
//                        relationType.setIsVerify("1");
                        relationType.setIsInUse("1");
                        arrayList.add(relationType);

                        arrayListOrgName.add(businessRelation.get(j).getOrgName());
                        arrayListOrgId.add(String.valueOf(businessRelation.get(j).getRcOrgId()));
                    }
                }
            }

            if (recommendationsRelationList.size() > 0) {

                ExistingRelationRequest recommendationsRelation = recommendationsRelationList.get(0);

                // businessRelation
                ArrayList<RelationResponse> businessRecommendationsRelation = recommendationsRelation.getBusinessRelationList();

                if (!Utils.isArraylistNullOrEmpty(businessRecommendationsRelation)) {

                    for (int j = 0; j < businessRecommendationsRelation.size(); j++) {

//                        organizationName = businessRecommendationsRelation.get(j).getOrganization().getRmParticular();

                        IndividualRelationType relationType = new IndividualRelationType();
                        relationType.setRelationId(String.valueOf(businessRecommendationsRelation.get(j).getRcRelationMasterId()));
                        relationType.setRelationName(businessRecommendationsRelation.get(j).getRmParticular());
                        relationType.setOrganizationName(businessRecommendationsRelation.get(j).getOrgName());
                        relationType.setIsOrgVerified(businessRecommendationsRelation.get(j).getOmIsVerified());
                        relationType.setOrganizationId(String.valueOf(businessRecommendationsRelation.get(j).getRcOrgId()));
//                        relationType.setIsVerify("1");
                        relationType.setIsInUse("1");
                        arrayList.add(relationType);

                        arrayListOrgName.add(businessRecommendationsRelation.get(j).getOrgName());
                        arrayListOrgId.add(String.valueOf(businessRecommendationsRelation.get(j).getRcOrgId()));
                    }
                }

                // familyRelation
                ArrayList<RelationResponse> familyRecommendation = recommendationsRelation.getFamilyRelationList();

                if (!Utils.isArraylistNullOrEmpty(familyRecommendation)) {

                    isFamilyAlreadyAdded = true;

                    familyRelationId = familyRecommendation.get(0).getRcRelationMasterId();
                    familyRelation = familyRecommendation.get(0).getRmParticular();
                    inputValueFamily.setText(familyRelation);

                    imgFamilyClear.setVisibility(View.VISIBLE);
                    imgFamilyClear.setImageResource(R.drawable.ico_relation_lock_svg);
                    imgFamilyClear.setEnabled(false);
                    inputValueFamily.setEnabled(false);
                }
//                else {
//                    clearFamilyData();
//                }

                // friendRelation
                ArrayList<RelationResponse> friendRecommendation = recommendationsRelation.getFriendRelationList();

                if (!Utils.isArraylistNullOrEmpty(friendRecommendation)) {

                    friendRelation = "Friend";
                    inputValueFriend.setText(friendRelation);

                    imgFriendClear.setVisibility(View.VISIBLE);
                    imgFriendClear.setImageResource(R.drawable.ico_relation_lock_svg);
                    imgFriendClear.setEnabled(false);
                    inputValueFriend.setEnabled(false);
                    checkboxFriend.setVisibility(View.GONE);
                    inputValueFriend.setVisibility(View.VISIBLE);
                }
//                else {
//                    clearFriendData();
//                }
            }

            businessRelationDetails();

            Glide.with(activity)
                    .load(profileImage)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .override(512, 512)
                    .into(imageProfile);

        } else {

            linearSingleBusinessRelation.setVisibility(View.VISIBLE);
            linearBusinessRelation.setVisibility(View.GONE);

            clearBusinessData();
        }
    }

    private void businessRelationDetails() {

        if (arrayList.size() == 0) {
            addBusinessRelationView(0, null);
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                addBusinessRelationView(i, arrayList.get(i));
            }
            addBusinessRelationView(arrayList.size(), null);
        }
    }

    @SuppressLint("InflateParams")
    private void addBusinessRelationView(int position, Object detailObject) {

        View view = LayoutInflater.from(this).inflate(R.layout.list_item_business_relation_list, null);
        ImageView imageViewDelete = view.findViewById(R.id.img_business_clear);
        imageViewDelete.setVisibility(View.GONE);

        final EditText inputValueBusiness = view.findViewById(R.id.input_value_business);
        final TextView textRelationName = view.findViewById(R.id.text_relation_name);
        final TextView textOrganizationName = view.findViewById(R.id.text_organization_name);
        final TextView textOrganizationId = view.findViewById(R.id.text_organization_id);
        final TextView textInUse = view.findViewById(R.id.text_in_use);
        final TextView textIsOrgVerify = view.findViewById(R.id.text_org_verify);
        final TextView textRelationId = view.findViewById(R.id.text_relation_id);

        final LinearLayout linearRowBusinessRelationItem = view.findViewById(R.id
                .linear_row_business_relation_item);

        imageViewDelete.setTag(AppConstants.RELATION);
        inputValueBusiness.setHint(R.string.choose_relation);
        inputValueBusiness.setTypeface(Utils.typefaceIcons(activity));
        inputValueBusiness.setInputType(InputType.TYPE_CLASS_TEXT);
        inputValueBusiness.setFocusable(false);

        if (detailObject != null) {

            IndividualRelationType relationType = (IndividualRelationType) detailObject;

            imageViewDelete.setVisibility(View.VISIBLE);
            inputValueBusiness.setEnabled(false);

            if (relationType.getIsOrgVerified() == 1) {

//                String s = relationType.getRelationName() + " at " + relationType.getOrganizationName();
//                inputValueBusiness.setText(Utils.setMultipleTypeface(activity, s + " " + activity.getString(R.string.im_icon_unverify),
//                        0, (StringUtils.length(s) + 1), ((StringUtils.length(s) + 1) + 1)));
                inputValueBusiness.setText(String.format("%s at %s", relationType.getRelationName(), relationType.getOrganizationName()));
                inputValueBusiness.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_relation_single_tick_green_svg, 0);

            } else {
                inputValueBusiness.setText(String.format("%s at %s", relationType.getRelationName(),
                        relationType.getOrganizationName()));
            }
            textRelationName.setText(relationType.getRelationName());
            textOrganizationName.setText(relationType.getOrganizationName());
            textOrganizationId.setText(relationType.getOrganizationId());
            textInUse.setText(relationType.getIsInUse());
            textIsOrgVerify.setText(String.valueOf(relationType.getIsOrgVerified()));
            textRelationId.setText(relationType.getRelationId());
            linearRowBusinessRelationItem.setTag(relationType.getRelationId());

            if (relationType.getIsInUse().equalsIgnoreCase("1")) {
                imageViewDelete.setEnabled(false);
                imageViewDelete.setImageResource(R.drawable.ico_relation_lock_svg);
            } else {
                imageViewDelete.setEnabled(true);
                imageViewDelete.setImageResource(R.drawable.close_vector);
            }
        }

        inputValueBusiness.setTag(position);
        inputValueBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (arrayListOrganization.size() > 0) {

                    if (StringUtils.isEmpty(contactName)) {
                        Utils.showErrorSnackBar(activity, relativeRootNewRelation,
                                "Please select User to establish relation!!");
                    } else {
                        businessPosition = (int) view.getTag();
                        dialogBusinessRelation();
                    }
                } else {
                    startActivityIntent(activity, EditProfileActivity.class, null);
                }
            }
        });

        imageViewDelete.setTag(position);
        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView textInUse = linearRowBusinessRelationItem.findViewById(R.id.text_in_use);
                TextView textOrganizationName = linearRowBusinessRelationItem.findViewById(R.id.text_organization_name);
                TextView textOrganizationId = linearRowBusinessRelationItem.findViewById(R.id.text_organization_id);

                if (linearBusinessRelation.getChildCount() > 1) {
                    if (!textInUse.getText().toString().trim().equalsIgnoreCase("1")) {
                        linearBusinessRelation.removeView(linearRowBusinessRelationItem);

                        arrayListOrgName.remove(textOrganizationName.getText().toString().trim());
                        arrayListOrgId.remove(textOrganizationId.getText().toString().trim());

                        addBusinessDetailsToList();

                        linearBusinessRelation.removeAllViews();
                        businessRelationDetails();
                    }
                } else if (linearBusinessRelation.getChildCount() == 1) {
                    if (!textInUse.getText().toString().trim().equalsIgnoreCase("1"))
                        inputValueBusiness.getText().clear();
                }
            }
        });
        //</editor-fold>

        linearBusinessRelation.addView(view);
    }

    private void addBusinessDetailsToList() {

        arrayList.clear();

        for (int i = 0; i < linearBusinessRelation.getChildCount() - 1; i++) {

            IndividualRelationType relationType = new IndividualRelationType();
            View linearBusiness = linearBusinessRelation.getChildAt(i);

            final EditText inputValueBusiness = linearBusiness.findViewById(R.id.input_value_business);
            final TextView textRelationName = linearBusiness.findViewById(R.id.text_relation_name);
            final TextView textOrganizationName = linearBusiness.findViewById(R.id.text_organization_name);
            final TextView textOrganizationId = linearBusiness.findViewById(R.id.text_organization_id);
            final TextView textInUse = linearBusiness.findViewById(R.id.text_in_use);
            final TextView textIsOrgVerify = linearBusiness.findViewById(R.id.text_org_verify);
            final TextView textRelationId = linearBusiness.findViewById(R.id.text_relation_id);

            relationType.setRelationName(textRelationName.getText().toString().trim());
            relationType.setOrganizationId(textOrganizationId.getText().toString().trim());
            relationType.setOrganizationName(textOrganizationName.getText().toString().trim());

            if (StringUtils.length(textIsOrgVerify.getText().toString()) > 0) {
                relationType.setIsOrgVerified(Integer.parseInt(textIsOrgVerify.getText().toString().trim()));
            } else {
                relationType.setIsOrgVerified(0);
            }

//            if (StringUtils.length(textVerify.getText().toString()) > 0) {
//                relationType.setIsVerify(textVerify.getText().toString().trim());
//            } else {
//                relationType.setIsVerify("0");
//            }

            if (StringUtils.length(textInUse.getText().toString()) > 0) {
                relationType.setIsInUse(textInUse.getText().toString().trim());
            } else {
                relationType.setIsInUse("0");
            }

            if (StringUtils.length(textRelationId.getText().toString()) > 0) {
                relationType.setRelationId(textRelationId.getText().toString().trim());
            } else {
                relationType.setRelationId("0");
            }

            arrayList.add(relationType);
        }

        for (int j = 0; j < arrayListOrganization.size(); j++) {

            ProfileDataOperationOrganization organization = new ProfileDataOperationOrganization();
            organization.setOrgId(arrayListOrganization.get(j).getOrgId());
            organization.setOrgName(arrayListOrganization.get(j).getOrgName());
            organization.setOrgJobTitle(arrayListOrganization.get(j).getOrgJobTitle());
            organization.setOrgIndustryType(arrayListOrganization.get(j).getOrgIndustryType());
            organization.setOrgEntId(arrayListOrganization.get(j).getOrgEntId());
            organization.setOrgLogo(arrayListOrganization.get(j).getOrgLogo());
            organization.setOrgFromDate(arrayListOrganization.get(j).getOrgFromDate());
            organization.setOrgToDate(arrayListOrganization.get(j).getOrgToDate());
            organization.setIsVerify(arrayListOrganization.get(j).getIsVerify());
            organization.setOrgRcpType(arrayListOrganization.get(j).getOrgRcpType());
//            organization.setIsInUse("0");
            arrayListOrganization.set(j, organization);
        }
    }

    private void getOrganizationsList() {

        TableOrganizationMaster tableOrganizationMaster = new TableOrganizationMaster
                (databaseHandler);

        arrayListOrganization = tableOrganizationMaster
                .getAllOrganizationsFromPmId(Integer.parseInt(getUserPmId()));

        if (!(arrayListOrganization.size() > 0)) {
            inputValueBusiness.setText(R.string.str_hint_add_organization);
            inputValueBusiness.setTextColor(colorPineGreen);
        }
    }

    private void getGender() {

        TableProfileMaster tableProfileMaster = new TableProfileMaster
                (databaseHandler);

        strGender = tableProfileMaster
                .getUserGender(Integer.parseInt(getUserPmId()));

        if (StringUtils.isEmpty(strGender)) {
            inputValueFamily.setText(R.string.str_hint_add_family);
            inputValueFamily.setTextColor(colorPineGreen);
        } else {
            inputValueFamily.setFocusable(false);
            inputValueFamily.setText("");
            inputValueFamily.setTextColor(colorPineGreen);
        }
    }

    private void dialogBusinessRelation() {

        businessRelationPosition = -1;
        orgPosition = -1;
        businessRelationName = "";
        businessRelationId = -1;

        ArrayList<Relation> relationList = tableRelationMaster.getRelation(3);

        businessRelationDialog = new Dialog(this);
        businessRelationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        businessRelationDialog.setContentView(R.layout.dialog_all_organization);
        businessRelationDialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(businessRelationDialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        businessRelationDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = businessRelationDialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.business_relation));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = businessRelationDialog.findViewById(R.id.button_right);
        Button buttonLeft = businessRelationDialog.findViewById(R.id.button_left);
        RippleView rippleRight = businessRelationDialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = businessRelationDialog.findViewById(R.id.ripple_left);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.str_next);
        buttonLeft.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setText(R.string.str_cancel);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                if (businessRelationName.equalsIgnoreCase("")) {
                    Utils.showErrorSnackBar(activity, relativeRootNewRelation,
                            "Please select any Business Relation!");
                } else {
                    businessRelationDialog.dismiss();
                    showAllOrganizations();
                }
            }
        });

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                businessRelationDialog.dismiss();
            }
        });

        RecyclerView recyclerViewDialogList = businessRelationDialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        BusinessRelationListAdapter adapter = new BusinessRelationListAdapter(this, relationList,
                new BusinessRelationListAdapter.OnClickListener() {
                    @Override
                    public void onClick(String relationName, int id) {
                        businessRelationName = relationName;
                        businessRelationId = id;
                    }
                }, "business");

        recyclerViewDialogList.setAdapter(adapter);

        businessRelationDialog.show();
    }

    private void showAllOrganizations() {

        organizationId = -1;
        organizationName = "";
        isSelectedOrgVerified = false;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_all_organization);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = dialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.title_all_organizations));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = dialog.findViewById(R.id.button_right);
        Button buttonLeft = dialog.findViewById(R.id.button_left);
        RippleView rippleRight = dialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = dialog.findViewById(R.id.ripple_left);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.str_done);
        buttonLeft.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setText(R.string.str_back);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                if (organizationName.equalsIgnoreCase("")) {
                    Utils.showErrorSnackBar(activity, relativeRootNewRelation,
                            "Please select any Organization!");
                } else {

                    if (businessRelationDialog != null && businessRelationDialog.isShowing())
                        businessRelationDialog.dismiss();
                    dialog.dismiss();

                    if (linearBusinessRelation.getVisibility() == View.VISIBLE) {

                        IndividualRelationType relationType = new IndividualRelationType();

                        relationType.setRelationName(businessRelationName);
                        relationType.setOrganizationName(organizationName);

                        if (businessPosition == arrayList.size()) {

                            relationType.setRelationId(String.valueOf(businessRelationId));
                            relationType.setIsInUse("0");

                            if (isSelectedOrgVerified) {
                                relationType.setIsOrgVerified(1);
                            } else {
                                relationType.setIsOrgVerified(0);
                            }
                            relationType.setOrganizationId(String.valueOf(organizationId));
                            arrayList.add(relationType);

                            isAdd = true;

                        } else {

                            IndividualRelationType relationType1 = arrayList.
                                    get(businessPosition);

                            relationType.setRelationId(relationType1.getRelationId());
                            relationType.setOrganizationId(String.valueOf(organizationId));
                            relationType.setIsInUse(relationType1.getIsInUse());
                            relationType.setIsOrgVerified(relationType1.getIsOrgVerified());
                            arrayList.set(businessPosition, relationType);

                            isAdd = false;
                        }

                        if (!arrayListOrgName.contains(organizationName)) {
                            arrayListOrgName.add(organizationName);
                            arrayListOrgId.add(String.valueOf(organizationId));
                        }

                        linearBusinessRelation.removeAllViews();

                        for (int i = 0; i < arrayList.size(); i++) {
                            addBusinessRelationView(i, arrayList.get(i));
                        }

                        if (isAdd)
                            addBusinessRelationView(arrayList.size(), null);

                    } else {

                        inputValueBusiness.setText(String.format("%s at %s", businessRelationName,
                                organizationName));
                        imgBusinessClear.setVisibility(View.VISIBLE);

                        if (isSelectedOrgVerified) {
                            inputValueBusiness.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_relation_single_tick_green_svg, 0);
                        }
                    }
                }
            }
        });

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
                dialogBusinessRelation();
            }
        });

        RecyclerView recyclerViewDialogList = dialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        OrganizationRelationListAdapter adapter = new OrganizationRelationListAdapter(this,
                arrayListOrganization, arrayListOrgName, arrayListOrgId,
                new OrganizationRelationListAdapter.OnClickListener() {
                    @Override
                    public void onClick(String orgId, String orgName, boolean isOrgVerified) {

                        organizationId = (int) Long.parseLong(orgId);
                        organizationName = orgName;
                        isSelectedOrgVerified = isOrgVerified;
                    }
                }, businessRelationName);

        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    private void dialogFamilyRelation() {

        familyRelationPosition = -1;
        familyRelation = "";
        familyRelationId = -1;

//        ArrayList<Relation> familyRelationList = tableRelationMaster.getRelation(2);

        final Dialog familyDialog = new Dialog(this);
        familyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        familyDialog.setContentView(R.layout.dialog_all_organization);
        familyDialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(familyDialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        familyDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = familyDialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.family_relation));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = familyDialog.findViewById(R.id.button_right);
        Button buttonLeft = familyDialog.findViewById(R.id.button_left);
        RippleView rippleRight = familyDialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = familyDialog.findViewById(R.id.ripple_left);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.str_done);
        buttonLeft.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setText(R.string.str_cancel);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                familyDialog.dismiss();

                isFamilyAlreadyAdded = false;
                inputValueFamily.setText(familyRelation);
                imgFamilyClear.setVisibility(View.VISIBLE);
            }
        });

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                familyDialog.dismiss();
            }
        });

        RecyclerView recyclerViewDialogList = familyDialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        BusinessRelationListAdapter adapter = new BusinessRelationListAdapter(this, familyRelationList,
                new BusinessRelationListAdapter.OnClickListener() {
                    @Override
                    public void onClick(String relationName, int id) {
                        familyRelation = relationName;
                        familyRelationId = id;
                    }

                }, "family");

        recyclerViewDialogList.setAdapter(adapter);

        familyDialog.show();
    }

    private void sendRelationRequest(ArrayList<RelationRequest> relationRequest) {

        WsRequestObject sendRelationRequestObject = new WsRequestObject();
        sendRelationRequestObject.setArrayListRelationRequest(relationRequest);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    sendRelationRequestObject, null, WsResponseObject.class, WsConstants
                    .REQ_SEND_RELATION_REQUEST, getResources().getString(R.string.msg_please_wait),
                    true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    BuildConfig.WS_ROOT + WsConstants.REQ_SEND_RELATION_REQUEST);
        } else {
            Utils.showErrorSnackBar(this, relativeRootNewRelation, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void getRCPExistingRelation() {

        if (Utils.isNetworkAvailable(this)) {
            new AsyncGetWebServiceCall(this, WsResponseObject.class, WsConstants
                    .REQ_GET_RELATION, getResources().getString(R.string.msg_please_wait))
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                            WsConstants.REQ_GET_RELATION + "?startAt=0&user_id=" + pmId);
        } else {
            Utils.showErrorSnackBar(this, relativeRootNewRelation, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void getFamilyRelation() {

        if (Utils.isNetworkAvailable(this)) {
            new AsyncGetWebServiceCall(this, WsResponseObject.class, WsConstants
                    .REQ_GET_RELATION_MASTER, getResources().getString(R.string.msg_please_wait))
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                            WsConstants.REQ_GET_RELATION_MASTER + "?rm_type=2&user_id=" + pmId + "&rm_gender=" + rcpGender.toLowerCase());
        } else {
            Utils.showErrorSnackBar(this, relativeRootNewRelation, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

//    private void storeProfileDataToDb(ArrayList<RelationRequest> relationRequestResponse) {
//
//        //<editor-fold desc="Relation Mapping Master">
//        TableRelationMappingMaster tableRelationMappingMaster = new
//                TableRelationMappingMaster(databaseHandler);
//
//        if (!Utils.isArraylistNullOrEmpty(relationRequestResponse)) {
//
//            ArrayList<RelationRequestResponse> relationResponseList = new ArrayList<>();
//
//            for (int i = 0; i < relationRequestResponse.size(); i++) {
//                RelationRequestResponse relationResponse = new RelationRequestResponse();
//
//                relationResponse.setId(relationRequestResponse.get(i).getId());
//                relationResponse.setRcRelationMasterId(relationRequestResponse.get(i).getRcRelationMasterId());
//                relationResponse.setRrmToPmId(relationRequestResponse.get(i).getRrmToPmId());
//                relationResponse.setRrmType(relationRequestResponse.get(i).getRrmType());
//                relationResponse.setRrmFromPmId(relationRequestResponse.get(i).getRrmFromPmId());
//                relationResponse.setRcStatus(relationRequestResponse.get(i).getRcStatus());
//                relationResponse.setRcOrgId(relationRequestResponse.get(i).getRcOrgId());
//                relationResponse.setCreatedAt(relationRequestResponse.get(i).getCreatedAt());
//
//                relationResponseList.add(relationResponse);
//            }
//
//            tableRelationMappingMaster.addRelationMapping(relationResponseList);
//        }
//    }
}
