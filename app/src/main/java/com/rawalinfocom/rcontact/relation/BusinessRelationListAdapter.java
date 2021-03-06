package com.rawalinfocom.rcontact.relation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.Relation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class BusinessRelationListAdapter extends RecyclerView.Adapter<BusinessRelationListAdapter
        .OrganizationViewHolder> {

    private Context context;
    private ArrayList<Relation> arrayListBusinessRelation;
    private String type;

    private OnClickListener clickListener;

    public interface OnClickListener {
        void onClick(String relationName, int id);
    }

    public BusinessRelationListAdapter(Context context, ArrayList<Relation>
            arrayListBusinessRelation, OnClickListener clickListener, String type) {
        this.context = context;
        this.type = type;
        this.clickListener = clickListener;
        this.arrayListBusinessRelation = arrayListBusinessRelation;
    }

    @Override
    public OrganizationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_organization_list_relation, parent, false);
        return new OrganizationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrganizationViewHolder holder, int position) {

        Relation relation = arrayListBusinessRelation.get(position);

        holder.textMain.setText(relation.getRmRelationName());

        if (type.equalsIgnoreCase("family")) {
            holder.checkbox.setChecked(position == (AddNewRelationActivity.familyRelationPosition));

            if (position == (AddNewRelationActivity.familyRelationPosition)) {
                if (clickListener != null) {
                    clickListener.onClick(relation.getRmRelationName(), relation.getRmId());
                }
            }

        } else {
            holder.checkbox.setChecked(position == (AddNewRelationActivity.businessRelationPosition));

            if (position == (AddNewRelationActivity.businessRelationPosition)) {
                if (clickListener != null) {
                    clickListener.onClick(relation.getRmRelationName(), relation.getRmId());
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayListBusinessRelation.size();
    }

    class OrganizationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_main)
        TextView textMain;
        @BindView(R.id.checkbox)
        RadioButton checkbox;

        OrganizationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textMain.setTypeface(Utils.typefaceRegular(context));

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (type.equalsIgnoreCase("family")) {
                        AddNewRelationActivity.familyRelationPosition = getAdapterPosition();
                    } else {
                        AddNewRelationActivity.businessRelationPosition = getAdapterPosition();
                    }
                    notifyDataSetChanged();

                }
            });
        }
    }
}
