package com.danielogbuti.androideatitserver.ViewHolder;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielogbuti.androideatitserver.Interface.ItemClickListener;
import com.danielogbuti.androideatitserver.R;
import com.danielogbuti.androideatitserver.common.Common;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public ImageView menu_image;
    public TextView menu_name;
    private ItemClickListener ItemClickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);

        menu_image = (ImageView)itemView.findViewById(R.id.menu_image);
        menu_name = (TextView)itemView.findViewById(R.id.menu_name);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setOnClickListener(ItemClickListener itemClickListener){
        ItemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        ItemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");

        menu.add(0,0,getAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAdapterPosition(),Common.DELETE);
    }
}
