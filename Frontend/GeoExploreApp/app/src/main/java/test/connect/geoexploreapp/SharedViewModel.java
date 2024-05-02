package test.connect.geoexploreapp;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import test.connect.geoexploreapp.model.User;
// Passes the data among classes quickly
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Double> latitude = new MutableLiveData<>();
    private final MutableLiveData<Double> longitude = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isCreateEmergencyNotification = new MutableLiveData<>();
    private final MutableLiveData<User> loggedInUser = new MutableLiveData<>();
    private final MutableLiveData<User> creator = new MutableLiveData<>();


    public void setLocation(double lat, double lon) {
        latitude.setValue(lat);
        longitude.setValue(lon);
    }

    public LiveData<Double> getLatitude() {
        return latitude;
    }

    public LiveData<Double> getLongitude() {
        return longitude;
    }

    public void setCreateEmergencyNotification(boolean isCreate) {
        Log.d("SharedViewModel","" + isCreate);
        isCreateEmergencyNotification.setValue(isCreate);
    }

    public LiveData<Boolean> getCreateEmergencyNotification() {
        return isCreateEmergencyNotification;
    }

    public void setCreator(User user){
        creator.setValue(user);
    }

    public LiveData<User> getCreator(){
        return creator;
    }

    public void setLoggedInUser(User user){
        Log.d("Logged in User: ", String.valueOf(user));
        loggedInUser.setValue(user);
    }

    public LiveData<User> getLoggedInUser(){
        return loggedInUser;
    }


}
