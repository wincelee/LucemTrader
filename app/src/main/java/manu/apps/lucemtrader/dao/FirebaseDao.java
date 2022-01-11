package manu.apps.lucemtrader.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import manu.apps.lucemtrader.classes.Config;
import manu.apps.lucemtrader.classes.Crypto;
import manu.apps.lucemtrader.classes.Crypto2;
import manu.apps.lucemtrader.classes.Investor;


public class FirebaseDao {

    private final DatabaseReference rootDatabaseReference;

    private final DatabaseReference cryptoDatabaseReference;

    private final DatabaseReference investorsDatabaseReference;

    private final FirebaseUser firebaseUser;

    private final FirebaseStorage rootFirebaseStorage;


    public FirebaseDao() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        // using below method in the event of database region change
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Config.FIREBASE_INSTANCE_URL);

        rootDatabaseReference = firebaseDatabase.getReference();

        cryptoDatabaseReference = firebaseDatabase.getReference("Cryptos").child("ProfitLoss").child(returnUserId());

        investorsDatabaseReference = firebaseDatabase.getReference("Investors").child(returnUserId());

        rootFirebaseStorage = FirebaseStorage.getInstance();

    }


    public String returnUserId() {

        assert firebaseUser != null;
        return firebaseUser.getUid();

    }

    public String returnPhoneNo() {

        assert firebaseUser != null;
        return firebaseUser.getPhoneNumber();

    }

    public Task<Void> addInvestor(Investor investor) {

        return investorsDatabaseReference.push().setValue(investor);

    }

    public Task<Void> updateInvestor(String key, HashMap<String, Object> hashMap) {

        return investorsDatabaseReference.child(key).updateChildren(hashMap);

    }

    public Task<Void> removeInvestor(String key) {

        return investorsDatabaseReference.child(key).removeValue();

    }

    public FirebaseStorage returnRootFirebaseStorage(){

        return rootFirebaseStorage;

    }

    public DatabaseReference returnInvestorsDatabaseReference() {

        return investorsDatabaseReference;

    }

    public DatabaseReference rootDatabaseReference() {

        return rootDatabaseReference;

    }

    public List<Crypto> returnCryptoListWithValueEventListener() {

        List<Crypto> cryptoList = new ArrayList<>();

        cryptoDatabaseReference.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int i= 1;

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                    Crypto crypto = dataSnapshot.getValue(Crypto.class);

                    cryptoList.add(crypto);

                    Log.wtf("CryptoListWithValueEventListener", i++ + ":" + "\n" + new Gson().toJson(cryptoList));

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return cryptoList;
    }


    public List<Investor> returnInvestorsListWithValueEventListener() {

        List<Investor> investorList = new ArrayList<>();

        investorsDatabaseReference.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                    Investor investor = dataSnapshot.getValue(Investor.class);

                    investorList.add(investor);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return investorList;
    }

    public List<Crypto2> returnCryptoListWithQuery() {

        List<Crypto2> cryptoList = new ArrayList<>();

        Query query = rootDatabaseReference.child("Cryptos").child("ProfitLoss").child(returnUserId());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i = 1;

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Crypto2 crypto = snapshot.getValue(Crypto2.class);

                    if (crypto != null) {

                        crypto.setId(snapshot.getKey());

                    }

                    cryptoList.add(crypto);

                    Log.wtf("CryptoListWithQuery", i++ + ":" + "\n" + new Gson().toJson(cryptoList));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return cryptoList;
    }


    public List<Investor> returnInvestorsListWithQuery() {

        List<Investor> investorList = new ArrayList<>();

        Query query = rootDatabaseReference.child("Investors").child(returnUserId());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Investor investor = snapshot.getValue(Investor.class);

                    investorList.add(investor);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return investorList;
    }


}
