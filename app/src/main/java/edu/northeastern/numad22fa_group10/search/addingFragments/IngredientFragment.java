package edu.northeastern.numad22fa_group10.search.addingFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import edu.northeastern.numad22fa_group10.R;


public class IngredientFragment extends Fragment {
    EditText ingredientName;
    EditText ingredientAmount;
    Button addIngredient;
    Button addBack;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ingredient, container, false);

        //add customized ingredient in the firebase
        ingredientName = view.findViewById(R.id.editTextIngredientName);
        ingredientAmount = view.findViewById(R.id.editTextCaloriesAmount2);
        addIngredient = (Button)view.findViewById(R.id.btn_add_ingredient);
        addBack = (Button)view.findViewById(R.id.btn_back);

        // find the current user
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        // get the current user id
        assert firebaseUser != null;
        String userid = firebaseUser.getUid();

        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ingredientName.getText().toString().matches("") || ingredientAmount
                        .getText().toString().matches("")) {
                    Toast.makeText(getContext(), "Missing ingredient name or calories", Toast.LENGTH_SHORT).show();
                }
                else if (!isNumeric(ingredientAmount.getText().toString())) {
                    Toast.makeText(getContext(), "Calorie entered is invalid", Toast.LENGTH_SHORT).show();
                }
                else{
                    FirebaseDatabase.getInstance().getReference("MyRecord").child(userid)
                            .child("ingredients").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //generate unique food id
                                    DatabaseReference dbReference = FirebaseDatabase.getInstance()
                                            .getReference("MyRecord").child(userid).child("ingredients");
                                    String foodId = dbReference.push().getKey();

                                    //add data into firebase by user input
                                    String foodName = ingredientName.getText().toString();
                                    double amount = Double.parseDouble(ingredientAmount.getText().toString());
                                    if (amount < 0) {
                                        Toast.makeText(getContext(), "Invalided input", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Map<String, Double> map = new HashMap<>();
                                        map.put(foodName, amount);
                                        dbReference = dbReference.child(foodId);
                                        dbReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Ingredient is added", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    Toast.makeText(getContext(), "Ingredient is not added", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }


            }
        });

        addBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getParentFragmentManager().getBackStackEntryCount() == 0) {
                    getActivity().finish();
                } else {
                    getParentFragmentManager().popBackStack();
                }
            }
        });
        return view;
    }



    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}