package tech2.demo.com.demo.custom.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.model.Meals;

/**
 * Created by Joel on 27-Jan-16.
 */
public class MealsAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {
    private List<Meals> mData;
    private SparseBooleanArray mSelectedItemsIds;
    private int mExpectedCalories;

    private LayoutInflater mInflater;
    private Context mContext;

    private int[] mSectionIndices;
    private String[] mSectionDates;
    private List<Integer> mSectionPositions;
    // Counts the total calories for each section
    private Map<Integer, Integer> mSectionsCalories;

    public MealsAdapter(Context context, List<Meals> mData, int mExpectedCalories) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mData = mData;
        this.mContext = context;
        if (mData.size() > 0)
            setSections();
        mSelectedItemsIds = new SparseBooleanArray();
        this.mExpectedCalories = mExpectedCalories;
    }

    private void setSections() {
        ArrayList<Integer> sectionIndices = new ArrayList<>();
        mSectionPositions = new ArrayList<>();
        String lastDate = mData.get(0).getDate();
        sectionIndices.add(0);
        int count = 0;
        mSectionPositions.add(0);
        for (int i = 1; i < mData.size(); i++) {
            if (!lastDate.equals(mData.get(i).getDate())) {
                sectionIndices.add(i);
                lastDate = mData.get(i).getDate();
                count++;
            }
            mSectionPositions.add(count);
        }

        mSectionIndices = new int[sectionIndices.size()];
        mSectionDates = new String[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            mSectionIndices[i] = sectionIndices.get(i);
            mSectionDates[i] = mData.get(sectionIndices.get(i)).getDate();
        }

        setCalories();
    }

    private void setCalories(){
        if(mData.size() != 0) {
            mSectionsCalories = new HashMap<>();
            int countCalories = mData.get(0).getNumberCalories();
            int lastPosition = 0;
            String lastDate = mData.get(0).getDate();
            for (int i = 1; i < mData.size(); i++) {
                if (!lastDate.equals(mData.get(i).getDate())) {
                    lastDate = mData.get(i).getDate();
                    mSectionsCalories.put(lastPosition, countCalories);
                    countCalories = mData.get(i).getNumberCalories();
                    lastPosition = i;
                } else {
                    countCalories += mData.get(i).getNumberCalories();
                }
            }
            mSectionsCalories.put(lastPosition, countCalories);
        }
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public void removeItem(int position) {
        mData.remove(position);
        setCalories();
        mSectionPositions.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Meals getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.listview_meals_row, null);
            holder.description = (TextView) convertView.findViewById(R.id.listview_meals_row_description);
            holder.numberCalories = (TextView) convertView.findViewById(R.id.listview_meals_row_calories);
            holder.time = (TextView) convertView.findViewById(R.id.listview_meals_row_time);
            holder.parent = (LinearLayout) convertView.findViewById(R.id.listview_meals_row_parent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Meals meal = mData.get(position);
        holder.description.setText(meal.getDescription());
        holder.time.setText(meal.getTime());
        String numCal = meal.getNumberCalories() + " " + mContext.getResources().getString(R.string.calories);
        holder.numberCalories.setText(numCal);

        if(mSelectedItemsIds.get(position)){
            holder.parent.setBackgroundColor(mContext.getResources().getColor(R.color.foodie_yellow));
        } else {
            holder.parent.setBackgroundColor(mContext.getResources().getColor(R.color.foodie_yellow_dim));
        }

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.listview_meals_header, null);
            holder.date = (TextView) convertView.findViewById(R.id.listview_meals_header_date);
            holder.calories = (TextView) convertView.findViewById(R.id.listview_meals_header_calories);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        Meals meal = mData.get(position);
        //If it is a header do this
        holder.date.setText(meal.getDate());
        String numCal = mSectionsCalories.get(position) + " " + mContext.getResources().getString(R.string.calories);
        holder.calories.setText(numCal);

        if(mSectionsCalories.get(position) > mExpectedCalories)
            holder.calories.setTextColor(mContext.getResources().getColor(R.color.foodie_red));
        else
            holder.calories.setTextColor(mContext.getResources().getColor(R.color.foodie_green));

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return mSectionPositions.get(position);
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionDates;
    }

    public void restore(List<Meals> mData) {
        this.mData = mData;
        if(mData.size() > 0)
            setSections();
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public TextView description, time, numberCalories;
        public LinearLayout parent;
    }

    public static class HeaderViewHolder {
        public TextView date, calories;
    }
}
