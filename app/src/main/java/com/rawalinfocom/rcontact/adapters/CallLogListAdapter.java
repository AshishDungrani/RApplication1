package com.rawalinfocom.rcontact.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.calllog.CallLogFragment;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.ProfileData;

import java.lang.annotation.ElementType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 15/02/17.
 */

public class CallLogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SectionIndexer {


    private final int HEADER = 0, CALL_LOGS = 1;
    private Context context;
    private Fragment fragment;
    /* phone book contacts */
    private ArrayList<Object> arrayListCallLogs;
    private ArrayList<String> arrayListCallLogHeader;
    private ArrayList<CallLogType> arrayListCallLoghistroy;
    private int previousPosition = 0;




    //<editor-fold desc="Constructor">
    public CallLogListAdapter(Fragment fragment, ArrayList<Object> arrayListCallLogs,
                                 ArrayList<String> arrayListCallLogHeader/*, ArrayList<CallLogType> listCallLogHistroy*/) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.arrayListCallLogs = arrayListCallLogs;
        this.arrayListCallLogHeader = arrayListCallLogHeader;
//        this.arrayListCallLoghistroy =  listCallLogHistroy;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case HEADER:
                View v1 = inflater.inflate(R.layout.list_item_call_log_header , parent, false);
                viewHolder = new CallLogListAdapter.CallLogHeaderViewHolder(v1);
                break;
            case CALL_LOGS:
                View v2 = inflater.inflate(R.layout.list_item_call_log_list, parent, false);
                viewHolder = new CallLogListAdapter.AllCallLogViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case HEADER:
                CallLogListAdapter.CallLogHeaderViewHolder contactHeaderViewHolder = (CallLogListAdapter.CallLogHeaderViewHolder) holder;
                configureHeaderViewHolder(contactHeaderViewHolder, position);
                break;
            case CALL_LOGS:
                CallLogListAdapter.AllCallLogViewHolder contactViewHolder = (CallLogListAdapter.AllCallLogViewHolder) holder;
                configureAllContactViewHolder(contactViewHolder, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayListCallLogs.get(position) instanceof CallLogType) {
            return CALL_LOGS;
        } else if (arrayListCallLogs.get(position) instanceof String) {
            return HEADER;
        }
        return -1;
    }

    @Override
    public int getItemCount() {

        return arrayListCallLogs.size();
    }

    @Override
    public Object[] getSections() {
        return arrayListCallLogHeader.toArray(new String[arrayListCallLogHeader.size()]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position >= arrayListCallLogs.size()) {
            position = arrayListCallLogs.size() - 1;
        }

        if (arrayListCallLogs.get(position) instanceof String) {
            String letter = (String) arrayListCallLogs.get(position);
            previousPosition = arrayListCallLogHeader.indexOf(letter);

        } else {
            /*for (int i = position; i < arrayListUserContact.size(); i++) {
                if (arrayListUserContact.get(i) instanceof String) {
                    String letter = (String) arrayListUserContact.get(i);
                    previousPosition = arrayListContactHeader.indexOf(letter);
                    break;
                }
            }*/
            for (int i = position; i >= 0; i--) {
                if (arrayListCallLogs.get(i) instanceof String) {
                    String letter = (String) arrayListCallLogs.get(i);
                    previousPosition = arrayListCallLogHeader.indexOf(letter);
                    break;
                }
            }
        }

        return previousPosition;
    }

    private void configureAllContactViewHolder(final AllCallLogViewHolder holder, final int
            position) {

        CallLogType callLogType = (CallLogType) arrayListCallLogs.get(position);
        final String name =  callLogType.getContactName();
        if(!TextUtils.isEmpty(name))
        {
            holder.textContactName.setTypeface(Utils.typefaceBold(context));
            holder.textContactName.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));
            Pattern numberPat = Pattern.compile("\\d+");
            Matcher matcher1 = numberPat.matcher(name);
            if(matcher1.find()){
                String number = Utils.getFormattedNumber(context,name);
                holder.textContactName.setText(number);
            }else {
                holder.textContactName.setText(name);
            }
        }
        long date = callLogType.getDate();
        if(date>0){
            Date date1 = new Date(date);
            String logDate = new SimpleDateFormat("MMMM dd, hh:mm a").format(date1);
            holder.textContactDate.setText(logDate);
        }
        int callType =  callLogType.getType();
        if(callType>0){
            switch (callType) {
                case CallLogFragment.INCOMING:
                    holder.imageCallType.setImageResource(R.drawable.ic_call_incoming);
                    break;
                case CallLogFragment.OUTGOING:
                    holder.imageCallType.setImageResource(R.drawable.ic_call_outgoing);
                    break;
                case CallLogFragment.MISSED:
                    holder.imageCallType.setImageResource(R.drawable.ic_call_missed);
                    break;
                default:
                    break;

            }
        }

        int logCount =  callLogType.getHistroyLogCount();
        Log.i("Histroy Adapter count",logCount+"" + " at position "+ position + " of number " + name );
        if(logCount > 0){
            holder.textCount.setText("("+logCount+""+")");
        }else {
            holder.textCount.setText(" ");
        }

        boolean isDual = AppConstants.isDualSimPhone();
        String simNumber = "";
        simNumber =  callLogType.getCallSimNumber();
        if(isDual)
        {
            if(!TextUtils.isEmpty(simNumber)){
                if(simNumber.equalsIgnoreCase("2")){
                    holder.textSimType.setTextColor(ContextCompat.getColor(context,R.color.darkCyan));
                    holder.textSimType.setText(context.getString(R.string.im_sim_2));
                    holder.textSimType.setTypeface(Utils.typefaceIcons(context));
                }else {
                    holder.textSimType.setTextColor(ContextCompat.getColor(context,R.color.vividBlue));
                    holder.textSimType.setText(context.getString(R.string.im_sim_1));
                    holder.textSimType.setTypeface(Utils.typefaceIcons(context));
                }
            }else
            {
                /*holder.textSimType.setTextColor(ContextCompat.getColor(context,R.color.vividBlue));
                holder.textSimType.setText(context.getString(R.string.im_sim_1));
                holder.textSimType.setTypeface(Utils.typefaceIcons(context));*/
                holder.textSimType.setVisibility(View.GONE);
            }

        }else {
          /*  holder.textSimType.setTextColor(ContextCompat.getColor(context,R.color.vividBlue));
            holder.textSimType.setText(context.getString(R.string.im_sim_1));
            holder.textSimType.setTypeface(Utils.typefaceIcons(context));*/
            holder.textSimType.setVisibility(View.GONE);

        }
        holder.text3dotsCallLog.setTypeface(Utils.typefaceIcons(context));
        final String number =  callLogType.getNumber();
        holder.relativeRowMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileDetailActivity.class);
                intent.putExtra(AppConstants.PROFILE_ACTIVITY_CALL_INSTANCE, AppConstants.PROFILE_SHOW_VIEW);
                intent.putExtra(AppConstants.CALL_HISTROY_NUMBER, number);
                intent.putExtra(AppConstants.CALL_HISTROY_NAME, name);
                context.startActivity(intent);
            }
        });

    }

    private void configureHeaderViewHolder(CallLogListAdapter.CallLogHeaderViewHolder holder, int position) {
        String date = (String) arrayListCallLogs.get(position);
        holder.textHeader.setText(date);
    }


    public class AllCallLogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.text_3dots_call_log)
        TextView text3dotsCallLog;
        @BindView(R.id.image_social_media)
        ImageView imageSocialMedia;
        @BindView(R.id.text_contact_name)
        public TextView textContactName;
        @BindView(R.id.text_cloud_contact_name)
        TextView textCloudContactName;
        @BindView(R.id.image_call_type)
        ImageView imageCallType;
        @BindView(R.id.text_contact_date)
        TextView textContactDate;
        @BindView(R.id.text_sim_type)
        TextView textSimType;
        @BindView(R.id.text_rating_user_count)
        TextView textRatingUserCount;
        @BindView(R.id.rating_user)
        RatingBar ratingUser;
        @BindView(R.id.linear_rating)
        LinearLayout linearRating;
        @BindView(R.id.linear_content_main)
        LinearLayout linearContentMain;
        @BindView(R.id.relative_row_main)
        RelativeLayout relativeRowMain;
        @BindView(R.id.textCount)
        TextView textCount;


        public AllCallLogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);


        }
    }


    public class CallLogHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_header)
        TextView textHeader;

        public CallLogHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            textHeader.setTypeface(Utils.typefaceSemiBold(context));

        }
    }



}
