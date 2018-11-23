package tech2.demo.com.demo.custom.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import tech2.demo.com.demo.R;
import tech2.demo.com.demo.model.Users;

/**
 * Created by Joel on 25-Feb-16.
 */
public class UsersAdapter extends BaseAdapter {

    private List<Users> mUsers;
    private LayoutInflater mInflater;
    private SparseBooleanArray mSelectedItemsIds;
    private Context mContext;

    public UsersAdapter(Context mContext, List<Users> mUsers) {
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mUsers = mUsers;
        mSelectedItemsIds = new SparseBooleanArray();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Users getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void removeItem(int position) {
        mUsers.remove(position);
        notifyDataSetChanged();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_admin, null);
            holder.username = (TextView) convertView.findViewById(R.id.listview_admin_username);
            holder.numberMeals = (TextView) convertView.findViewById(R.id.listview_admin_number_meals);
            holder.parent = (RelativeLayout) convertView.findViewById(R.id.listview_admin_parent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Users user = getItem(position);

        holder.username.setText(user.getEmail());
        if (user.getPermissions() > 0) {
            if(user.getPermissions() == 1){
                holder.numberMeals.setText(mContext.getResources().getString(R.string.user_adapter_manager));
            } else
                holder.numberMeals.setText(mContext.getResources().getString(R.string.user_adapter_admin));
            holder.numberMeals.setBackgroundResource(R.drawable.full_transparent_yellow_stroke);
        } else {
            String numMeals = user.getNumberMeals() + " meal";
            if(user.getNumberMeals() == 0 || user.getNumberMeals() > 1)
                numMeals += "s";
            holder.numberMeals.setText(numMeals);
            holder.numberMeals.setBackground(null);
        }

        if(mSelectedItemsIds.get(position)){
            holder.parent.setBackgroundColor(mContext.getResources().getColor(R.color.foodie_yellow));
        } else {
            holder.parent.setBackgroundColor(mContext.getResources().getColor(R.color.foodie_yellow_dim));
        }

        return convertView;
    }

    public class ViewHolder {
        TextView username, numberMeals;
        RelativeLayout parent;
    }
}
