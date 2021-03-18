/*
Interface sa retrofit definicijama mrežnih poziva.
*/

package com.example.fightpanicnew.Network;

import com.example.fightpanicnew.Models.Response;
import com.example.fightpanicnew.Models.Room;
import com.example.fightpanicnew.Models.User;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitInterface {

    @POST("updateProfileInformation")
    Observable<Response> updateProfileInformation(@Body User user);

    @POST("registerUser")
    Observable<Response> register(@Body User user);

    @POST("authenticate")
    Observable<Response> login();

    @GET("getProfile/{email}")
    Observable<User> getProfile(@Path("email") String email);

    @GET("getUserEmail/{email}")
    Observable<Response> getExistingUserEmail(@Path("email") String email);

    @GET("getUsername/{username}")
    Observable<Response> getExistingUsername(@Path("username") String username);

    @PUT("changePassword/{email}")
    Observable<Response> changePassword(@Path("email") String email, @Body User user);

    @POST("resetPassword/{email}")
    Observable<Response> resetPasswordInit(@Path("email") String email);

    @POST("resetPassword/{email}")
    Observable<Response> resetPasswordFinish(@Path("email") String email, @Body User user);

    //Dio za chat
    @GET("rooms")
    Observable<List<Room>> getRooms();

    @POST("createNewRoom")
    Observable<Room> createNewRoom(@Body Room room);

    @GET("authorizeJoinRoom/{roomName}/{roomPassword}")
    Observable<Response> authorizeJoinRoom(@Path("roomName") String roomName, @Path("roomPassword") String roomPassword);

    @GET("getIfRoomAlive/{roomName}")
    Observable<Response> getIfRoomAlive(@Path("roomName") String roomName);
}
