package com.example.myhabits;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>
        implements PopupMenu.OnMenuItemClickListener {
    List<HabitDisplayed> habitsList;
    Context context;
    DatabaseHelper databaseHelper;

    public RecyclerViewAdapter(List<HabitDisplayed> habitsList, Context context) {
        this.habitsList = habitsList;
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.one_habit_card, parent, false);
        MyViewHolder holder = new MyViewHolder(view); //tying the two together
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        //for cb's:
        holder.bind(position); //why do i need this?

        holder.tv_habitName.setText(habitsList.get(position).getName());
        holder.tv_habitDescription.setText(habitsList.get(position).getDescription());
        holder.cb_done.setChecked(habitsList.get(position).isChecked()); //setting the checkbox

        //for image, we use the *Glide library*:
        Glide.with(context).load(habitsList.get(position).getImageURL()).into(holder.iv_habitPicture);
        //try to replace this with a context menu (DELETE ITEM ACTION)
        holder.oneCardHabitLayout.setLongClickable(true); //for context menu on long click

        //comment this out once you use it!!
        holder.oneCardHabitLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(context, v); //context, anchor (view the popup will appear at)
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_habit:
                                //show a dialog:
                                String dialogTitle = context.getResources().getString(R.string.delete_item);
                                String deleteMsg = context.getResources().getString(R.string.delete_message);
                                String yesStr = context.getResources().getString(R.string.yes);
                                String noStr = context.getResources().getString(R.string.no);
                                new AlertDialog.Builder(context)
                                        .setTitle(dialogTitle) //Delete entry
                                        .setMessage(deleteMsg)
                                        .setPositiveButton(yesStr, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //delete from db -> delete from local list -> notify adapter about changes!
                                                databaseHelper.deleteOneHabit(habitsList.get(position));
                                                habitsList.remove(habitsList.get(position));
                                                notifyItemRemoved(position);

                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setNegativeButton(noStr, null)
                                        .setIcon(R.drawable.ic_warning_black) // icon doesn't show for some reason...
                                        .show();


                                return true;
                            case R.id.edit_habit:
                                Intent intent = new Intent(context, AddEditActivity.class);
                                intent.putExtra("id", habitsList.get(position).getId());
                                context.startActivity(intent);
                                return true;
                            case R.id.statistics:
                                Intent intent2 = new Intent(context, StatisticsActivity.class);
                                intent2.putExtra("id", habitsList.get(position).getId());
                                intent2.putExtra("name", habitsList.get(position).getName());
                                context.startActivity(intent2);
                                return true;
                            default:
                                return false;

                        } //end of switch
                    }
                }); //implemented below
                popup.inflate(R.menu.floating_menu_habit);
                popup.show();

                return true;
            } //end of OnLongClick
        }); //end of setting listener

        //trying to replace this with Context menu (EDIT HABIT)
        holder.oneCardHabitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddEditActivity.class);
                intent.putExtra("id", habitsList.get(position).getId());
                context.startActivity(intent);
            }
        });
        //checkboxes...
        holder.cb_done.setOnClickListener(new View.OnClickListener() { //implement in class to use for both CardView and Checkbox -> case1: case2: [logic] break; (NO BREAK BETWEEN THE TWO!)
            @Override
            public void onClick(View v) {

                if (!habitsList.get(position).isChecked()) { //changing attributes in habits:
                    habitsList.get(position).setChecked(true);
                    databaseHelper.addHabitDate(habitsList.get(position), MainActivity.getDisplayedDate());
                    //save changes in db
                }
                else {
                    habitsList.get(position).setChecked(false);
                    databaseHelper.deleteHabitDate(habitsList.get(position), MainActivity.getDisplayedDate());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return habitsList.size();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_habit:
                Toast.makeText(context, "Delete habit", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.edit_habit:
                Toast.makeText(context, "Edit habit", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.statistics:
                Toast.makeText(context, "Habit stats", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;

        } //end of switch
    }//end of onMenuItemClick


    //in adapter:
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{//, View.OnCreateContextMenuListener{ //make the ViewHolder fill set the Checkbox value!
        ImageView iv_habitPicture;
        TextView tv_habitName;
        TextView tv_habitDescription;
        CheckBox cb_done;
        ConstraintLayout oneCardHabitLayout;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_habitPicture=itemView.findViewById(R.id.iv_habitPicture);
            tv_habitName=itemView.findViewById(R.id.tv_habitName);
            tv_habitDescription=itemView.findViewById(R.id.tv_habitDescription);
            cb_done=itemView.findViewById(R.id.cb_done);
            oneCardHabitLayout = itemView.findViewById(R.id.oneCardHabitLayout);
            itemView.setOnClickListener(this);
        }

        void bind(int position){
            if(habitsList.get(position).isChecked()){ //checking the state of the model
                cb_done.setChecked(true);
            }else{
                cb_done.setChecked(false);
            }
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (!habitsList.get(adapterPosition).isChecked()) {
                cb_done.setChecked(true);
                habitsList.get(adapterPosition).setChecked(true);
                //add row in HabitDate db:
                databaseHelper.addHabitDate(habitsList.get(adapterPosition), MainActivity.getDisplayedDate());
            }
            else  {
                cb_done.setChecked(false);
                habitsList.get(adapterPosition).setChecked(false);
                //remove row from HabitDate db:
                databaseHelper.deleteHabitDate(habitsList.get(adapterPosition), MainActivity.getDisplayedDate());
            }
        }

    } //end of ViewHolder
}

