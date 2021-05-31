package com.machina.siband.user.liveData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.machina.siband.user.model.Lantai
import java.util.*

class LantaiLiveData:
    LiveData<List<Lantai>>(){

}