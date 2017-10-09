package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.model.IndividualRelationRecommendationType;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hardik on 05/10/17.
 */

class IndividualExistingRelationListAdapter extends RecyclerView.Adapter
        <IndividualExistingRelationListAdapter.IndividualExistingRelationViewHolder> {

    private Activity activity;
    private ArrayList<IndividualRelationRecommendationType> individualRelationRecommendationTypeList;

    @Override
    public IndividualExistingRelationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_existing_relation, parent, false);
        return new IndividualExistingRelationViewHolder(v);
    }

    IndividualExistingRelationListAdapter(Activity activity, ArrayList<IndividualRelationRecommendationType>
            individualRelationRecommendationTypeList) {
        this.activity = activity;
        this.individualRelationRecommendationTypeList = individualRelationRecommendationTypeList;
    }

    @Override
    public void onBindViewHolder(IndividualExistingRelationViewHolder holder, int position) {

        IndividualRelationRecommendationType type = individualRelationRecommendationTypeList.get(position);

        if (!StringUtils.isEmpty(type.getRelationName())) {

            holder.llRelationOrganization.setVisibility(View.VISIBLE);
            holder.textBusinessRelationName.setVisibility(View.VISIBLE);

            holder.textBusinessRelationName.setText(type.getRelationName());
            holder.textBusinessRelationName.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ico_relation_business_svg, 0, R.drawable.ico_relation_double_tick_svg, 0);
            holder.textOrganizationName.setText(type.getOrganizationName());

        } else {

            holder.llRelationOrganization.setVisibility(View.GONE);
            holder.textBusinessRelationName.setVisibility(View.GONE);

//            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.llOtherRelation.getLayoutParams();
//            layoutParams.setMargins(0, 0, 0, 0);
//            holder.llOtherRelation.setLayoutParams(layoutParams);
        }

        if (!StringUtils.isEmpty(type.getFamilyName())) {

            holder.llFamilyRelation.setVisibility(View.VISIBLE);

            holder.textFamilyName.setText(type.getFamilyName());
            holder.textFamilyName.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ico_realtion_family_svg, 0, R.drawable.ico_relation_single_tick_svg, 0);
        } else {
            holder.llFamilyRelation.setVisibility(View.GONE);
        }

        if (type.getIsFriendRelation()) {

            holder.llFriendRelation.setVisibility(View.VISIBLE);

            holder.textFriendName.setText(activity.getString(R.string.str_friend));
            holder.textFriendName.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ico_relation_friend_svg, 0, R.drawable.ico_relation_single_tick_svg, 0);
        } else {
            holder.llFriendRelation.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return individualRelationRecommendationTypeList.size();
    }

    class IndividualExistingRelationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_business_relation_name)
        TextView textBusinessRelationName;
        @BindView(R.id.text_at)
        TextView textAt;
        @BindView(R.id.text_organization_name)
        TextView textOrganizationName;
        @BindView(R.id.ll_relation_organization)
        LinearLayout llRelationOrganization;
        @BindView(R.id.text_family_name)
        TextView textFamilyName;
        @BindView(R.id.ll_family_relation)
        LinearLayout llFamilyRelation;
        @BindView(R.id.text_friend_name)
        TextView textFriendName;
        @BindView(R.id.ll_friend_relation)
        LinearLayout llFriendRelation;
        @BindView(R.id.ll_other_relation)
        LinearLayout llOtherRelation;

        IndividualExistingRelationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            llRelationOrganization.setVisibility(View.GONE);
            textBusinessRelationName.setVisibility(View.GONE);
            llFamilyRelation.setVisibility(View.GONE);
            llFriendRelation.setVisibility(View.GONE);
        }
    }
}
