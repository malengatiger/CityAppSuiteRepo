package com.boha.library.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.library.R;
import com.boha.library.dto.ComplaintTypeDTO;

import java.util.List;

/**
 * Created by aubreyM on 14/12/17.
 */
public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ComplaintTypeViewHolder> {

    public interface ComplaintTypeListener {
        void onComplaintTypeClicked(ComplaintTypeDTO complaintType);

    }

    private ComplaintTypeListener mListener;
    private List<ComplaintTypeDTO> complaintTypes;
    private Context ctx;

    public SubCategoryAdapter(List<ComplaintTypeDTO> complaintTypes, Context ctx, ComplaintTypeListener listener) {
        this.complaintTypes = complaintTypes;
        this.mListener = listener;
        this.ctx = ctx;
    }

    @Override
    public ComplaintTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sub_category_item, parent, false);
        return new ComplaintTypeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ComplaintTypeViewHolder holder, final int position) {

        final ComplaintTypeDTO p = complaintTypes.get(position);
        holder.complaintType.setText(p.getComplaintTypeName());
        setIcon(p.getComplaintTypeName(),holder.icon, ctx);


        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onComplaintTypeClicked(p);
            }
        });

    }

    @Override
    public int getItemCount() {
        return complaintTypes == null ? 0 : complaintTypes.size();
    }

    public class ComplaintTypeViewHolder extends RecyclerView.ViewHolder {
        protected TextView complaintType;
        protected ImageView icon;
        protected View main;


        public ComplaintTypeViewHolder(View itemView) {
            super(itemView);
            complaintType = (TextView) itemView.findViewById(R.id.name);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            main =  itemView.findViewById(R.id.main);
        }

    }
    public static void setIcon(String p, ImageView image, Context ctx) {
        if (p.equalsIgnoreCase("Water")) {
            Drawable d = ContextCompat.getDrawable(ctx, R.drawable.zwater);
            d.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("BLUE"), PorterDuff.Mode.MULTIPLY));
            image.setImageDrawable(d);
        }
        if (p.equalsIgnoreCase("Burst Pipe")) {
            Drawable d = ContextCompat.getDrawable(ctx, R.drawable.zburst_pipe);
            d.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("BLUE"), PorterDuff.Mode.MULTIPLY));
            image.setImageDrawable(d);
        }
        if (p.equalsIgnoreCase("Overgrown Area")) {
            Drawable d = ContextCompat.getDrawable(ctx, R.drawable.zovergrown_area);
            d.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("BLACK"), PorterDuff.Mode.MULTIPLY));
            image.setImageDrawable(d);
        }
        if (p.equalsIgnoreCase("Power Failure")) {
            Drawable d = ContextCompat.getDrawable(ctx, R.drawable.zpower_failure);
            d.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("BLACK"), PorterDuff.Mode.MULTIPLY));
            image.setImageDrawable(d);
        }
        if (p.equalsIgnoreCase("Potholes")) {
            Drawable d = ContextCompat.getDrawable(ctx, R.drawable.zpotholes);
            d.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("BROWN"), PorterDuff.Mode.MULTIPLY));
            image.setImageDrawable(d);
        }
        if (p.equalsIgnoreCase("High Bill")) {
            Drawable d = ContextCompat.getDrawable(ctx, R.drawable.zhigh_bill);
            d.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("RED"), PorterDuff.Mode.MULTIPLY));
            image.setImageDrawable(d);
        }
        if (p.equalsIgnoreCase("Traffic Light Management")) {
            Drawable d = ContextCompat.getDrawable(ctx, R.drawable.ztraffic_light_management);
            d.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("BLACK"), PorterDuff.Mode.MULTIPLY));
            image.setImageDrawable(d);
        }
        if (p.equalsIgnoreCase("Blocked Drains")) {
            Drawable d = ContextCompat.getDrawable(ctx, R.drawable.zblocked_drains);
            d.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("BLACK"), PorterDuff.Mode.MULTIPLY));
            image.setImageDrawable(d);
        }
        if (p.equalsIgnoreCase("Manhole Covers")) {
            Drawable d = ContextCompat.getDrawable(ctx, R.drawable.zmanhole_covers);
            d.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("RED"), PorterDuff.Mode.MULTIPLY));
            image.setImageDrawable(d);
        }
        if (p.equalsIgnoreCase("Illegal Dumping")) {
            Drawable d = ContextCompat.getDrawable(ctx, R.drawable.zillegal_dumping);
            d.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("BLACK"), PorterDuff.Mode.MULTIPLY));
            image.setImageDrawable(d);
        }
        if (p.equalsIgnoreCase("Sanitation")) {
            Drawable d = ContextCompat.getDrawable(ctx, R.drawable.zsanitation);
            d.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("BLACK"), PorterDuff.Mode.MULTIPLY));
            image.setImageDrawable(d);
        }
        if (p.equalsIgnoreCase("Nuisance Reporting")) {
            Drawable d = ContextCompat.getDrawable(ctx, R.drawable.znuisance_reporting);
            d.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("BLACK"), PorterDuff.Mode.MULTIPLY));
            image.setImageDrawable(d);
        }
        if (p.equalsIgnoreCase("Uncategorized Complaint")) {
            image.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_action_bell));
        }

    }
    static final String LOG = SubCategoryAdapter.class.getSimpleName();
}