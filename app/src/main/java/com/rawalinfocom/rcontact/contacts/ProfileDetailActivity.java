package com.rawalinfocom.rcontact.contacts;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.OrganizationListAdapter;
import com.rawalinfocom.rcontact.adapters.ProfileDetailAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.QueryManager;
import com.rawalinfocom.rcontact.database.TableContactRatingMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.DbRating;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.Rating;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileDetailActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    private final String TAG_IMAGE_EDIT = "tag_edit";
    private final String TAG_IMAGE_FAVOURITE = "tag_favourite";
    private final String TAG_IMAGE_UN_FAVOURITE = "tag_un_favourite";

    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    TextView textToolbarTitle;
    RippleView rippleActionBack;
    RippleView rippleActionRightLeft;
    RippleView rippleActionRightCenter;
    RippleView rippleActionRightRight;
    ImageView imageRightLeft;

    /* @BindView(R.id.text_joining_date)
     TextView textJoiningDate;*/
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.text_user_rating)
    TextView textUserRating;
    @BindView(R.id.linear_basic_detail_rating)
    LinearLayout linearBasicDetailRating;
    @BindView(R.id.text_name)
    TextView textName;
    /*  @BindView(R.id.text_cloud_name)
      TextView textCloudName;*/
    @BindView(R.id.text_full_screen_text)
    TextView textFullScreenText;
    @BindView(R.id.text_designation)
    TextView textDesignation;
    @BindView(R.id.text_organization)
    TextView textOrganization;
    @BindView(R.id.text_view_all_organization)
    TextView textViewAllOrganization;
    @BindView(R.id.linear_basic_detail)
    LinearLayout linearBasicDetail;
    @BindView(R.id.relative_basic_detail)
    RelativeLayout relativeBasicDetail;
    @BindView(R.id.image_call)
    ImageView imageCall;
    @BindView(R.id.text_label_phone)
    TextView textLabelPhone;
    @BindView(R.id.recycler_view_contact_number)
    RecyclerView recyclerViewContactNumber;
    @BindView(R.id.linear_phone)
    LinearLayout linearPhone;
    @BindView(R.id.image_email)
    ImageView imageEmail;
    @BindView(R.id.text_label_email)
    TextView textLabelEmail;
    @BindView(R.id.recycler_view_email)
    RecyclerView recyclerViewEmail;
    @BindView(R.id.linear_email)
    LinearLayout linearEmail;
    @BindView(R.id.image_website)
    ImageView imageWebsite;
    @BindView(R.id.text_label_website)
    TextView textLabelWebsite;
    @BindView(R.id.recycler_view_website)
    RecyclerView recyclerViewWebsite;
    @BindView(R.id.linear_website)
    LinearLayout linearWebsite;
    @BindView(R.id.image_address)
    ImageView imageAddress;
    @BindView(R.id.text_label_address)
    TextView textLabelAddress;
    @BindView(R.id.recycler_view_address)
    RecyclerView recyclerViewAddress;
    @BindView(R.id.linear_address)
    LinearLayout linearAddress;
    @BindView(R.id.image_social_contact)
    ImageView imageSocialContact;
    @BindView(R.id.text_label_social_contact)
    TextView textLabelSocialContact;
    @BindView(R.id.recycler_view_social_contact)
    RecyclerView recyclerViewSocialContact;
    @BindView(R.id.linear_social_contact)
    LinearLayout linearSocialContact;
    @BindView(R.id.card_contact_details)
    CardView cardContactDetails;
    @BindView(R.id.image_event)
    ImageView imageEvent;
    @BindView(R.id.text_label_event)
    TextView textLabelEvent;
    @BindView(R.id.recycler_view_event)
    RecyclerView recyclerViewEvent;
    @BindView(R.id.linear_event)
    LinearLayout linearEvent;
    @BindView(R.id.image_gender)
    ImageView imageGender;
    @BindView(R.id.text_label_gender)
    TextView textLabelGender;
    @BindView(R.id.image_icon_gender)
    ImageView imageIconGender;
    @BindView(R.id.text_gender)
    TextView textGender;
    @BindView(R.id.linear_gender)
    LinearLayout linearGender;
    @BindView(R.id.card_other_details)
    CardView cardOtherDetails;
    @BindView(R.id.button_view_more)
    Button buttonViewMore;
    @BindView(R.id.ripple_view_more)
    RippleView rippleViewMore;
    @BindView(R.id.relative_section_view_more)
    RelativeLayout relativeSectionViewMore;
    @BindView(R.id.relative_root_profile_detail)
    RelativeLayout relativeRootProfileDetail;
    @BindView(R.id.linear_organization_detail)
    LinearLayout linearOrganizationDetail;
    @BindView(R.id.rating_user)
    RatingBar ratingUser;
    @BindView(R.id.linear_call_sms)
    LinearLayout linearCallSms;
    @BindView(R.id.button_call_log)
    Button buttonCallLog;
    @BindView(R.id.button_sms)
    Button buttonSms;

    RelativeLayout relativeRootRatingDialog;

    String pmId, phoneBookId, contactName = "", cloudContactName = null, checkNumberFavourite =
            null;
    boolean displayOwnProfile = false, isHideFavourite = false;

    PhoneBookContacts phoneBookContacts;
    int listClickedPosition = -1;

    ProfileDetailAdapter phoneDetailAdapter;
    MaterialDialog callConfirmationDialog;

    ArrayList<String> arrayListFavouriteContacts;
    RContactApplication rContactApplication;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        rContactApplication = (RContactApplication) getApplicationContext();
        ButterKnife.bind(this);

        phoneBookContacts = new PhoneBookContacts(this);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(AppConstants.EXTRA_PM_ID)) {
                pmId = intent.getStringExtra(AppConstants.EXTRA_PM_ID);
            } else {
                pmId = "-1";
            }

            if (intent.hasExtra(AppConstants.EXTRA_PHONE_BOOK_ID)) {
                phoneBookId = intent.getStringExtra(AppConstants.EXTRA_PHONE_BOOK_ID);
            } else {
                phoneBookId = "-1";
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_NAME)) {
                contactName = intent.getStringExtra(AppConstants.EXTRA_CONTACT_NAME);
            } else {
                contactName = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME)) {
                cloudContactName = intent.getStringExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME);
                cloudContactName = StringUtils.substring(cloudContactName, 2, cloudContactName
                        .length() - 1);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CHECK_NUMBER_FAVOURITE)) {
                isHideFavourite = true;
                checkNumberFavourite = intent.getStringExtra(AppConstants
                        .EXTRA_CHECK_NUMBER_FAVOURITE);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_POSITION)) {
                listClickedPosition = intent.getIntExtra(AppConstants.EXTRA_CONTACT_POSITION, -1);
            }
        }

        if (pmId.equalsIgnoreCase(getUserPmId())) {
            displayOwnProfile = true;
        }

        arrayListFavouriteContacts = new ArrayList<>();
        if (!Utils.isArraylistNullOrEmpty(Utils.getArrayListPreference(this, AppConstants
                .PREF_FAVOURITE_CONTACT_NUMBER_EMAIL))) {
            arrayListFavouriteContacts.addAll(Utils.getArrayListPreference(this, AppConstants
                    .PREF_FAVOURITE_CONTACT_NUMBER_EMAIL));
        }

        init();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_view_more:
                if (relativeSectionViewMore.getVisibility() == View.VISIBLE) {
                    relativeSectionViewMore.setVisibility(View.GONE);
                    buttonViewMore.setText("View More");
                } else {
                    relativeSectionViewMore.setVisibility(View.VISIBLE);
                    buttonViewMore.setText("View Less");
                }
                break;

            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_action_right_left:
                if (StringUtils.equals(imageRightLeft.getTag().toString(), TAG_IMAGE_FAVOURITE)
                        || StringUtils.equals(imageRightLeft.getTag().toString(),
                        TAG_IMAGE_UN_FAVOURITE)) {
                    int favStatus;
                    if (StringUtils.equals(imageRightLeft.getTag().toString(),
                            TAG_IMAGE_FAVOURITE)) {
                        favStatus = PhoneBookContacts.status_un_favourite;
                        imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
                        imageRightLeft.setTag(TAG_IMAGE_UN_FAVOURITE);
                    } else {
                        favStatus = PhoneBookContacts.status_favourite;
                        imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
                        imageRightLeft.setTag(TAG_IMAGE_FAVOURITE);
                    }
                    int updateStatus = phoneBookContacts.setFavouriteStatus(phoneBookId, favStatus);
                    if (updateStatus != 1) {
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, "Error while " +
                                "updating favourite status!");
                    }
                    ArrayList<ProfileData> arrayListFavourites = new ArrayList<>();
                    ProfileData favouriteStatus = new ProfileData();
                    favouriteStatus.setLocalPhoneBookId(phoneBookId);
                    favouriteStatus.setIsFavourite(String.valueOf(favStatus));
                    arrayListFavourites.add(favouriteStatus);
                    setFavouriteStatus(arrayListFavourites);

                    rContactApplication.setFavouriteModified(true);

                }
                break;
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_GET_PROFILE_DETAIL">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_PROFILE_DETAIL)) {
                WsResponseObject profileDetailResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (profileDetailResponse != null && StringUtils.equalsIgnoreCase
                        (profileDetailResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    final ProfileDataOperation profileDetail = profileDetailResponse
                            .getProfileDetail();
                    setUpView(profileDetail);

                } else {
                    if (profileDetailResponse != null) {
                        Log.e("error response", profileDetailResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_MARK_AS_FAVOURITE">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_MARK_AS_FAVOURITE)) {
                WsResponseObject favouriteStatusResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (favouriteStatusResponse == null || !StringUtils.equalsIgnoreCase
                        (favouriteStatusResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    if (favouriteStatusResponse != null) {
                        Log.e("error response", favouriteStatusResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_PROFILE_RATING">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_PROFILE_RATING)) {
                WsResponseObject profileRatingResponse = (WsResponseObject) data;
                if (profileRatingResponse != null && StringUtils.equalsIgnoreCase
                        (profileRatingResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Utils.showSuccessSnackBar(this, relativeRootProfileDetail, "Rating Submitted");

                    if (profileRatingResponse.getProfileRating() != null) {

                        DbRating dbRating = new DbRating();
                        Rating responseRating = profileRatingResponse.getProfileRating();
                        dbRating.setRcProfileMasterPmId(String.valueOf(responseRating.getPrToPmId
                                ()));
                        dbRating.setCrmStatus(String.valueOf(responseRating.getPrStatus()));
                        dbRating.setCrmRating(responseRating.getPrRatingStars());
                        dbRating.setCrmCloudPrId(String.valueOf(responseRating.getPrId()));
                        dbRating.setCrmComment(responseRating.getPrComment());
                        dbRating.setCrmCreatedAt(responseRating.getCreatedAt());

                        TableContactRatingMaster tableContactRatingMaster = new
                                TableContactRatingMaster(databaseHandler);
                        tableContactRatingMaster.addRating(dbRating);

                        TableProfileMaster tableProfileMaster = new TableProfileMaster
                                (databaseHandler);
                        tableProfileMaster.updateUserProfileRating(pmId, responseRating
                                .getProfileRating(), responseRating.getTotalProfileRateUser());

                        ratingUser.setRating(Float.parseFloat(responseRating.getProfileRating()));
                        textUserRating.setText(responseRating.getTotalProfileRateUser());
                    }
                } else {
                    if (profileRatingResponse != null) {
                        Log.e("error response", profileRatingResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, "" + error
                    .getLocalizedMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.linear_basic_detail_rating)
    public void onRatingClick() {

        if (!StringUtils.equalsIgnoreCase(pmId, "-1") && !displayOwnProfile) {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_submit_rating);
            dialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

            TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
            relativeRootRatingDialog = (RelativeLayout) dialog.findViewById(R.id
                    .relative_root_rating_dialog);
            final RatingBar ratingUser = (RatingBar) dialog.findViewById(R.id.rating_user);
            TextView textComment = (TextView) dialog.findViewById(R.id.text_comment);
            final TextView textRemainingCharacters = (TextView) dialog.findViewById(R.id
                    .text_remaining_characters);
            final EditText inputComment = (EditText) dialog.findViewById(R.id.input_comment);
            RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);
            Button buttonLeft = (Button) dialog.findViewById(R.id.button_left);
            RippleView rippleRight = (RippleView) dialog.findViewById(R.id.ripple_right);
            Button buttonRight = (Button) dialog.findViewById(R.id.button_right);

            textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));
            textComment.setTypeface(Utils.typefaceRegular(this));
            textRemainingCharacters.setTypeface(Utils.typefaceLight(this));
            inputComment.setTypeface(Utils.typefaceRegular(this));
            buttonLeft.setTypeface(Utils.typefaceSemiBold(this));
            buttonRight.setTypeface(Utils.typefaceSemiBold(this));

            textDialogTitle.setText("Rate " + contactName);
            textRemainingCharacters.setText(getResources().getInteger(R.integer
                    .max_comment_length) + " characters left");

            buttonRight.setText(R.string.action_submit);
            buttonLeft.setText(R.string.action_cancel);

            rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    dialog.dismiss();
                }
            });

            rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    if (ratingUser.getRating() > 0) {
                        submitRating(String.valueOf(ratingUser.getRating()), inputComment.getText
                                ().toString());
                        dialog.dismiss();
                    } else {
                        Utils.showErrorSnackBar(ProfileDetailActivity.this,
                                relativeRootRatingDialog, "Please fill appropriate stars!");
                    }
                }
            });

            inputComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    int characters = getResources().getInteger(R.integer.max_comment_length) -
                            charSequence.toString().length();
                    textRemainingCharacters.setText(characters + (characters == 1 ? " character" :
                            " characters" + " left"));
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            dialog.show();

        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        imageRightLeft = ButterKnife.findById(includeToolbar, R.id.image_right_left);
        rippleActionRightLeft = ButterKnife.findById(includeToolbar, R.id.ripple_action_right_left);
        rippleActionRightCenter = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_center);
        rippleActionRightRight = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_right);

        recyclerViewContactNumber.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmail.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWebsite.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAddress.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEvent.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSocialContact.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewContactNumber.setNestedScrollingEnabled(false);
        recyclerViewEmail.setNestedScrollingEnabled(false);
        recyclerViewWebsite.setNestedScrollingEnabled(false);
        recyclerViewAddress.setNestedScrollingEnabled(false);
        recyclerViewEvent.setNestedScrollingEnabled(false);
        recyclerViewSocialContact.setNestedScrollingEnabled(false);

        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
//        textJoiningDate.setTypeface(Utils.typefaceRegular(this));
        textFullScreenText.setTypeface(Utils.typefaceSemiBold(this));
        textName.setTypeface(Utils.typefaceSemiBold(this));
        textDesignation.setTypeface(Utils.typefaceRegular(this));
        textOrganization.setTypeface(Utils.typefaceRegular(this));
        textViewAllOrganization.setTypeface(Utils.typefaceRegular(this));
        textUserRating.setTypeface(Utils.typefaceRegular(this));

        textFullScreenText.setSelected(true);

        rippleViewMore.setOnRippleCompleteListener(this);
        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionRightLeft.setOnRippleCompleteListener(this);

        if (!StringUtils.equalsIgnoreCase(pmId, "-1")) {
            // RC Profile
//            getProfileDetail();
            if (displayOwnProfile) {
                ProfileDataOperation profileDataOperation = (ProfileDataOperation) Utils
                        .getObjectPreference(this, AppConstants.PREF_REGS_USER_OBJECT,
                                ProfileDataOperation.class);
                setUpView(profileDataOperation);
            } else {
//                TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
                QueryManager queryManager = new QueryManager(databaseHandler);
                ProfileDataOperation profileDataOperation = queryManager.getRcProfileDetail
                        (this, pmId);
                setUpView(profileDataOperation);
            }
        } else {
            // Non-RC Profile
//            textJoiningDate.setVisibility(View.GONE);
            setUpView(null);
        }

//        textName.setText(contactName);
        textFullScreenText.setText(contactName);
        if (StringUtils.length(cloudContactName) > 0) {
            textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
            textName.setText(cloudContactName);
            /*textCloudName.setText(cloudContactName);
            textName.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));*/
        } else {
            if (StringUtils.equalsIgnoreCase(pmId, "-1")) {
//                textName.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
            } else {
//                textName.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            }
//            textCloudName.setVisibility(View.GONE);
            textName.setVisibility(View.GONE);
        }

        if (displayOwnProfile) {
            textToolbarTitle.setText(getString(R.string.title_my_profile));
            linearCallSms.setVisibility(View.GONE);
            imageRightLeft.setImageResource(R.drawable.ic_action_edit);
            imageRightLeft.setTag(TAG_IMAGE_EDIT);
        } else {
            textToolbarTitle.setText("Profile Detail");
            linearCallSms.setVisibility(View.VISIBLE);
        }

        if (isHideFavourite) {
            rippleActionRightLeft.setEnabled(false);
            if (checkNumberFavourite != null && arrayListFavouriteContacts.contains
                    (checkNumberFavourite)) {
                imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
            } else {
                imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
            }
        }

        initSwipe();

    }


    private void setUpView(final ProfileDataOperation profileDetail) {

        //<editor-fold desc="Favourite">

        if (!displayOwnProfile && !isHideFavourite) {

            int isFavourite = 0;
            Cursor contactFavouriteCursor = phoneBookContacts.getStarredStatus(phoneBookId);

            if (contactFavouriteCursor != null && contactFavouriteCursor.getCount() > 0) {
                while (contactFavouriteCursor.moveToNext()) {
                    isFavourite = contactFavouriteCursor.getInt(contactFavouriteCursor
                            .getColumnIndex(ContactsContract.Contacts.STARRED));
                }
                contactFavouriteCursor.close();
            }

            if (isFavourite == 0) {
                imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
                imageRightLeft.setTag(TAG_IMAGE_UN_FAVOURITE);
            } else {
                imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
                imageRightLeft.setTag(TAG_IMAGE_FAVOURITE);
            }
        }

        //</editor-fold>

        //<editor-fold desc="Joining Date">
       /* if (profileDetail != null) {
            String joiningDate = StringUtils.defaultString(Utils.convertDateFormat(profileDetail
                    .getJoiningDate(), "yyyy-MM-dd HH:mm:ss", "dd'th' MMM, yyyy"), "-");
            textJoiningDate.setText("Joining Date:- " + joiningDate);
        }*/
        //</editor-fold>

        //<editor-fold desc="User Name">
        /*if (profileDetail != null) {
            textName.setText(profileDetail.getPbNameFirst() + " " + profileDetail.getPbNameLast());
        }*/
        //</editor-fold>

        //<editor-fold desc="Organization Detail">

        // From Cloud
        ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new ArrayList<>();

        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail
                .getPbOrganization())) {
            arrayListOrganization.addAll(profileDetail.getPbOrganization());
        }

        // From PhoneBook
        Cursor contactOrganizationCursor = phoneBookContacts.getContactOrganization(phoneBookId);
        ArrayList<ProfileDataOperationOrganization> arrayListPhoneBookOrganization = new
                ArrayList<>();

        if (contactOrganizationCursor != null && contactOrganizationCursor.getCount() > 0) {
            while (contactOrganizationCursor.moveToNext()) {

                ProfileDataOperationOrganization organization = new
                        ProfileDataOperationOrganization();

                organization.setOrgName(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.COMPANY)));
                organization.setOrgJobTitle(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.TITLE)));
                organization.setOrgDepartment(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.DEPARTMENT)));
                organization.setOrgType(phoneBookContacts.getOrganizationType
                        (contactOrganizationCursor,
                                contactOrganizationCursor.getInt((contactOrganizationCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .Organization.TYPE)))));
                organization.setOrgJobDescription(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.JOB_DESCRIPTION)));
                organization.setOrgOfficeLocation(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.OFFICE_LOCATION)));
                organization.setOrgRcpType(String.valueOf(getResources().getInteger(R.integer
                        .rcp_type_local_phone_book)));

                if (!arrayListOrganization.contains(organization)) {
                    arrayListPhoneBookOrganization.add(organization);
                }

            }
            contactOrganizationCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListOrganization) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookOrganization)) {

            final ArrayList<ProfileDataOperationOrganization> tempOrganization = new ArrayList<>();
            tempOrganization.addAll(arrayListOrganization);
            tempOrganization.addAll(arrayListPhoneBookOrganization);

            if (tempOrganization.size() == 1) {
                textViewAllOrganization.setVisibility(View.GONE);
            } else {
                textViewAllOrganization.setVisibility(View.VISIBLE);
            }
            textDesignation.setText(tempOrganization.get(0).getOrgJobTitle());
            textOrganization.setText(tempOrganization.get(0).getOrgName());

            textViewAllOrganization.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAllOrganizations(tempOrganization);
                }
            });

        } else {
            linearOrganizationDetail.setVisibility(View.INVISIBLE);
        }
        //</editor-fold>

        //<editor-fold desc="User Rating">
        if (profileDetail != null) {
            textUserRating.setText(profileDetail.getTotalProfileRateUser());
            ratingUser.setRating(Float.parseFloat(profileDetail.getProfileRating()));
        } else {
            textUserRating.setText("0");
            ratingUser.setRating(0);
            ratingUser.setEnabled(false);
        }
        //</editor-fold>

        //<editor-fold desc="Phone Number">

        // From Cloud
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();
        ArrayList<String> arrayListCloudNumber = new ArrayList<>();

        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbPhoneNumber
                ())) {
            arrayListPhoneNumber.addAll(profileDetail.getPbPhoneNumber());
            for (int i = 0; i < arrayListPhoneNumber.size(); i++) {
                String number = Utils.getFormattedNumber(this, arrayListPhoneNumber.get(i)
                        .getPhoneNumber());
                arrayListCloudNumber.add(number);
            }
        }

        // From PhoneBook
        Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(phoneBookId);
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneBookNumber = new ArrayList<>();

        if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
            while (contactNumberCursor.moveToNext()) {

                ProfileDataOperationPhoneNumber phoneNumber = new
                        ProfileDataOperationPhoneNumber();

                phoneNumber.setPhoneNumber(Utils.getFormattedNumber(this, contactNumberCursor
                        .getString(contactNumberCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Phone.NUMBER))));
                phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                        (contactNumberCursor.getInt(contactNumberCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.TYPE))));

                if (!arrayListCloudNumber.contains(phoneNumber.getPhoneNumber())) {
                    arrayListPhoneBookNumber.add(phoneNumber);
                }

            }
            contactNumberCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListPhoneNumber) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookNumber)) {
            ArrayList<Object> tempPhoneNumber = new ArrayList<>();
            tempPhoneNumber.addAll(arrayListPhoneNumber);
            tempPhoneNumber.addAll(arrayListPhoneBookNumber);

            linearPhone.setVisibility(View.VISIBLE);
            phoneDetailAdapter = new ProfileDetailAdapter(this,
                    tempPhoneNumber, AppConstants.PHONE_NUMBER);
            recyclerViewContactNumber.setAdapter(phoneDetailAdapter);
        } else {
            linearPhone.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Email Id">

        // From Cloud
        ArrayList<ProfileDataOperationEmail> arrayListEmail = new ArrayList<>();
        ArrayList<String> arrayListCloudEmail = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbEmailId())) {
            arrayListEmail.addAll(profileDetail.getPbEmailId());
            for (int i = 0; i < arrayListEmail.size(); i++) {
                String email = arrayListEmail.get(i).getEmEmailId();
                arrayListCloudEmail.add(email);
            }
        }

        // From PhoneBook
        Cursor contactEmailCursor = phoneBookContacts.getContactEmail(phoneBookId);
        ArrayList<ProfileDataOperationEmail> arrayListPhoneBookEmail = new ArrayList<>();

        if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
            while (contactEmailCursor.moveToNext()) {

                ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();

                emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                emailId.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                        contactEmailCursor.getInt
                                (contactEmailCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Email.TYPE))));

                if (!arrayListCloudEmail.contains(emailId.getEmEmailId())) {
                    arrayListPhoneBookEmail.add(emailId);
                }

            }
            contactEmailCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListEmail) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookEmail)) {
            ArrayList<Object> tempEmail = new ArrayList<>();
            tempEmail.addAll(arrayListEmail);
            tempEmail.addAll(arrayListPhoneBookEmail);
            linearEmail.setVisibility(View.VISIBLE);
            ProfileDetailAdapter emailDetailAdapter = new ProfileDetailAdapter(this, tempEmail,
                    AppConstants.EMAIL);
            recyclerViewEmail.setAdapter(emailDetailAdapter);
        } else {
            linearEmail.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Website">

        // From Cloud
        ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = new ArrayList<>();
        ArrayList<String> arrayListCloudWebsite = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress
                ())) {
            arrayListWebsite.addAll(profileDetail.getPbWebAddress());
            for (int i = 0; i < arrayListWebsite.size(); i++) {
                String website = arrayListWebsite.get(i).getWebAddress();
                arrayListCloudWebsite.add(website);
            }
        }
       /* ArrayList<String> arrayListWebsite = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress
                ())) {
            arrayListWebsite.addAll(profileDetail.getPbWebAddress());
        }*/

        // From PhoneBook
        Cursor contactWebsiteCursor = phoneBookContacts.getContactWebsite(phoneBookId);
        ArrayList<ProfileDataOperationWebAddress> arrayListPhoneBookWebsite = new ArrayList<>();

        if (contactWebsiteCursor != null && contactWebsiteCursor.getCount() > 0) {
            while (contactWebsiteCursor.moveToNext()) {

                ProfileDataOperationWebAddress webAddress = new ProfileDataOperationWebAddress();

                webAddress.setWebAddress(contactWebsiteCursor.getString(contactWebsiteCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                webAddress.setWebRcpType(String.valueOf(getResources().getInteger(R.integer
                        .rcp_type_local_phone_book)));

                if (!arrayListCloudWebsite.contains(webAddress.getWebAddress())) {
                    arrayListPhoneBookWebsite.add(webAddress);
                }

            }
            contactWebsiteCursor.close();
        }
       /* Cursor contactWebsiteCursor = phoneBookContacts.getContactWebsite(phoneBookId);
        ArrayList<String> arrayListPhoneBookWebsite = new ArrayList<>();

        if (contactWebsiteCursor != null && contactWebsiteCursor.getCount() > 0) {
            while (contactWebsiteCursor.moveToNext()) {

                String website = contactWebsiteCursor.getString(contactWebsiteCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                website = website + ";" + getResources().getInteger(R.integer
                        .rcp_type_local_phone_book);

                if (!arrayListWebsite.contains(website)) {
                    arrayListPhoneBookWebsite.add(website);
                }

            }
            contactWebsiteCursor.close();
        }*/

        if (!Utils.isArraylistNullOrEmpty(arrayListWebsite) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookWebsite)) {
            ArrayList<Object> tempWebsite = new ArrayList<>();
            tempWebsite.addAll(arrayListWebsite);
            tempWebsite.addAll(arrayListPhoneBookWebsite);

            linearWebsite.setVisibility(View.VISIBLE);
            ProfileDetailAdapter websiteDetailAdapter = new ProfileDetailAdapter(this,
                    tempWebsite, AppConstants.WEBSITE);
            recyclerViewWebsite.setAdapter(websiteDetailAdapter);
        } else {
            linearWebsite.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Address">

        // From Cloud
        ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();
        ArrayList<String> arrayListCloudAddress = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbAddress())) {
            arrayListAddress.addAll(profileDetail.getPbAddress());
            for (int i = 0; i < arrayListAddress.size(); i++) {
                String address = arrayListAddress.get(i).getFormattedAddress();
                arrayListCloudAddress.add(address);
            }
        }

        // From PhoneBook
        Cursor contactAddressCursor = phoneBookContacts.getContactAddress(phoneBookId);
        ArrayList<ProfileDataOperationAddress> arrayListPhoneBookAddress = new ArrayList<>();

        if (contactAddressCursor != null && contactAddressCursor.getCount() > 0) {
            while (contactAddressCursor.moveToNext()) {

                ProfileDataOperationAddress address = new ProfileDataOperationAddress();

                address.setFormattedAddress(contactAddressCursor.getString
                        (contactAddressCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                address.setCity(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .CITY)));
                address.setCountry(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .COUNTRY)));
                address.setNeighborhood(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .NEIGHBORHOOD)));
                address.setPostCode(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POSTCODE)));
                address.setPoBox(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POBOX)));
                address.setStreet(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .STREET)));
                address.setAddressType(phoneBookContacts.getAddressType(contactAddressCursor,
                        contactAddressCursor.getInt(contactAddressCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.StructuredPostal.TYPE))));
                address.setRcpType(String.valueOf(getResources().getInteger(R.integer
                        .rcp_type_local_phone_book)));

                if (!arrayListCloudAddress.contains(address.getFormattedAddress())) {
                    arrayListPhoneBookAddress.add(address);
                }

            }
            contactAddressCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListAddress) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookAddress)) {
            ArrayList<Object> tempAddress = new ArrayList<>();
            tempAddress.addAll(arrayListAddress);
            tempAddress.addAll(arrayListPhoneBookAddress);
            linearAddress.setVisibility(View.VISIBLE);
            ProfileDetailAdapter addressDetailAdapter = new ProfileDetailAdapter(this,
                    tempAddress, AppConstants.ADDRESS);
            recyclerViewAddress.setAdapter(addressDetailAdapter);
        } else {
            linearAddress.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Im Account">

        // From Cloud
        ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();
        ArrayList<String> arrayListCloudImAccount = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbIMAccounts
                ())) {
            arrayListImAccount.addAll(profileDetail.getPbIMAccounts());
            for (int i = 0; i < arrayListImAccount.size(); i++) {
                String imAccount = arrayListImAccount.get(i).getIMAccountProtocol();
                arrayListCloudImAccount.add(imAccount);
            }
        }

        // From PhoneBook
        Cursor contactImAccountCursor = phoneBookContacts.getContactIm(phoneBookId);
        ArrayList<ProfileDataOperationImAccount> arrayListPhoneBookImAccount = new ArrayList<>();

        if (contactImAccountCursor != null && contactImAccountCursor.getCount() > 0) {
            while (contactImAccountCursor.moveToNext()) {

                ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();

                imAccount.setIMAccountDetails(contactImAccountCursor.getString
                        (contactImAccountCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA1)));

                imAccount.setIMAccountType(phoneBookContacts.getImAccountType
                        (contactImAccountCursor,
                                contactImAccountCursor.getInt(contactImAccountCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Im.TYPE))));

                imAccount.setIMAccountProtocol(phoneBookContacts.getImProtocol
                        (contactImAccountCursor.getInt((contactImAccountCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Im.PROTOCOL)))));

                imAccount.setIMRcpType(String.valueOf(getResources().getInteger(R.integer
                        .rcp_type_local_phone_book)));

                if (!arrayListCloudImAccount.contains(imAccount.getIMAccountProtocol())) {
                    arrayListPhoneBookImAccount.add(imAccount);
                }

            }
            contactImAccountCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListImAccount) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookImAccount)) {
            ArrayList<Object> tempImAccount = new ArrayList<>();
            tempImAccount.addAll(arrayListImAccount);
            tempImAccount.addAll(arrayListPhoneBookImAccount);
            linearSocialContact.setVisibility(View.VISIBLE);
            ProfileDetailAdapter imAccountDetailAdapter = new ProfileDetailAdapter(this,
                    tempImAccount, AppConstants.IM_ACCOUNT);
            recyclerViewSocialContact.setAdapter(imAccountDetailAdapter);
        } else {
            linearSocialContact.setVisibility(View.GONE);
        }
        //</editor-fold>

        if ((!Utils.isArraylistNullOrEmpty(arrayListWebsite) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookWebsite))
                ||
                (!Utils.isArraylistNullOrEmpty(arrayListAddress) || !Utils
                        .isArraylistNullOrEmpty(arrayListPhoneBookAddress))
                ||
                (!Utils.isArraylistNullOrEmpty(arrayListImAccount) || !Utils
                        .isArraylistNullOrEmpty(arrayListPhoneBookImAccount))
                ) {
            rippleViewMore.setVisibility(View.VISIBLE);
        } else {
            rippleViewMore.setVisibility(View.GONE);
        }

        // <editor-fold desc="Event">

        // From Cloud
        ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbEvent())) {
            arrayListEvent.addAll(profileDetail.getPbEvent());
        }

        // From PhoneBook
        Cursor contactEventCursor = phoneBookContacts.getContactEvent(phoneBookId);
        ArrayList<ProfileDataOperationEvent> arrayListPhoneBookEvent = new ArrayList<>();

        if (contactEventCursor != null && contactEventCursor.getCount() > 0) {
            while (contactEventCursor.moveToNext()) {

                ProfileDataOperationEvent event = new ProfileDataOperationEvent();

                event.setEventType(phoneBookContacts.getEventType(contactEventCursor,
                        contactEventCursor
                                .getInt(contactEventCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Event.TYPE))));

                event.setEventDate(contactEventCursor.getString(contactEventCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                .START_DATE)));

                event.setEventRcType(String.valueOf(getResources().getInteger(R.integer
                        .rcp_type_local_phone_book)));

                if (!arrayListEvent.contains(event)) {
                    arrayListPhoneBookEvent.add(event);
                }

            }
            contactEventCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListEvent) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookEvent)) {
            ArrayList<Object> tempEvent = new ArrayList<>();
            tempEvent.addAll(arrayListEvent);
            tempEvent.addAll(arrayListPhoneBookEvent);
            linearEvent.setVisibility(View.VISIBLE);
            ProfileDetailAdapter eventDetailAdapter = new ProfileDetailAdapter(this, tempEvent,
                    AppConstants.EVENT);
            recyclerViewEvent.setAdapter(eventDetailAdapter);
        } else {
            linearEvent.setVisibility(View.GONE);
        }
        //</editor-fold>

        linearGender.setVisibility(View.GONE);

        if (Utils.isArraylistNullOrEmpty(arrayListEvent) && Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookEvent)
//                && Utils.isArraylistNullOrEmpty(arrayListAddress)
                ) {
            cardOtherDetails.setVisibility(View.GONE);
        } else {
            cardOtherDetails.setVisibility(View.VISIBLE);
        }

    }


    //    private void showAllOrganizations(ProfileDataOperation profileDetail) {
    private void showAllOrganizations(ArrayList<ProfileDataOperationOrganization>
                                              arraylistOrganization) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_all_organization);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.title_all_organizations));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = (Button) dialog.findViewById(R.id.button_right);
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.ripple_right);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.action_close);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        RecyclerView recyclerViewDialogList = (RecyclerView) dialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        /*OrganizationListAdapter adapter = new OrganizationListAdapter(this, profileDetail
                .getPbOrganization());*/
        OrganizationListAdapter adapter = new OrganizationListAdapter(this, arraylistOrganization);
        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String actionNumber = StringUtils.defaultString(((ProfileDetailAdapter
                        .ProfileDetailViewHolder) viewHolder).textMain.getText()
                        .toString());
                if (direction == ItemTouchHelper.LEFT) {
                    /* SMS */
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                  /*  smsIntent.setData(Uri.parse("sms:" + ((ProfileData)
                            arrayListPhoneBookContacts.get(position)).getOperation().get(0)
                            .getPbPhoneNumber().get(0).getPhoneNumber()));*/
                    smsIntent.setData(Uri.parse("sms:" + actionNumber));
                    startActivity(smsIntent);

                } else {
                    showCallConfirmationDialog(actionNumber);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (phoneDetailAdapter != null) {
                            phoneDetailAdapter.notifyDataSetChanged();
                        }
                    }
                }, 1500);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                Paint p = new Paint();
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(ContextCompat.getColor(ProfileDetailActivity.this, R.color
                                .darkModerateLimeGreen));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView
                                .getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable
                                .ic_action_call);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float)
                                itemView.getTop() + width, (float) itemView.getLeft() + 2 *
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(ContextCompat.getColor(ProfileDetailActivity.this, R.color
                                .brightOrange));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float)
                                itemView.getTop(), (float) itemView.getRight(), (float) itemView
                                .getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable
                                .ic_action_sms);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width,
                                (float) itemView.getTop() + width, (float) itemView.getRight() -
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewContactNumber);
    }

    private void showCallConfirmationDialog(final String number) {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        callConfirmationDialog.dismissDialog();
                        break;

                    case R.id.rippleRight:
                        callConfirmationDialog.dismissDialog();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                number));
                        startActivity(intent);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(this, cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Call");
        callConfirmationDialog.setDialogBody("Call " + number + "?");

        callConfirmationDialog.showDialog();

    }

    public RelativeLayout getRelativeRootProfileDetail() {
        return relativeRootProfileDetail;
    }

    public int getListClickedPosition() {
        return listClickedPosition;
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void getProfileDetail() {

        WsRequestObject profileDetailObject = new WsRequestObject();
        profileDetailObject.setPmId(pmId);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    profileDetailObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_PROFILE_DETAIL, getString(R.string.msg_please_wait), true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_GET_PROFILE_DETAIL);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    private void setFavouriteStatus(ArrayList<ProfileData> favourites) {

        WsRequestObject favouriteStatusObject = new WsRequestObject();
        favouriteStatusObject.setPmId(getUserPmId());
        favouriteStatusObject.setFavourites(favourites);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    favouriteStatusObject, null, WsResponseObject.class, WsConstants
                    .REQ_MARK_AS_FAVOURITE, null, true).execute(WsConstants.WS_ROOT + WsConstants
                    .REQ_MARK_AS_FAVOURITE);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    private void submitRating(String ratingStar, String comment) {

/*       ArrayList<Rating> ratings = new ArrayList<>();
        Rating rating = new Rating();
        rating.setPrToPmId(Integer.valueOf(pmId));
        rating.setPrRatingStars(ratingStar);
        rating.setPrComment(comment);
        rating.setPrStatus(getResources().getInteger(R.integer.rating_done));
        ratings.add(rating);*/

        WsRequestObject ratingObject = new WsRequestObject();
        ratingObject.setPmId(getUserPmId());
        ratingObject.setPrComment(comment);
        ratingObject.setPrRatingStars(ratingStar);
        ratingObject.setPrStatus(String.valueOf(getResources().getInteger(R.integer.rating_done)));
        ratingObject.setPrToPmId(pmId);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    ratingObject, null, WsResponseObject.class, WsConstants.REQ_PROFILE_RATING,
                    null, true).execute(WsConstants.WS_ROOT + WsConstants.REQ_PROFILE_RATING);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    //</editor-fold>
}
