package com.longx.intelligent.android.ichat2.data;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Date;

/**
 * Created by LONG on 2024/3/30 at 12:56 PM.
 */
@JsonAutoDetect(getterVisibility = NONE, fieldVisibility = ANY)
public class Self extends UserInfo{
    private final String ichatId;
    private final String ichatIdUser;
    private final String email;
    private final Date registerTime;
    private final String username;
    private final Avatar avatar;
    private final Integer sex;
    private final Region firstRegion;
    private final Region secondRegion;
    private final Region thirdRegion;
    private final UserProfileVisibility userProfileVisibility;
    private final WaysToFindMe waysToFindMe;

    public Self() {
        this(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public Self(String ichatId, String ichatIdUser, String email, Date registerTime, String username, Avatar avatar, Integer sex, Region firstRegion, Region secondRegion, Region thirdRegion, UserProfileVisibility userProfileVisibility, WaysToFindMe waysToFindMe) {
        this.ichatId = ichatId;
        this.ichatIdUser = ichatIdUser;
        this.email = email;
        this.registerTime = registerTime;
        this.username = username;
        this.avatar = avatar;
        this.sex = sex;
        this.firstRegion = firstRegion;
        this.secondRegion = secondRegion;
        this.thirdRegion = thirdRegion;
        this.userProfileVisibility = userProfileVisibility;
        this.waysToFindMe = waysToFindMe;
    }

    public String getIchatId() {
        return ichatId;
    }

    public String getIchatIdUser() {
        return ichatIdUser;
    }

    public String getEmail() {
        return email;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public String getUsername() {
        return username;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public Integer getSex() {
        return sex;
    }

    public Region getFirstRegion() {
        return firstRegion;
    }

    public Region getSecondRegion() {
        return secondRegion;
    }

    public Region getThirdRegion() {
        return thirdRegion;
    }

    public UserProfileVisibility getUserProfileVisibility() {
        return userProfileVisibility;
    }

    public WaysToFindMe getWaysToFindMe() {
        return waysToFindMe;
    }

    public Channel toChannel(){
        return new Channel(ichatId, ichatIdUser, email, username, null, avatar, sex, firstRegion, secondRegion, thirdRegion, true);
    }
}
