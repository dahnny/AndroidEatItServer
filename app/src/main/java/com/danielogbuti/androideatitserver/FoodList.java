package com.danielogbuti.androideatitserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.danielogbuti.androideatitserver.Interface.ItemClickListener;
import com.danielogbuti.androideatitserver.ViewHolder.FoodViewHolder;
import com.danielogbuti.androideatitserver.common.Common;
import com.danielogbuti.androideatitserver.model.Category;
import com.danielogbuti.androideatitserver.model.Food;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;

    Food newFood;

    FloatingActionButton fab;

    MaterialEditText editName,editDescription,editPrice,editDiscount;
    Button buttonSelect,buttonUpload;
    RelativeLayout relativeLayout;

    String categoryId = " ";

    Uri saveUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        recyclerView = (RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        relativeLayout = (RelativeLayout)findViewById(R.id.rootView);


        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        if (getIntent() != null){
            categoryId =  getIntent().getStringExtra("category");
        }
        if (!categoryId.isEmpty() && categoryId != null){
            loadFoodList(categoryId);
        }

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodDialog(categoryId);
            }
        });



    }

    private void showFoodDialog(String categoryId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Add new Food");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater  = this.getLayoutInflater();
        View add_menu_layout =  inflater.inflate(R.layout.add_new_food,null);

        editName = add_menu_layout.findViewById(R.id.editName);
        editDescription =add_menu_layout.findViewById(R.id.editDescription);
        editDiscount = add_menu_layout.findViewById(R.id.editDiscount);
        editPrice =add_menu_layout.findViewById(R.id.editPrice);

        buttonSelect = add_menu_layout.findViewById(R.id.buttonSelect);
        buttonUpload = add_menu_layout.findViewById(R.id.buttonUpload);

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (newFood != null){
                    //put the new category in the firebasedatabase
                    foodList.push().setValue(newFood);
                    Snackbar.make(relativeLayout,"New Food "+ newFood.getName() +" was added.",Snackbar.LENGTH_LONG).show();
                }
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE)){
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {
        foodList.child(key).removeValue();
    }

    private void showUpdateDialog(final String key, final Food item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater  = this.getLayoutInflater();
        View add_menu_layout =  inflater.inflate(R.layout.add_new_menu,null);

        editName = add_menu_layout.findViewById(R.id.editName);
        buttonSelect = add_menu_layout.findViewById(R.id.buttonSelect);
        buttonUpload = add_menu_layout.findViewById(R.id.buttonUpload);

        editName.setText(item.getName());

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setName(editName.getText().toString());
                foodList.child(key).setValue(item);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void changeImage(final Food item) {
        if (saveUri != null){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");
            dialog.show();

            String imageName = UUID.randomUUID().toString();
            //set the storage space/folder
            final StorageReference imageFolder = storageReference.child("images/"+ imageName);
            imageFolder.putFile(saveUri)
                    //display something when it is successful
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            //this line below makes sure we can get the link from the firebase
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //sets value for new category
                                    item.setImage(uri.toString());

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                //use this method when the image is being uploaded
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    dialog.setMessage("Uploaded "+ progress+"%");
                }
            });

        }
    }

    private void uploadImage() {
        if (saveUri != null){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");
            dialog.show();

            String imageName = UUID.randomUUID().toString();
            //set the storage space/folder
            final StorageReference imageFolder = storageReference.child("images/"+ imageName);
            imageFolder.putFile(saveUri)
                    //display something when it is successful
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            //this line below makes sure we can get the link from the firebase
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //sets value for new category
                                    newFood = new Food(editName.getText().toString()
                                            ,uri.toString(),editDescription.getText().toString(),
                                            editDiscount.getText().toString()
                                            ,editPrice.getText().toString()
                                            ,categoryId);

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                //use this method when the image is being uploaded
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    dialog.setMessage("Uploaded "+ progress+"%");
                }
            });

        }
    }

    private void chooseImage() {
        //open the gallery intent
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //get the image uri
        startActivityForResult(intent, Common.PICK_IMAGE_REQUEST);
    }

    private void loadFoodList(String categoryId) {
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodList.orderByChild("menuId").equalTo(categoryId),Food.class)
                .build();


       adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
           @Override
           protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
               holder.food_name.setText(model.getName());
               Picasso.with(getBaseContext()).load(model.getImage())
                       .into(holder.food_image);
               holder.setOnClickListener(new ItemClickListener() {
                   @Override
                   public void onClick(View view, int position, boolean isLongClick) {
                       //...
                   }
               });
           }

           @NonNull
           @Override
           public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
               return new FoodViewHolder(LayoutInflater.from(viewGroup.getContext())
                       .inflate(R.layout.food_item,viewGroup,false));
           }
       };
       adapter.notifyDataSetChanged();
       recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK  && data != null && data.getData() != null){
            saveUri = data.getData();
            buttonSelect.setText("Image Selected");
            buttonSelect.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
