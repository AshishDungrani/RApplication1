package com.rawalinfocom.rcontact.events;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.EventsActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {


    private List<EventItem> list;
    private Context context;
    private int recyclerPosition;

    public EventAdapter(Context context, List<EventItem> list, int recyclerPosition) {
        this.list = list;
        this.context = context;
        this.recyclerPosition = recyclerPosition;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final EventItem item = list.get(position);
        holder.textPersonName.setText(item.getPersonName());
        holder.textEventName.setText(item.getEventName());
        holder.textEventCommentTime.setText(Utils.formatDateTime(item.getCommentTime(), "dd-MM hh:mm a"));
        int notiType = item.getEventType();
        if (notiType == AppConstants.COMMENT_TYPE_BIRTHDAY) {
            holder.textEventDetailInfo.setText(item.getEventDetail() + ", " + item.getEventName() + " on " + Utils.formatDateTime(item.getEventDate(), "EEE, dd MMM"));
        } else if (notiType == AppConstants.COMMENT_TYPE_ANNIVERSARY) {
            holder.textEventDetailInfo.setText(item.getEventDetail() + " on " + Utils.formatDateTime(item.getEventDate(), "EEE, dd MMM"));
        } else {
            holder.textEventDetailInfo.setText(item.getEventName() + " on " + Utils.formatDateTime(item.getEventDate(), "dd-MM-yyyy"));
        }
        if (recyclerPosition != 2) {
            if (!item.isEventCommentPending()) {
                holder.textUserComment.setVisibility(View.VISIBLE);
                holder.textUserComment.setText("You wrote to " + item.getPersonFirstName() + "'s Timeline");
                holder.layoutUserCommentPending.setVisibility(View.GONE);
            } else {
                holder.textUserComment.setVisibility(View.GONE);
                holder.layoutUserCommentPending.setVisibility(View.VISIBLE);
            }
        } else {
            // upcoming events
            holder.layoutUserCommentPending.setVisibility(View.GONE);
            holder.textUserComment.setVisibility(View.GONE);
            holder.textEventCommentTime.setVisibility(View.GONE);
        }
        holder.buttonUserCommentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                "type" : "birthday",
//                        "to_pm_id" : 1,
//                        "comment" : "hello",
//                        "date" : "2017-03-15",
//                        "status" : 1
                String userComment = holder.edittextUserComment.getText().toString();
                if (userComment != null && userComment.length() > 0) {
                    EventsActivity.evmRecordId = item.getEventRecordIndexId();
                    EventsActivity.selectedRecycler = recyclerPosition;
                    EventsActivity.selectedRecyclerItem = position;
                    addEventComment(item.getEventName(), item.getPersonRcpPmId(), userComment, item.getEventDate(), AppConstants.COMMENT_STATUS_SENT);
                } else {
                    Toast.makeText(context, "Please enter some comment first!", Toast.LENGTH_SHORT).show();
                }
                //addEventComment("birthday", 1, "hello", "2017-03-15", 1);
                //Toast.makeText(context, "Button clicked" + position + " :" + item.getPersonName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_person)
        ImageView imagePerson;
        @BindView(R.id.text_person_name)
        TextView textPersonName;
        @BindView(R.id.text_event_name)
        TextView textEventName;
        @BindView(R.id.text_event_comment_time)
        TextView textEventCommentTime;
        @BindView(R.id.text_event_detail_info)
        TextView textEventDetailInfo;
        @BindView(R.id.edittext_user_comment)
        EditText edittextUserComment;
        @BindView(R.id.button_user_comment_submit)
        ImageView buttonUserCommentSubmit;
        @BindView(R.id.layout_user_comment_pending)
        LinearLayout layoutUserCommentPending;
        @BindView(R.id.text_user_comment)
        TextView textUserComment;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            textPersonName.setTypeface(Utils.typefaceRegular(context));

            textEventName.setTypeface(Utils.typefaceRegular(context));
            textEventCommentTime.setTypeface(Utils.typefaceRegular(context));

            textEventDetailInfo.setTypeface(Utils.typefaceRegular(context));

            edittextUserComment.setTypeface(Utils.typefaceRegular(context));
            textUserComment.setTypeface(Utils.typefaceRegular(context));

        }

    }

    private void addEventComment(String eventType, int toPmId, String comment, String date, int status) {

        WsRequestObject addCommentObject = new WsRequestObject();

        addCommentObject.setType(eventType);
        addCommentObject.setToPmId(toPmId);
        addCommentObject.setComment(comment);
        addCommentObject.setDate(date);
        addCommentObject.setStatus(status + "");

        if (Utils.isNetworkAvailable(context)) {
            new AsyncWebServiceCall(context, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    addCommentObject, null, WsResponseObject.class, WsConstants
                    .REQ_ADD_EVENT_COMMENT, "Sending comments..", true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_ADD_EVENT_COMMENT);
        } else {
            //show no toast
            Log.i("MAULIK", "no internet");
        }
    }

    public void updateAdapter() {

    }

}
